package coffee.khyonieheart.craftorigami.module.provider;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.origami.module.OrigamiModule;

public class OrigamiLibraryModule implements OrigamiModule
{
    public OrigamiLibraryModule(String name, String description, String version, String author, ClassLoader libraryLoader)
    {
        YamlConfiguration config = new YamlConfiguration();

        config.set("name", name);
        config.set("description", description);
        config.set("version", version);
        config.set("author", author);
    }

    @Override
    public void onEnable() 
    {
        
    }

    @Override
    public void onDisable() 
    {
        
    }
}
