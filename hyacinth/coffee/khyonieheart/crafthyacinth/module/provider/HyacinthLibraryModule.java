package coffee.khyonieheart.crafthyacinth.module.provider;

import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * General-use module template for files that should be loaded like a module and have their resources shared, but don't necessarily need their own module functionality.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class HyacinthLibraryModule implements HyacinthModule
{
    private JarFile jar;

	/**
	 * @param name Module name
	 * @param description Module description
	 * @param version Module version
	 * @param author Module author
	 * @param jar Jarfile this is linked to
	 *
	 * @since 1.0.0
	 */
    public HyacinthLibraryModule(
		@NotNull String name, 
		@NotNull String description, 
		@NotNull String version, 
		@NotNull String author, 
		@NotNull JarFile jar
	) {
        YamlConfiguration config = new YamlConfiguration();

        config.set("name", name);
        config.set("description", description);
        config.set("version", version);
        config.set("author", author);

        this.jar = jar;
    }

    @Override
    public void onEnable() 
    {
        
    }

    @Override
    public void onDisable() 
    {
        
    }

	/**
	 * Obtains this library's jar file.
	 * 
	 * @return This library's jar file.
	 * 
	 * @since 1.0.0
	 */
    public JarFile getJar()
    {
        return this.jar;
    }

	/**
	 * Creates a new module instance from a library jar.
	 *
	 * @param config Yaml configuration for library
	 * @param jar Library jar
	 *
	 * @return A module instance for this jar
	 */
    public static HyacinthLibraryModule create(
        @NotNull YamlConfiguration config, 
        @NotNull JarFile jar
    ) {
        return new HyacinthLibraryModule(
            config.getString("name"), 
            config.getString("description"), 
            config.getString("version"), 
            config.getString("author"),
            jar
        );
    }
}
