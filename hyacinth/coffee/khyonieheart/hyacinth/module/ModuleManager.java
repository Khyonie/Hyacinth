package coffee.khyonieheart.hyacinth.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.crafthyacinth.module.HyacinthModuleManager;
import coffee.khyonieheart.crafthyacinth.module.provider.HyacinthProviderPrimer;
import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.exception.HyacinthModuleException;
import coffee.khyonieheart.hyacinth.exception.InstantiationRuntimeException;
import coffee.khyonieheart.hyacinth.exception.InvalidModuleClassException;
import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

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
    public HyacinthModule loadModule(@NotNull File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException, HyacinthModuleException;

    /**
     * Attempts to obtain a loaded module instance with the given name.
     * @param moduleName Name of module to obtain.
     * @eden.optional {@link coffee.khyonieheart.hyacinth.module.HyacinthModule}
     * @return See optional.
     */
    public Option getModule(String moduleName);

	public void addModule(HyacinthModule module, YamlConfiguration configuration);

    public Class<?> getGlobalClass(String name, ClassLoader accessor);

    /**
     * Obtains a module manager instance from a provider source.
     * @param sourcePath Provider source path (<Source>/<Fully qualified class>, where "Source" translates to file <Source>.jar inside providers/modules/)
     * // TODO Fix broken comment format
     * @return A module manager.
     * @throws FileNotFoundException Provider file does not exist.
     * @throws HyacinthModuleException Provider failed to be loaded or is in an invalid format.
     */
    public static ModuleManager obtainModuleManager(
        @NotNull String sourcePath
    )
        throws FileNotFoundException,
            HyacinthModuleException
    {
        Logger.verbose("Sourcing module manager from \"" + sourcePath + "\"");
        
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
                Class<?> potentialProviderClass = Class.forName(classPath, false, Hyacinth.getClassloader());

                if (potentialProviderClass == null)
                {
                    throw new NullPointerException();
                }

                providerClass = potentialProviderClass.asSubclass(ModuleManager.class);
            } catch (ClassNotFoundException | NullPointerException e) {
                Logger.log("§cFailed to find internal module manager \"" + classPath + "\", falling back to Hyacinth module manager");
                providerClass = HyacinthModuleManager.class;
            } catch (ClassCastException e) {
                Logger.log("§cPotential module manager provider \"" + classPath + "\" is not a valid ModuleManager, falling back to Hyacinth module manager");
                providerClass = HyacinthModuleManager.class;

                e.printStackTrace();
            }

            try {
                manager = (ModuleManager) Reflect.simpleInstantiate(providerClass);

                return manager;
            } catch (InstantiationRuntimeException e) {
                Logger.log("§cFailed to instantiate module manager provider class \"" + providerClass.getName() + "\"");
                e.printStackTrace();
            }

            return new HyacinthModuleManager();
        }

        Logger.verbose("Sourcing external module manager");

        File providerSource = providerFileWithName(source);
        if (!providerSource.exists())
        {
            throw new FileNotFoundException("Proposed module manager file \"" + source + ".jar\" does not exist");
        }

        HyacinthProviderPrimer primer = new HyacinthProviderPrimer();

        try {
            primer.loadModule(providerSource);
            
            providerClass = primer.getGlobalClass(classPath, Hyacinth.getClassloader());

            if (providerClass == null)
            {
                throw new HyacinthModuleException("Proposed external module manager class \"" + classPath + "\" does not exist");
            }
        } catch (IllegalArgumentException | IOException | HyacinthModuleException e) {
            Logger.log("§cFailed to locate module manager provider class \"" + classPath + "\", falling back to Hyacinth module manager");
            e.printStackTrace();

            return new HyacinthModuleManager();
        }

        try {
            manager = (ModuleManager) Reflect.simpleInstantiate(providerClass);

            Logger.verbose("Instantiated new module manager " + manager.getClass().getName());

            return manager;
        } catch (InstantiationRuntimeException e) {
            Logger.log("§cFailed to instantiate external module manager provider class \"" + providerClass.getName() + "\", falling back to Hyacinth module manager");
            e.printStackTrace();

            return new HyacinthModuleManager();
        }
    }

    public static File moduleFileWithName(String name)
    {
        return new File("./Hyacinth/modules/" + name + ".jar");
    }

    public static File providerFileWithName(String name)
    {
        return new File("./Hyacinth/providers/modules/" + name + ".jar");
    }
}
