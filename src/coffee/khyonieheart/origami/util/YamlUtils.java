package coffee.khyonieheart.origami.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

public class YamlUtils 
{
    public static YamlConfiguration ofDefault(Object... defaults) throws IllegalArgumentException
    {
        YamlConfiguration config = new YamlConfiguration();

        // Sanity check
        if (defaults.length == 0)
            return config;

        if (defaults.length % 2 != 0)
            throw new IllegalArgumentException("YAML defaults must have an even number of inputs (received " + defaults.length + ")");

        // Build config
        for (int i = 0; i < defaults.length; i++)
        {
            if (i >= defaults.length) // Just in case
                break;

            if (defaults[i] instanceof String)
            {
                config.set((String) defaults[i], defaults[++i]);
                continue;
            }

            throw new IllegalArgumentException("YAML default keys must all be of type java.lang.String (key at index " + i + " is of " + defaults[i].getClass().getName() + ")");
        }

        return config;
    }   
    
    /**
     * Attempts to merge two configurations with similar, if not same defaults, storing the result in configuration A.
     * @param configA
     * @param configB
     * @param preferB
     * @return
     */
    public static Map<String, Object> mergeConfigs(YamlConfiguration configA, YamlConfiguration configB)
    {
        Map<String, Object> rewrittenKeys = new HashMap<>();


        for (String key : configB.getKeys(true))
        {
            if (configA.contains(key))
            {
                if (configA.get(key).equals(configB.get(key)))
                    continue;

                configA.set(key, configB.get(key));
                rewrittenKeys.put(key, configA.get(key));
                continue;
            }

            configA.set(key, configB.get(key));
            rewrittenKeys.put(key, configA.get(key));
        }

        return rewrittenKeys;
    }

}
