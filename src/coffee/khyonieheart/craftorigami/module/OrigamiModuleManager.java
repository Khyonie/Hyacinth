package coffee.khyonieheart.craftorigami.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.origami.Logger;
import coffee.khyonieheart.origami.module.ModuleManager;
import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.option.Option;
import coffee.khyonieheart.origami.util.marker.NotNull;

/**
 * Default implementation of a module manager.
 * @since 1.0.0
 */
public class OrigamiModuleManager implements ModuleManager
{
    private Map<String, OrigamiModuleClassloader> activeClassloaders = new HashMap<>();
    private Map<String, Class<?>> cachedClasses = new HashMap<>();

    @Override
    public OrigamiModule loadModule(@NotNull File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException
    {
        if (moduleFile == null)
            throw new IllegalArgumentException("Module file cannot be null");
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

            
        }
        
        return null;
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
                    return result;
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
        
        return null;
    }
    
}
