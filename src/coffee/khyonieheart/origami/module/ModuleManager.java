package coffee.khyonieheart.origami.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import coffee.khyonieheart.origami.option.Option;
import coffee.khyonieheart.origami.util.marker.NotNull;

public interface ModuleManager 
{
    /**
     * Attempts to load a module into memory.
     * @return A module at the given location.
     * @throws FileNotFoundException The file does not exist.
     * @throws IllegalArgumentException The moduleFile object is null.
     * @throws IOException The file could not be accessed.
     */
    public OrigamiModule loadModule(@NotNull File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException;

    /**
     * Attempts to obtain a loaded module instance with the given name.
     * @param moduleName Name of module to obtain.
     * @eden.optional {@link coffee.khyonieheart.origami.module.OrigamiModule}
     * @return See optional.
     */
    public Option getModule(String moduleName);

    public Class<?> getGlobalClass(String name, ClassLoader accessor);
}
