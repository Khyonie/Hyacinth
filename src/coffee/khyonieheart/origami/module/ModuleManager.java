package coffee.khyonieheart.origami.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import coffee.khyonieheart.craftorigami.module.OrigamiModuleManager;
import coffee.khyonieheart.origami.Logger;
import coffee.khyonieheart.origami.Origami;
import coffee.khyonieheart.origami.exception.InstantiationRuntimeException;
import coffee.khyonieheart.origami.exception.InvalidModuleClassException;
import coffee.khyonieheart.origami.exception.OrigamiModuleException;
import coffee.khyonieheart.origami.module.marker.PreventAutoLoad;
import coffee.khyonieheart.origami.option.Option;
import coffee.khyonieheart.origami.util.Reflect;
import coffee.khyonieheart.origami.util.marker.NotNull;

/**
 * Module manager interface, used to load and unload modules from .jar files into or out of memory.
 * Implementations of module managers which automatically register commands and listeners should always respect {@link PreventAutoLoad} annotations.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public interface ModuleManager 
{
    /**
     * Attempts to load a module into memory. This method should never return null, and implementations should always throw an exception rather than return as such.
     * @param moduleFile A file containing a module.
     * @return A module at the given location.
     * @throws FileNotFoundException The file does not exist.
     * @throws IllegalArgumentException The moduleFile object is null.
     * @throws IOException The file could not be accessed.
     * @throws InvalidModuleClassException The proposed module class is not valid.
     */ 
    @NotNull
    public OrigamiModule loadModule(@NotNull File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException, OrigamiModuleException;

    /**
     * Attempts to obtain a loaded module instance with the given name.
     * @param moduleName Name of module to obtain.
     * @eden.optional {@link coffee.khyonieheart.origami.module.OrigamiModule}
     * @return See optional.
     */
    public Option getModule(String moduleName);

    public Class<?> getGlobalClass(String name, ClassLoader accessor);

    public static ModuleManager obtainModuleManager(
        @NotNull String sourcePath
    ) {
        String[] splitData = sourcePath.split("/");

        if (splitData.length != 2)
        {
            Logger.log("§cInvalid module manager source: \"" + sourcePath + "\"");
            return null;
        }

        String source = splitData[0].replace("\\", "");
        String classPath = splitData[1];

        Class<?> providerClass;
        ModuleManager manager;

        if (source.equals("internal"))
        {
            Logger.verbose("Sourcing internal module manager");

            try {
                Class<?> potentialProviderClass = Class.forName(classPath, false, Origami.getClassloader());

                if (potentialProviderClass == null)
                {
                    throw new NullPointerException();
                }

                providerClass = ModuleManager.class.asSubclass(potentialProviderClass);
            } catch (ClassNotFoundException | NullPointerException e) {
                Logger.log("§cFailed to find internal module manager \"" + classPath + "\", falling back to Origami module manager");
                providerClass = OrigamiModuleManager.class;
            } catch (ClassCastException e) {
                Logger.log("§cPotential module manager provider \"" + classPath + "\" is not a valid ModuleManager, falling back to Origami module manager");
                providerClass = OrigamiModuleManager.class;
            }

            try {
                manager = (ModuleManager) Reflect.simpleInstantiate(providerClass);

                return manager;
            } catch (InstantiationRuntimeException e) {
                Logger.log("§cFailed to instantiate module manager provider class \"" + providerClass.getName() + "\"");
                e.printStackTrace();
            }

            return new OrigamiModuleManager();
        }

        

        return manager;
    }

    public static File moduleFileWithName(String name)
    {
        return new File("plugins/Origami/modules/" + name + ".jar");
    }

    public static File providerFileWithName(String name)
    {
        return new File("plugins/Origami/providers/modules/" + name + ".jar");
    }
}
