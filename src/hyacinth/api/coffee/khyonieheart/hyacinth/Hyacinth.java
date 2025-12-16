package coffee.khyonieheart.hyacinth;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.jar.JarFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.crafthyacinth.command.HyacinthCommandManager;
import coffee.khyonieheart.crafthyacinth.event.HyacinthListenerManager;
import coffee.khyonieheart.crafthyacinth.module.HyacinthCoreModule;
import coffee.khyonieheart.crafthyacinth.module.HyacinthModuleManager;
import coffee.khyonieheart.hibiscus.Hibiscus;
import coffee.khyonieheart.hyacinth.command.CommandManager;
import coffee.khyonieheart.hyacinth.listener.ListenerManager;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.ModuleManager;
import coffee.khyonieheart.hyacinth.module.nouveau.ClassCoordinator;
import coffee.khyonieheart.hyacinth.module.nouveau.ModuleLoaderPipeline;
import coffee.khyonieheart.hyacinth.util.Folders;
import coffee.khyonieheart.hyacinth.util.JarUtils;
import coffee.khyonieheart.hyacinth.util.YamlUtils;

/**
 * Main class for the Hyacinth API.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Hyacinth extends JavaPlugin
{
    private static final YamlConfiguration DEFAULT_CONFIG = YamlUtils.of(
        "enableVerboseLogging", false,
        "enableModules", false,
        "performUnitTests", false,
        "regularLoggingFlavor", "§8[ §7LOGGING §8] §9%MODULE §8> §e%CLASS§6:§e%METHOD §8> §7",
        "verboseLoggingFlavor", "§8[ §eVERBOSE §8] §9%MODULE §8> §e%CLASS§6:§e%METHOD §8> §7",
		"debugLoggingFlavor", "§8[ §8 §cDEBUG §c §8] §9%MODULE §8> §e%CLASS§6:§e%METHOD §8> §7",
		"todoLoggingFlavor", "§8[ §d §dTODO §d §d §8] §9%MODULE §8> §e%CLASS§6:§e%METHOD §8> §7"
    );

    private static Hyacinth instance;
    private static boolean libraryMode = true;
    private static ModuleManager moduleManager = new HyacinthModuleManager();
    private static CommandManager commandManager = new HyacinthCommandManager();
	private static ListenerManager listenerManager = new HyacinthListenerManager();

    private static YamlConfiguration loadedConfiguration = new YamlConfiguration();
    private static YamlConfiguration metadata = new YamlConfiguration();

    @Override
    public void onEnable()
    {
        long currentTime = System.currentTimeMillis();

        instance = this;

        Folders.ensureFolders("./Hyacinth", "modules");

        File configFile = new File("hyacinth.yml");
        boolean firstStart = false;

        try {
            boolean created = false;
            
            if (!configFile.exists())
            {
                DEFAULT_CONFIG.save(configFile);
				JarUtils.extractFromJar(JarUtils.getPluginJar("Hyacinth"), "meta/hyacinth.yml", configFile);
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
        Logger.verbose("Looking for missing keys");
        for (String key : DEFAULT_CONFIG.getKeys(true))
        {
            if (loadedConfiguration.contains(key, false))
                continue;

            Logger.verbose("§e - Missing key: " + key + " (default: " + DEFAULT_CONFIG.get(key) + ")");
            loadedConfiguration.set(key, DEFAULT_CONFIG.get(key));
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

		// Internal hyacinth module
		HyacinthCoreModule hyacinth = new HyacinthCoreModule();
		moduleManager.registerModule(hyacinth);

		Hibiscus hibiscus = new Hibiscus();
		moduleManager.registerModule(hibiscus);

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
		ModuleLoaderPipeline loader = new ModuleLoaderPipeline();
		
		try {
			loader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (HyacinthModule module : moduleManager.getModules())
		{
			try {
				YamlConfiguration config = module.getConfiguration();
				Logger.verbose("Running onEnable() for module " + (config != null ? config.getString("name") : ClassCoordinator.getOwningModule(module.getClass()).getConfiguration().getString("name")));
				module.onEnable();
			} catch (Exception | Error e) {
				Logger.log("§cFailed to enable module " + module.getClass().getName());
				e.printStackTrace();
			}
		}

        Logger.verbose("Loading complete");
        Logger.log("Loaded in " + (System.currentTimeMillis() - currentTime) + " ms");

        // Loading complete
    }

    @Override
    public void onDisable()
    {
		moduleManager.getModules().forEach(mod -> { 
			try { 
				mod.onDisable(); 
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
		});
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
	@NotNull
    public static ModuleManager getModuleManager()
    {
        return moduleManager;
    }

    /**
     * Obtains the current command manager in use.
     * @return A command manager.
     * 
     * @since 1.0.0
     */
	@NotNull
    public static CommandManager getCommandManager()
    {
        return commandManager;
    }

	@NotNull
	public static ListenerManager getListenerManager()
	{
		return listenerManager;
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
}
