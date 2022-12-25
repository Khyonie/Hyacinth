package coffee.khyonieheart.origami.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.origami.util.marker.NotEmpty;
import coffee.khyonieheart.origami.util.marker.NotNull;

public class YamlUtils 
{
    public static YamlConfiguration of(Object... data) throws IllegalArgumentException
    {
        YamlConfiguration config = new YamlConfiguration();

        // Sanity check
        if (data.length == 0)
            return config;

        if (data.length % 2 != 0)
            throw new IllegalArgumentException("YAML data must have an even number of inputs (received " + data.length + ")");

        // Build config
        for (int i = 0; i < data.length; i++)
        {
            if (data[i] instanceof String)
            {
                config.set((String) data[i], data[++i]);
                continue;
            }

            throw new IllegalArgumentException("YAML configuration keys must all be of type java.lang.String (key at index " + i + " is of " + (data[i] == null ? "null" : data[i].getClass().getName()) + ")");
        }

        return config;
    }   

    public static boolean containsAll(
        @NotNull YamlConfiguration configuration,
        @NotEmpty String... keys
    ) {
        for (String key : keys)
        {
            if (configuration.contains(key))
            {
                continue;
            }

            return false;
        }

        return true;
    }

    public static List<String> getMissingKeys(
        @NotNull YamlConfiguration configuration,
        @NotEmpty String... keys
    ) {
        List<String> data = new ArrayList<>(Arrays.asList(keys));

        data.removeIf((key) -> configuration.contains(key));

        return data;
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