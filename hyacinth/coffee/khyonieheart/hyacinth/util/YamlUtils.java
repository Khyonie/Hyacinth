package coffee.khyonieheart.hyacinth.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Collection of various utilities for creating and checking .yaml configurations.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
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

    /**
     * Checks a given configuration to ensure it contains all the given keys.
     * @param configuration Configuration to check.
     * @param keys Not empty array of keys.
     * @return Whether the given configuration has all the keys given.
     */
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

    /**
     * Obtains a list of keys missing from the given configuration. May be empty.
     * @param configuration Configuration to check.
     * @param keys List of keys to check.
     * @return List of keys missing from the configuration.
     */
    @NotNull
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
     * @param configA First configuration.
     * @param configB Second configuration.
     * @return A collection of differing keys and values.
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

    /**
     * Loads a YAML configuration from a jar entry.
     * @param jar Jar to load from
     * @param filepathInJar Jar entry path.
     * @return A YAML configuration from the interior of the given jar.
     * @throws IOException YAML configuration could not be loaded.
     */
    public static YamlConfiguration yamlFromJar(JarFile jar, String filepathInJar) throws IOException
    {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(JarUtils.toInputStream(null, filepathInJar))); 
    }
}
