package coffee.khyonieheart.origami.module.marker;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Denotes that a module has an attached configuration.
 * @since 1.0.0
 */
public interface Configurable 
{
    public YamlConfiguration getDefaultConfiguration();

    public YamlConfiguration getConfiguration();

    public default Map<String, Object> verify(YamlConfiguration config, YamlConfiguration defaultConfig)
    {
        Map<String, Object> missingKeys = new HashMap<>();

        for (String key : defaultConfig.getKeys(true))
        {
            if (config.contains(key, false))
                continue;

            missingKeys.put(key, defaultConfig.get(key));
            config.set(key, defaultConfig.get(key));
        }

        return missingKeys;
    }
}
