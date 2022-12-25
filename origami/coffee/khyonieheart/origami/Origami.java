package coffee.khyonieheart.origami;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import coffee.khyonieheart.craftorigami.command.OrigamiCommandManager;
import coffee.khyonieheart.origami.command.CommandManager;
import coffee.khyonieheart.origami.exception.OrigamiModuleException;
import coffee.khyonieheart.origami.module.ModuleManager;
import coffee.khyonieheart.origami.testing.TestIdentifier;
import coffee.khyonieheart.origami.testing.UnitTestManager;
import coffee.khyonieheart.origami.testing.UnitTestResult;
import coffee.khyonieheart.origami.testing.UnitTestable;
import coffee.khyonieheart.origami.util.Folders;
import coffee.khyonieheart.origami.util.JarUtils;
import coffee.khyonieheart.origami.util.YamlUtils;
import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

/**
 * Main class for the Origami API.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Origami extends JavaPlugin implements UnitTestable
{
    private static YamlConfiguration DEFAULT_CONFIG = YamlUtils.of(
        "providers.moduleManagerProvider", "internal/coffee.khyonieheart.craftorigami.module.OrigamiModuleManager", // Format: Filename[.jar]/<class>
        "providers.commandManagerProvider", "internal/coffee.khyonieheart.craftorigami.command.OrigamiCommandManager", // See above
        "preferDiskConfigChanges", true, // If config was changed on disk, prefer those changes over the config in memory
        "disallowMultipleInnerModuleClasses", false,
        "enableVerboseLogging", false,
        "performUnitTests", false,
        "consoleColorCodes", "windows", // Either "windows" or "unix"
        "regularLoggingFlavor", "§9Origami §8> §7LOGGING §8> §7",
        "verboseLoggingFlavor", "§9Origami §8> §eVERBOSE §8> §7"
    );

    private static Origami INSTANCE;
    private static ModuleManager ACTIVE_MODULE_MANAGER;
    private static CommandManager ACTIVE_COMMAND_MANAGER = new OrigamiCommandManager(); // TODO This

    private static YamlConfiguration LOADED_CONFIGURATION = new YamlConfiguration();
    private static YamlConfiguration METADATA = new YamlConfiguration();

    @Override
    public void onEnable()
    {
        long currentTime = System.currentTimeMillis();

        INSTANCE = this;

        Folders.ensureFolders("./Origami", "providers/commands", "providers/modules", "modules");

        File configFile = new File("origami.yml");

        try {
            boolean created = false;

            if (!configFile.exists())
            {
                DEFAULT_CONFIG.save(configFile);
                LOADED_CONFIGURATION.load(configFile);
                Logger.verbose("Config file does not exist, creating");
            }
            
            if (!created)
                LOADED_CONFIGURATION.load(configFile);
            
            Logger.verbose("Loaded config");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();

            LOADED_CONFIGURATION = DEFAULT_CONFIG;
        }

        Logger.verbose("Verifying config");
        Logger.verbose("Looking for missing keys [1/2]");
        for (String key : DEFAULT_CONFIG.getKeys(true))
        {
            if (LOADED_CONFIGURATION.contains(key, false))
                continue;

            Logger.verbose("§e - Missing key: " + key + " (default: " + DEFAULT_CONFIG.get(key) + ")");
            LOADED_CONFIGURATION.set(key, DEFAULT_CONFIG.get(key));
        }

        Logger.verbose("Looking for unused keys [2/2]");
        for (String key : LOADED_CONFIGURATION.getKeys(true))
        {
            if (DEFAULT_CONFIG.contains(key, false))
                continue;

            Logger.verbose("§e - Unused key: " + key + "(value: " + LOADED_CONFIGURATION.get(key) + ")");
        }

        try {
            METADATA.load(new InputStreamReader(JarUtils.toInputStream(this.getJarFile(), "meta/meta.yml")));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            METADATA = YamlUtils.of("version", "unknown", "type", "unknown");
        }

        Logger.log("Loading Origami " + METADATA.getString("version"));

        try {
            ACTIVE_MODULE_MANAGER = ModuleManager.obtainModuleManager(LOADED_CONFIGURATION.getString("providers.moduleManagerProvider"));
        } catch (FileNotFoundException | OrigamiModuleException e) {
            e.printStackTrace();
        }

        if (ACTIVE_MODULE_MANAGER == null)
        {
            Logger.log("§cFailed to load module manager. Cannot load modules");
            return;
        }

        Logger.todo("Load command manager");

        File[] presentModuleFiles = new File("Origami/modules/").listFiles();

        for (File modFile : presentModuleFiles)
        {
            if (!modFile.getName().endsWith(".jar"))
            {
                continue;
            }

            try {
                ACTIVE_MODULE_MANAGER.loadModule(modFile);
                continue;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OrigamiModuleException e) {
                e.printStackTrace();
            }
            Logger.log("§cFailed to load module file " + modFile.getName());
        }

        Logger.verbose("Loading complete");
        Logger.log("Loaded in " + (System.currentTimeMillis() - currentTime) + " ms");

        // Loading complete

        try {
            if (LOADED_CONFIGURATION.getBoolean("performUnitTests"))
            {
                UnitTestManager.performUnitTests(true, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable()
    {
        saveOrigamiConfig();
    }  

    /**
     * Obtains the current module manager in use.
     * @return A module manager
     * 
     * @since 1.0.0
     */
    public static ModuleManager getModuleManager()
    {
        return ACTIVE_MODULE_MANAGER;
    }

    /**
     * Obtains the current command manager in use.
     * @return A command manager
     * 
     * @since 1.0.0
     */
    public static CommandManager getCommandManager()
    {
        return ACTIVE_COMMAND_MANAGER;
    }

    /**
     * Obtains Origami's current instance.
     * @return Origami instance
     * 
     * @since 1.0.0
     */
    public static Origami getInstance()
    {
        return INSTANCE;
    }

    /**
     * Obtains a value in the Origami main config.
     * @param key Key to value
     * @param objType Class of object to expect. May return null if value is incompatible with provided type, see {@link #getConfig(String)}
     * @return A value from configuration in memory
     * 
     * @since 1.0.0
     */
    @Nullable
    public static <T> T getConfig(
        @Nullable String key,
        @NotNull Class<T> objType
    ) {
        if (!LOADED_CONFIGURATION.contains(key))
            return null;

        if (key == null)
            return null;

        try {
            return objType.cast(LOADED_CONFIGURATION.get(key));
        } catch (ClassCastException e) {
            return null;            
        }
    }

    /**
     * Obtains an object in the Origami main config. This method does not perform any automatic casting. For the automatic cast version of this method, see {@link #getConfig(String, Class)} 
     * @param key
     * @return 
     * 
     * @since 1.0.0
     */
    @Nullable
    public static Object getConfig(
        @Nullable String key
    ) {
        return getConfig(key, Object.class);
    }

    /**
     * Obtains Bukkit's classloader used to load Origami. In most cases, this should be an instance of final package-private class {@link org.bukkit.plugin.java.PluginClassLoader}
     * @return Classloader used to load Origami
     * 
     * @since 1.0.0
     */
    public static ClassLoader getClassloader()
    {
        return INSTANCE.getClassLoader();
    }

    private static JarFile CACHED_JAR;
    public JarFile getJarFile()
    {
        if (CACHED_JAR == null)
        {
            try {
                CACHED_JAR = new JarFile(this.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return CACHED_JAR;
    }

    //
    // Instance methods
    //

    private void saveOrigamiConfig()
    {
        Logger.log("Saving config");

        File configFile = new File("origami.yml");

        try {
            if (!configFile.exists())
            {
                DEFAULT_CONFIG.save(configFile);
                Logger.verbose("Config file does not exist, creating");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.verbose("Checking for changes on disk");

        YamlConfiguration localConfig = new YamlConfiguration();
        
        try {
            localConfig.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            return;
        }

        YamlUtils.mergeConfigs(LOADED_CONFIGURATION, localConfig);

        try {
            LOADED_CONFIGURATION.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @TestIdentifier("Origami core tests")
    public List<UnitTestResult> test() 
    {
        List<UnitTestResult> completedTests = new ArrayList<>();

        completedTests.add(new UnitTestResult(INSTANCE != null, "Null plugin instance", (INSTANCE != null ? null : "Origami static instance not set"), this));

        File origamiConfigFile = new File("origami.yml");
        completedTests.add(new UnitTestResult(origamiConfigFile.exists(), "Main config exists", (origamiConfigFile.exists() ? null : "Main config file does not exist on disk"), this));

        // TODO Write more tests

        return completedTests;
    }
}