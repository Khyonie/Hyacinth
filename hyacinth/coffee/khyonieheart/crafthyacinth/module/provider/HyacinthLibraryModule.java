package coffee.khyonieheart.crafthyacinth.module.provider;

import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class HyacinthLibraryModule implements HyacinthModule
{
    private JarFile jar;

    public HyacinthLibraryModule(String name, String description, String version, String author, JarFile jar)
    {
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

    public JarFile getJar()
    {
        return this.jar;
    }

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
