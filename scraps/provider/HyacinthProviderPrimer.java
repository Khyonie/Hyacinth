package coffee.khyonieheart.crafthyacinth.module.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.exception.HyacinthModuleException;
import coffee.khyonieheart.hyacinth.module.ModuleManager;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.testing.UnitTestResult;
import coffee.khyonieheart.hyacinth.testing.UnitTestable;
import coffee.khyonieheart.hyacinth.util.YamlUtils;

/**
 * Module manager implementation that acts as the first link in the chainloading of a module manager provider.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class HyacinthProviderPrimer implements ModuleManager, UnitTestable
{
    private Map<String, HyacinthProviderClassloader> classloaders = new HashMap<>();
    private Map<String, Class<?>> loadedClasses = new HashMap<>();
    private HyacinthModule associatedLibrary;

    @Override
    public HyacinthModule loadModule(File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException, HyacinthModuleException 
    {
        Objects.requireNonNull(moduleFile);

        if (!moduleFile.exists())
        {
            throw new FileNotFoundException("Provider file does not exist");
        }

        JarFile jar = new JarFile(moduleFile);

        // Load and verify provider.yml
        YamlConfiguration moduleConfig = new YamlConfiguration();

        try {
            moduleConfig.load(new InputStreamReader(jar.getInputStream(jar.getEntry("provider.yml"))));
        } catch (InvalidConfigurationException e) {
            jar.close();
            throw new IOException("provider.yml for file " + moduleFile.getName() + " is invalid", e);
        }

        Logger.verbose("Verifying provider.yml for file " + moduleFile.getName());

        if (!YamlUtils.containsAll(moduleConfig, "name", "author", "version"))
        {
            jar.close();

            List<String> missingKeys = YamlUtils.getMissingKeys(moduleConfig, "name", "description", "author", "version");
            throw new HyacinthModuleException("Provider file " + moduleFile.getName() + " does not contain a valid provider.yml, missing keys: " + Arrays.toString(missingKeys.toArray(new String[missingKeys.size()])));
        }

        Logger.verbose(moduleFile.getName() + "'s provider.yml passed verification");

        classloaders.put(moduleFile.getName(), new HyacinthProviderClassloader(moduleFile, this.getClass().getClassLoader(), this));
        associatedLibrary = HyacinthLibraryModule.create(moduleConfig, jar);

        return associatedLibrary;
    }

    @Override
    public Option getModule(String moduleName) 
    {
        return Option.none();
    }

	@Override
	public List<? extends HyacinthModule> getLoadedModules()
	{
		throw new UnsupportedOperationException("Primer does not contain modules");
	}

    @Override
    public Class<?> getGlobalClass(String name, ClassLoader accessor) 
    {
        if (loadedClasses.containsKey(name))
        {
            return loadedClasses.get(name);
        }

        Class<?> buffer;
        for (HyacinthProviderClassloader loader : classloaders.values())
        {
            try {
                buffer = loader.findClass(name, false);
                loadedClasses.put(name, buffer);

                return buffer;
            } catch (ClassNotFoundException e) {
                continue;
            }
        }

        return null;
    }

    @Override
    public List<UnitTestResult> test() 
    {
       	// TODO This 
        return null;
    }

	@Override
	public void addModule(HyacinthModule module, YamlConfiguration configuration) 
	{
		throw new UnsupportedOperationException("Cannot add a module to a primer");	
	}

	@Override
	public YamlConfiguration getConfiguration(HyacinthModule module) 
	{
		throw new UnsupportedOperationException("Primer does not store module configurations");
	}

	@Override
	public YamlConfiguration getConfiguration(
		Class<? extends HyacinthModule> module
	) {
		throw new UnsupportedOperationException("Primer does not store module configurations");
	}
}
