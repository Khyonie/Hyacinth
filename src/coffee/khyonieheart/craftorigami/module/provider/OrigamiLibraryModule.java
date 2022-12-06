package coffee.khyonieheart.craftorigami.module.provider;

import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.util.marker.NotNull;

public class OrigamiLibraryModule implements OrigamiModule
{
    private JarFile jar;

    public OrigamiLibraryModule(String name, String description, String version, String author, JarFile jar)
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

    public static OrigamiLibraryModule create(
        @NotNull YamlConfiguration config, 
        @NotNull JarFile jar
    ) {
        return new OrigamiLibraryModule(
            config.getString("name"), 
            config.getString("description"), 
            config.getString("version"), 
            config.getString("author"),
            jar
        );
    }
}
