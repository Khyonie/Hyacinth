package coffee.khyonieheart.craftorigami.module.provider;

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

import coffee.khyonieheart.origami.Logger;
import coffee.khyonieheart.origami.exception.OrigamiModuleException;
import coffee.khyonieheart.origami.module.ModuleManager;
import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.option.Option;
import coffee.khyonieheart.origami.util.YamlUtils;

/**
 * Module manager implementation that acts as the first link in the chainloading of a module manager provider
 */
public class OrigamiProviderPrimer implements ModuleManager
{
    private Map<String, Class<?>> loadedClasses = new HashMap<>();
    private String classSource;

    public OrigamiProviderPrimer(String classSource)
    {
        this.classSource = classSource;
    }

    @Override
    public OrigamiModule loadModule(File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException, OrigamiModuleException 
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
            throw new OrigamiModuleException("Provider file " + moduleFile.getName() + " does not contain a valid mod.yml, missing keys: " + Arrays.toString(missingKeys.toArray(new String[missingKeys.size()])));
        }

        Logger.verbose(moduleFile.getName() + "'s mod.yml passed verification");

        OrigamiProviderClassloader mpcl = new OrigamiProviderClassloader(moduleFile, this.getClass().getClassLoader(), this);

        jar.close();
        return null;
    }

    @Override
    public Option getModule(String moduleName) 
    {
        return null;
    }

    @Override
    public Class<?> getGlobalClass(String name, ClassLoader accessor) 
    {
        return loadedClasses.get(name);
    }
}
