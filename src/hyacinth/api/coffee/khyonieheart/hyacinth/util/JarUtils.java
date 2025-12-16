package coffee.khyonieheart.hyacinth.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;

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
	@NotNull
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

	public static JarFile getPluginJar(String pluginName)
	{
		JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(pluginName);

		if (plugin == null)
		{
			return null;
		}

		try {
			Method getFile = JavaPlugin.class.getDeclaredMethod("getFile");
			getFile.setAccessible(true);
			File file = (File) getFile.invoke(plugin);
			return new JarFile(file);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts a jar entry to an input stream.
	 *
	 * @param jar Jarfile to read from
	 * @param filepathInJar Path to jar entry
	 *
	 * @return An inputstream for the jar entry. Returns null if jar entry cannot be accessed.
	 */
	@Nullable
    public static InputStream toInputStream(
		JarFile jar, 
		String filepathInJar
	) {
        try {
            return jar.getInputStream(jar.getEntry(filepathInJar));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
