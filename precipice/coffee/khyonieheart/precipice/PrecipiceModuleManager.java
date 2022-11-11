package coffee.khyonieheart.precipice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.craftorigami.module.OrigamiModuleClassloader;
import coffee.khyonieheart.origami.Logger;
import coffee.khyonieheart.origami.Origami;
import coffee.khyonieheart.origami.enums.ConfigurationType;
import coffee.khyonieheart.origami.module.ModuleManager;
import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.option.Option;
import coffee.khyonieheart.origami.util.marker.NotNull;

public class PrecipiceModuleManager implements ModuleManager
{
    private Map<String, OrigamiModuleClassloader> activeClassloaders = new HashMap<>();
    private Map<String, Class<?>> cachedClasses = new HashMap<>();

    @Override
    public OrigamiModule loadModule(@NotNull File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException
    {
        if (moduleFile == null)
            throw new NullPointerException("Module file cannot be null");
        if (!moduleFile.exists())
            throw new FileNotFoundException("Module file does not exist");

        JarFile jar = new JarFile(moduleFile);
        YamlConfiguration moduleConfig = new YamlConfiguration();

        try {
            moduleConfig.load(new InputStreamReader(jar.getInputStream(jar.getEntry("mod.yml"))));
        } catch (InvalidConfigurationException e) {
            jar.close();
            throw new IOException("mod.yml for file " + moduleFile.getName() + " is invalid", e);
        }

        // Assign a classloader, load module

        OrigamiModuleClassloader pmcl = OrigamiModuleClassloader.create(moduleFile, jar);
        OrigamiModule module;

        if (moduleConfig.contains("entry"))
        {
            try {
                Class<?> clazz = pmcl.findClass(moduleConfig.getString("entry"), false);

                module = (OrigamiModule) clazz.getConstructor().newInstance();

                return module;
            } catch (ClassNotFoundException | ClassCastException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
                return null;
            }
        }

        // TODO Anything below this does not work
        List<Class<? extends OrigamiModule>> moduleClasses = pmcl.collectSubclasses(OrigamiModule.class);

        if (moduleClasses.size() == 0)
        {
            Logger.verbose("Module " + moduleConfig.getString("name") + " does not contain any module classes. Loading as library...");
            // TODO Create cookie-cutter library class
        }

        if (moduleClasses.size() > 1)
        {
            if (!moduleConfig.contains("entry"))
            {
                Logger.log("Module" + moduleConfig.getString("name") + " contains multiple module classes, but does not specify an entry point.");
                Logger.log("This can be solved by adding an \"entry\" key to your mod.yml, containing the fully qualified path for the desired class.");
                return null;
            }

            Option opt = Origami.getConfig("disallowMultipleInnerModuleClasses", ConfigurationType.BOOLEAN);
            switch (opt.getState())
            {
                case SOME:
                    if (opt.unwrap(Boolean.class))
                        return null;
                default: break;
            }

            Logger.verbose("Choosing first loaded module class, being \"" + moduleClasses.get(0).getName() + "\"");
            moduleClasses = List.of(moduleClasses.get(0));
        }

        OrigamiModule finalModule;
        
        try {
            finalModule = moduleClasses.get(0).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }

        activeClassloaders.put(moduleFile.getName(), pmcl);
        
        return finalModule;
    }

    @Override
    public Option getModule(String moduleName) 
    {
        return null;
    }

    @Override
    public Class<?> getGlobalClass(String name, ClassLoader accessor) // TODO Write accessor checker
    {
        Class<?> result = cachedClasses.get(name);

        if (result != null)
            return result;

        // Check all classloaders
        for (OrigamiModuleClassloader classLoader : activeClassloaders.values())
        {
            try {
                result = classLoader.findClass(name, false);

                if (result != null)
                {
                    cachedClasses.put(name, result);
                    return result;
                }
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        
        return null;
    }
    
}
