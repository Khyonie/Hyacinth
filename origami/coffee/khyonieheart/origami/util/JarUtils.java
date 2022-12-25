package coffee.khyonieheart.origami.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

/**
 * Various utilities for manipulating Jar files.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class JarUtils 
{
    /**
     * Extracts a JarEntry file to a target file. 
     * @param jar Jar file to extract from
     * @param filepathInJar Path to jar entry
     * @param target Target to extract file to
     * @return The written file. If successful, this should be the same object as the target file
     * @throws FileNotFoundException Specified jar entry does not exist
     * @throws IOException File failed to be written or an input stream failed to be created from the jar entry
     * 
     * @since 1.0.0
     */
    public static File extractFromJar(
        @NotNull JarFile jar,
        @Nullable String filepathInJar, 
        @NotNull File target
    ) 
        throws FileNotFoundException, IOException
    {
        JarEntry entry = jar.getJarEntry(filepathInJar);

        if (entry == null)
            throw new FileNotFoundException("No such entry \"" + filepathInJar + "\" in jar \"" + jar.getName() + "\"");
    
        Files.copy(jar.getInputStream(entry), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return target;
    }    

    public static InputStream toInputStream(JarFile jar, String firepathInJar)
    {
        try {
            return jar.getInputStream(jar.getEntry(firepathInJar));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
