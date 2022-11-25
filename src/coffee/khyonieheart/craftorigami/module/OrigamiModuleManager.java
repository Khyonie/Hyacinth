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
 * Official implementation of a module manager.
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
        Class<?> moduleClass;

        if (!moduleConfig.contains("entry"))
        {
            List<Class<? extends OrigamiModule>> moduleClasses = pmcl.collectSubclasses(OrigamiModule.class);

            if (moduleClasses.size() == 0)
            {
                Logger.verbose("Module " + moduleConfig.getString("name") + " does not contain any module classes. Loading as library...");
                Logger.todo("Create cookie cutter library class");
            }
    
            if (moduleClasses.size() > 1)
            {
                if (!moduleConfig.contains("entry"))
                {
                    Logger.log("Module " + moduleConfig.getString("name") + " contains multiple module classes, but does not specify an entry point.");
                    Logger.log("This can be solved by adding an \"entry\" key to your mod.yml, containing the fully qualified path for the desired class.");
                    return null;
                }
            }
        } else {
            try {
                moduleClass = pmcl.findClass(moduleConfig.getString("entry"), false);

                if (!OrigamiModule.class.isAssignableFrom(moduleClass))
                {
                    Logger.log("Module " + moduleConfig.getString("name") + " declares a module class, but the found class is not an Origami module class.");
                    Logger.log("This can be solved by adding \"implements OrigamiModule\" to the class " + moduleClass.getName());
                }
            } catch (ClassNotFoundException e) {
                
            }            
        }

        
        return null;
    }

    private Class<?> locateModule(YamlConfiguration moduleConfig, JarFile jar, OrigamiModuleClassloader omcl, boolean skipEntry)
    {
        Class<?> target;

        if (moduleConfig.contains("entry") && !skipEntry)
        {
            try {
                target = omcl.findClass(moduleConfig.getString("entry"));
            } catch (ClassNotFoundException e) {
                Logger.log("Module " + moduleConfig.getString("name") + " declares a module class, but such a class could not be found.");
                return locateModule(moduleConfig, jar, omcl, true); // Recurse
            }

            if (target != null)
            {
                return target;
            }

            Logger.verbose("An error occurred while attempting to load entry class \"" + moduleConfig.getString("entry") + "\" of module " + moduleConfig.getString("name") + ". Attempting to load without entry...");
            return locateModule(moduleConfig, jar, omcl, true); // Recurse
        }

        List<Class<? extends OrigamiModule>> collected = omcl.collectSubclasses(OrigamiModule.class);
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
