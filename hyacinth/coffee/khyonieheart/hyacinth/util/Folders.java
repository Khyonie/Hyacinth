package coffee.khyonieheart.hyacinth.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Various utilities for manipulating folders
 * @author Khyonie
 * @since 1.0.0
 */
public class Folders 
{
    /**
     * Creates a directory and all needed parent directories, returning if it already exists.
     * @param path Abstract path of folders to be created.
     * @return Created directory
     * @throws IllegalStateException One or more directory failed to be created.
     *
     * @since 1.0.0
     */
    public static File ensureFolder(
        @NotNull String path
    ) throws
            IllegalStateException
    {
        Objects.requireNonNull(path);

        File file = new File(path);

        if (file.exists())
            return file;

        if (!file.mkdirs())
            throw new IllegalStateException("Failed to create director(y/ies) for path \"" + file.getAbsolutePath() + "\"");

        return file;
    }    

    /**
     * Creates a parent directory and a set of directories contained by parent.
     * @param parent Parent directory
     * @param folders Not-null, not-empty array of folders to be created 
     * @return List of created directories
     * @implNote Already created directories are not added to the list.
     * @throws IllegalStateException One or more directories failed to be created.
     * 
     * @since 1.0.0
     */
    public static List<File> ensureFolders(
        @NotNull String parent,
        @NotEmpty @NotNull String... folders
    ) throws
        IllegalStateException
    {
        Objects.requireNonNull(folders);

        List<File> created = new ArrayList<>();

        boolean createdFolder = false;
        for (String folder : folders)
        {
            File file = new File(parent + "/" + folder);
            createdFolder = !file.exists();

            file = ensureFolder(parent + "/" + folder);

            if (createdFolder)
                created.add(file);
        }        

        return created;
    }
}
