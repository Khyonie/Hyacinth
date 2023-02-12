package coffee.khyonieheart.hyacinth;

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
import org.bukkit.scheduler.BukkitScheduler;

import coffee.khyonieheart.crafthyacinth.command.HyacinthCommandManager;
import coffee.khyonieheart.crafthyacinth.module.HyacinthCoreModule;
import coffee.khyonieheart.hyacinth.command.CommandManager;
import coffee.khyonieheart.hyacinth.exception.HyacinthModuleException;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.ModuleManager;
import coffee.khyonieheart.hyacinth.testing.TestIdentifier;
import coffee.khyonieheart.hyacinth.testing.UnitTestManager;
import coffee.khyonieheart.hyacinth.testing.UnitTestResult;
import coffee.khyonieheart.hyacinth.testing.UnitTestable;
import coffee.khyonieheart.hyacinth.util.Folders;
import coffee.khyonieheart.hyacinth.util.JarUtils;
import coffee.khyonieheart.hyacinth.util.YamlUtils;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

/**
 * Main class for the Hyacinth API.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Hyacinth extends JavaPlugin implements UnitTestable
{
    private static final YamlConfiguration DEFAULT_CONFIG = YamlUtils.of(
        "providers.moduleManagerProvider", "internal/coffee.khyonieheart.crafthyacinth.module.HyacinthModuleManager", // Format: Filename[.jar]/<class>
        "providers.commandManagerProvider", "internal/coffee.khyonieheart.crafthyacinth.command.HyacinthCommandManager", // See above
        "preferDiskConfigChanges", true, // If config was changed on disk, prefer those changes over the config in memory
        "disallowMultipleInnerModuleClasses", false,
        "enableVerboseLogging", false,
        "enableModules", false,
        "performUnitTests", false,
        "deleteUnusedKeys", true,
        "regularLoggingFlavor", "§9Hyacinth §8> §7LOGGING §8> §7",
        "verboseLoggingFlavor", "§9Hyacinth §8> §eVERBOSE §8> §7"
    );

    private static Hyacinth instance;
    private static boolean libraryMode = true;
    private static ModuleManager activeModuleManager;
    private static CommandManager activeCommandManager = new HyacinthCommandManager(); // TODO This

    private static YamlConfiguration loadedConfiguration = new YamlConfiguration();
    private static YamlConfiguration metadata = new YamlConfiguration();

    @Override
    public void onEnable()
    {
        long currentTime = System.currentTimeMillis();

        instance = this;

        Folders.ensureFolders("./Hyacinth", "providers/commands", "providers/modules", "modules");

        File configFile = new File("hyacinth.yml");
        boolean firstStart = false;

        try {
            boolean created = false;
            
            if (!configFile.exists())
            {
                DEFAULT_CONFIG.save(configFile);
                loadedConfiguration.load(configFile);
                Logger.verbose("Config file does not exist, creating and assuming this is a first start");
                firstStart = true;
            }
            
            if (!created)
			{
                loadedConfiguration.load(configFile);
			}
            
            Logger.verbose("Loaded config");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();

            loadedConfiguration = DEFAULT_CONFIG;
        }

        Logger.verbose("Verifying config");
        Logger.verbose("Looking for missing keys [1/2]");
        for (String key : DEFAULT_CONFIG.getKeys(true))
        {
            if (loadedConfiguration.contains(key, false))
                continue;

            Logger.verbose("§e - Missing key: " + key + " (default: " + DEFAULT_CONFIG.get(key) + ")");
            loadedConfiguration.set(key, DEFAULT_CONFIG.get(key));
        }

        Logger.verbose("Looking for unused keys [2/2]");
        for (String key : loadedConfiguration.getKeys(true))
        {
            if (DEFAULT_CONFIG.contains(key, false))
                continue;

            Logger.verbose("§e - Unused key: " + key + "(value: " + loadedConfiguration.get(key) + ")");

            if (loadedConfiguration.getBoolean("deleteUnusedKeys"))
            {
                Logger.verbose("§e - Deleting unused key " + key);
                loadedConfiguration.set(key, null);
            }
        }

        try {
            loadedConfiguration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            metadata.load(new InputStreamReader(JarUtils.toInputStream(this.getJarFile(), "meta/meta.yml")));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            metadata = YamlUtils.of("version", "unknown", "type", "unknown");
        }

        // Decide library mode
        if ((System.getProperty("hyacinthFirstStartInModuleMode") != null && firstStart) || loadedConfiguration.getBoolean("enableModules")) // Added as -DhyacinthFirstStartInModuleMode to launch options
        {
            libraryMode = false;

            if (firstStart)
            {
                Logger.log("§eStarting Hyacinth's first-time setup with support for modules");
                loadedConfiguration.set("enableModules", true);
                try {
                    loadedConfiguration.save(configFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

		// Begin loading
        Logger.log("Loading Hyacinth " + metadata.getString("version"));

		try {
			activeModuleManager = ModuleManager.obtainModuleManager(loadedConfiguration.getString("providers.moduleManagerProvider"));
			if (activeModuleManager == null)
			{
				throw new HyacinthModuleException();
			}
		} catch (FileNotFoundException | HyacinthModuleException e) {
			e.printStackTrace();
            Logger.log("§cFailed to load module manager. Cannot load modules");
            return;
		}

		try {
			activeCommandManager = CommandManager.sourceCommandManager(loadedConfiguration.getString("providers.commandManagerProvider"));

			if (activeCommandManager == null)
			{
				throw new HyacinthModuleException();
			}
		} catch (FileNotFoundException | HyacinthModuleException e) {
			e.printStackTrace();
			Logger.log("§cFailed to load command manager. Cannot register commands.");
			return;
		}

		activeModuleManager.addModule(new HyacinthCoreModule(), HyacinthCoreModule.getConfiguration());

        if (!loadedConfiguration.getBoolean("enableModules") && libraryMode == true)
        {
            if (firstStart)
            {
                Logger.log("§eEnabled Hyacinth in library-only mode. Modules, libraries, and providers made for Hyacinth will not be loaded.");
                Logger.log("§e - If \"module\" mode is desired, edit \"enableModules\" in hyacinth.yml to true");
            }

			Logger.log("Hyacinth fully loaded in library mode");

            return;
        }

		// Load modules
        File[] presentModuleFiles = new File("Hyacinth/modules/").listFiles();

        for (File modFile : presentModuleFiles)
        {
            if (!modFile.getName().endsWith(".jar"))
            {
                continue;
            }

            try {
                activeModuleManager.loadModule(modFile);
                continue;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (HyacinthModuleException e) {
                e.printStackTrace();
            }
            Logger.log("§cFailed to load module file " + modFile.getName());
        }

        Logger.verbose("Loading complete");
        Logger.log("Loaded in " + (System.currentTimeMillis() - currentTime) + " ms");

        // Loading complete

        try {
            if (loadedConfiguration.getBoolean("performUnitTests"))
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
        saveHyacinthConfig();
    } 

	/**
	 * Runs a task outside of the server thread.
	 *
	 * @param runnable Task to run
	 * @since 1.0.0
	 *
	 * @return The created thread
	 */
	@NotNull
	public Thread runAsyncTask(
		@NotNull Runnable runnable
	) {
		Thread thread = new Thread(runnable);
		thread.start();
		return thread;
	}

	/**
	 * Obtains loaded Hyacinth metadata, such as version and build type.
	 *
	 * @since 1.0.0
	 *
	 * @return Metadata configuration
	 */
	@NotNull
	public static YamlConfiguration getMetadata()
	{
		return metadata;
	}

	/**
	 * Obtains Bukkit's task scheduler.
	 * @return Bukkit's task scheduler.
	 *
	 * @since 1.0.0
	 */
	@NotNull
	public static BukkitScheduler getScheduler()
	{
		return instance.getServer().getScheduler();
	}

    /**
     * Obtains the current module manager in use.
     * @return A module manager.
     * 
     * @since 1.0.0
     */
	@Nullable
    public static ModuleManager getModuleManager()
    {
        return activeModuleManager;
    }

    /**
     * Obtains the current command manager in use.
     * @return A command manager.
     * 
     * @since 1.0.0
     */
	@Nullable
    public static CommandManager getCommandManager()
    {
        return activeCommandManager;
    }

    /**
     * Obtains Hyacinth's current instance.
     * @return Hyacinth instance.
     * 
     * @since 1.0.0
     */
	@NotNull
    public static Hyacinth getInstance()
    {
        return instance;
    }

    /**
     * Obtains a value in the Hyacinth main config.
     * @param key Key to value.
     * @param objType Class of object to expect. May return null if value is incompatible with provided type, see {@link #getConfig(String)}
     * @return A value from configuration in memory.
     * 
     * @since 1.0.0
     */
    @Nullable
    public static <T> T getConfig(
        @Nullable String key,
        @NotNull Class<T> objType
    ) {
        if (!loadedConfiguration.contains(key))
            return null;

        if (key == null)
            return null;

        try {
            return objType.cast(loadedConfiguration.get(key));
        } catch (ClassCastException e) {
            return null;            
        }
    }

    /**
     * Obtains an object in the Hyacinth main config. This method does not perform any automatic casting. For the automatic cast version of this method, see {@link #getConfig(String, Class)} 
     * @param key String key to obtain config value.
     * @return A value from configuration in memory.
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
     * Obtains Bukkit's classloader used to load Hyacinth. In most cases, this should be an instance of final package-private class {@link org.bukkit.plugin.java.PluginClassLoader}
     * @return Classloader used to load Hyacinth.
     * 
     * @since 1.0.0
     */
	@NotNull
    public static ClassLoader getClassloader()
    {
        return instance.getClassLoader();
    }

    private static JarFile CACHED_JAR;

    /**
     * Obtains the jar file loaded by Bukkit to load this plugin.
     * @return Hyacinth jar file.
     */
	@NotNull
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

    private void saveHyacinthConfig()
    {
        Logger.log("Saving config");

        File configFile = new File("hyacinth.yml");

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

        YamlUtils.mergeConfigs(loadedConfiguration, localConfig);

        try {
            loadedConfiguration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @TestIdentifier("Hyacinth core tests")
    public List<UnitTestResult> test() 
    {
        List<UnitTestResult> completedTests = new ArrayList<>();

        completedTests.add(new UnitTestResult(instance != null, "Null plugin instance", (instance != null ? null : "Hyacinth static instance not set"), this));

        File hyacinthConfigFile = new File("hyacinth.yml");
        completedTests.add(new UnitTestResult(hyacinthConfigFile.exists(), "Main config exists", (hyacinthConfigFile.exists() ? null : "Main config file does not exist on disk"), this));

        // TODO Write more tests

        return completedTests;
    }
}
