package coffee.khyonieheart.crafthyacinth.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import coffee.khyonieheart.crafthyacinth.event.HyacinthListenerManager;
import coffee.khyonieheart.crafthyacinth.killswitch.KillswitchManager;
import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.command.HyacinthCommand;
import coffee.khyonieheart.hyacinth.exception.DependenciesNotMetException;
import coffee.khyonieheart.hyacinth.exception.HyacinthModuleException;
import coffee.khyonieheart.hyacinth.exception.InstantiationRuntimeException;
import coffee.khyonieheart.hyacinth.exception.InvalidModuleClassException;
import coffee.khyonieheart.hyacinth.killswitch.KillswitchTarget;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.ModuleManager;
import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;
import coffee.khyonieheart.hyacinth.module.provider.Chainloadable;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.print.Grammar;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.hyacinth.util.YamlUtils;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Internal implementation of a module manager.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class HyacinthModuleManager implements ModuleManager, Chainloadable
{
    private Map<String, HyacinthModuleClassloader> activeClassloaders = new HashMap<>();
    private Map<String, Class<?>> cachedClasses = new HashMap<>();

    private Map<String, HyacinthModule> loadedModules = new HashMap<>();
	private Map<HyacinthModule, YamlConfiguration> loadedConfigurations = new HashMap<>();

    /**
     * @implNote This implementation may additionally throw an {@link InstantiationRuntimeException}.
     */
    @Override
	@SuppressWarnings("unchecked")
    public HyacinthModule loadModule(@NotNull File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException, HyacinthModuleException
    {
        if (moduleFile == null)
            throw new IllegalArgumentException("Module file cannot be null");
        if (!moduleFile.exists())
            throw new FileNotFoundException("Module file does not exist");

        JarFile jar = new JarFile(moduleFile);

        // Load and verify mod.yml
        YamlConfiguration moduleConfig = new YamlConfiguration();

        try {
            moduleConfig.load(new InputStreamReader(jar.getInputStream(jar.getEntry("mod.yml"))));
        } catch (InvalidConfigurationException e) {
            jar.close();
            throw new IOException("mod.yml for file " + moduleFile.getName() + " is invalid", e);
        }

        Logger.verbose("Verifying mod.yml for file " + moduleFile.getName());

        if (!YamlUtils.containsAll(moduleConfig, "name", "description", "author", "version"))
        {
            jar.close();

            List<String> missingKeys = YamlUtils.getMissingKeys(moduleConfig, "name", "description", "author", "version");
            throw new HyacinthModuleException("Module file " + moduleFile.getName() + " does not contain a valid mod.yml, missing keys: " + Arrays.toString(missingKeys.toArray(new String[missingKeys.size()])));
        }

        Logger.verbose(moduleFile.getName() + "'s mod.yml passed verification");

		if (loadedModules.containsKey(moduleConfig.getString("name")))
		{
			jar.close();
			return loadedModules.get(moduleConfig.getString("name"));
		}

        // Assign a classloader, load module

        HyacinthModuleClassloader hmcl = HyacinthModuleClassloader.create(moduleFile, jar);
        activeClassloaders.put(moduleFile.getName(), hmcl);

        Logger.verbose("Registered new Hyacinth classloader " + moduleFile.getName() + "/" + moduleConfig.getString("name") + " (" + activeClassloaders.size() + " registered)");

        // Attempt to load dependencies
        if (moduleConfig.contains("requires"))
        {
            Logger.verbose("Handling dependencies of " + moduleFile.getName() + "/" + moduleConfig.getString("name"));

            List<String> requires = moduleConfig.getStringList("requires");
            List<String> missingDependencies = new ArrayList<>();

            for (String dependency : requires)
            {
                Logger.todo("Handle circular dependency recursion crash");

                if (dependency.equals(moduleConfig.get("name")))
                {
                    Logger.verbose("§eModule " + moduleFile.getName() + "/" + moduleConfig.getString("name") + " lists itself as a dependency! Ignoring");
                    continue;
                }

                if (activeClassloaders.containsKey(dependency + ".jar"))
                {
                    continue;
                }

                File targetFile = ModuleManager.moduleFileWithName(dependency);

                if (!targetFile.exists())
                {
                    missingDependencies.add(dependency);
                    Logger.verbose("Module " + moduleConfig.getString("name") + " is missing dependency " + dependency + ", will complain about it later");
                    continue;
                }

                try {
                    loadModule(ModuleManager.moduleFileWithName(dependency));
                } catch (Exception e) {
                    Logger.verbose("§cDependency " + dependency + " of module " + moduleConfig.getName() + " failed to load with exception: " + e.getClass().getSimpleName());
                    missingDependencies.add(dependency);

                    continue;
                }
            }

            if (!missingDependencies.isEmpty())
            {
                Logger.log("§cModule" + moduleFile.getName() + "/" + moduleConfig.getString("name") + " is missing the following " + missingDependencies.size() + Grammar.plural(missingDependencies.size(), " dependency:", " dependencies:"));

                for (String missingDep : missingDependencies)
                {
                    Logger.log("§c - " + missingDep);
                }

                throw new DependenciesNotMetException();
            }

            Logger.log("§aSuccessfully loaded dependencies for " + moduleFile.getName() + "/" + moduleConfig.getString("name"));
        }

        // Instantiate

        Class<?> moduleClass = this.locateModule(moduleConfig, jar, hmcl, false);

        if (moduleClass == null)
            throw new InvalidModuleClassException("Module class for file \"" + moduleFile.getName() + "\" came up null");

        if (!HyacinthModule.class.isAssignableFrom(moduleClass))
             throw new InvalidModuleClassException("Class \"" + moduleClass.getName() + "\" does not implement HyacinthModule");
        
        HyacinthModule module = (HyacinthModule) Reflect.simpleInstantiate(moduleClass); // See @implNote

        // Register

        Map<Class<?>, List<Class<?>>> toRegister = hmcl.collectMultiSubclasses(HyacinthCommand.class, Listener.class, KillswitchTarget.class);
        toRegister.forEach((clazz, collected) -> {
            collected.removeIf((collectedClass) -> collectedClass.isAnnotationPresent(PreventAutoLoad.class));
        });

        int commandCount = toRegister.get(HyacinthCommand.class).size();
        int listenerCount = toRegister.get(Listener.class).size();

        Logger.verbose("Registering " + commandCount + Grammar.plural(commandCount, " command and ", " commands and ") + listenerCount + Grammar.plural(listenerCount, " listener to ", " listeners to ") + moduleFile.getName() + "/" + moduleConfig.getString("name"));

        boolean encounteredErrors = false;

        // Commands
        for (Class<?> commandClass : toRegister.get(HyacinthCommand.class))
        {
			Logger.verbose(" - Registering command " + commandClass.getName());
            HyacinthCommand command;

            try {
                command = (HyacinthCommand) Reflect.simpleInstantiate(commandClass);
            } catch (InstantiationRuntimeException e) {
                encounteredErrors = true;

                Logger.verbose("§cFailed to load command " + commandClass.getName() + " with exception " + e.getCause().getClass().getSimpleName());

                if (Hyacinth.getConfig("enableVerboseLogging", Boolean.class))
                {
                    e.printStackTrace();
                }

                continue;
            }

            Hyacinth.getCommandManager().register(command.getName(), command, Hyacinth.getInstance().getServer());
        }

        // Listeners
        for (Class<?> listenerClass : toRegister.get(Listener.class))
        {
			Logger.verbose(" - Registering listener " + listenerClass.getName());
            Listener listener;

            try {
                listener = (Listener) Reflect.simpleInstantiate(listenerClass);
            } catch (InstantiationRuntimeException e) {
                encounteredErrors = true;

                Logger.verbose("§cFailed to load listener " + listenerClass.getName() + " with exception " + e.getCause().getClass().getSimpleName());

                if (Hyacinth.getConfig("enableVerboseLogging", Boolean.class))
                {
                    e.printStackTrace();
                }

                continue;
            }

            HyacinthListenerManager.register(module.getClass(), listener);
        }

		// Killswitch targets
		for (Class<?> featureClass : toRegister.get(KillswitchTarget.class))
		{
			Logger.verbose(" - Registering feature " + featureClass.getName());
			KillswitchTarget target;

			try {
				target = (KillswitchTarget) Reflect.simpleInstantiate(featureClass);
			} catch (InstantiationRuntimeException e) {
				encounteredErrors = true;

				Logger.verbose("§cFailed to register toggle-able feature " + featureClass.getName() + " with exception " + e.getCause().getClass().getSimpleName());

				if (Hyacinth.getConfig("enableVerboseLogging", Boolean.class))
				{
					e.printStackTrace();
				}

				continue;
			}

			KillswitchManager.register((Class<? extends HyacinthModule>) moduleClass, target);
		}

        // Finished

        loadedModules.put(moduleConfig.getString("name"), module);
		loadedConfigurations.put(module, moduleConfig);

        if (!encounteredErrors)
        {
            Logger.log("§aFinished loading " + moduleFile.getName() + "/" + moduleConfig.getString("name"));
            return module;
        }

        Logger.log("§cFinished loading " + moduleFile.getName() + "/" + moduleConfig.getString("name") + " with registration errors");

        if (!Hyacinth.getConfig("enableVerboseLogging", Boolean.class))
        {
            Logger.log("§c - Enable verbose logging for more details and stacktraces");
        }

        return module;
    }

	@Override
	public YamlConfiguration getConfiguration(HyacinthModule module)
	{
		return this.loadedConfigurations.get(module);
	}

    private Class<?> locateModule(YamlConfiguration moduleConfig, JarFile jar, HyacinthModuleClassloader hmcl, boolean skipEntry)
    {
        Class<?> target;

        if (moduleConfig.contains("entry") && !skipEntry)
        {
            try {
                target = hmcl.findClass(moduleConfig.getString("entry"));
            } catch (ClassNotFoundException e) {
                Logger.log("Module " + moduleConfig.getString("name") + " declares a module class, but such a class could not be found.");
                return locateModule(moduleConfig, jar, hmcl, true); // Recurse
            }

            if (target != null)
            {
                return target;
            }

            Logger.verbose("An error occurred while attempting to load entry class \"" + moduleConfig.getString("entry") + "\" of module " + moduleConfig.getString("name") + ". Attempting to load without entry");
            return locateModule(moduleConfig, jar, hmcl, true); // Recurse
        }

        List<Class<? extends HyacinthModule>> collected = hmcl.collectSubclasses(HyacinthModule.class);

        if (collected.size() == 0)
        {
            Logger.verbose("Module " + moduleConfig.getString("name") + " does not contain any module classes. Loading as library");
            Logger.todo("Create cookie cutter library class");
            return null;
        }

        if (collected.size() > 1)
        {
            Logger.log("Module " + moduleConfig.getString("name") + " contains multiple module classes, but does not specify an entry point.");
            Logger.log("This can be solved by adding an \"entry\" key to your mod.yml, containing the fully qualified path for the desired class.");
            return null;
        }

        return collected.get(0);
    }

    @Override
    public Option getModule(String moduleName) 
    {
        return loadedModules.containsKey(moduleName) ? Option.some(loadedModules.get(moduleName)) : Option.none();
    }

	@Override
	public List<? extends HyacinthModule> getLoadedModules()
	{
		return new ArrayList<>(this.loadedModules.values());
	}

    @Override
    public Class<?> getGlobalClass(String name, ClassLoader accessor) // TODO Write accessor checker
    {
        Class<?> result = cachedClasses.get(name);

        if (result != null)
            return result;

        // Check all classloaders
        for (HyacinthModuleClassloader classLoader : activeClassloaders.values())
        {
            try {
                result = classLoader.findClass(name, false);

                if (result != null)
                    return result;
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        
        return null;
    }

    @Override
    public void transfer(Map<String, Class<?>> loadedClasses) 
    {
        this.cachedClasses.putAll(loadedClasses);
    }

	@Override
	public void addModule(HyacinthModule module, YamlConfiguration configuration) 
	{
		this.loadedModules.put(configuration.getString("name"), module);
		this.loadedConfigurations.put(module, configuration);
	}
}
