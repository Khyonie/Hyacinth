package coffee.khyonieheart.origami;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import coffee.khyonieheart.origami.enums.ConfigurationType;
import coffee.khyonieheart.origami.module.ModuleManager;
import coffee.khyonieheart.origami.option.Option;
import coffee.khyonieheart.origami.testing.TestIdentifier;
import coffee.khyonieheart.origami.testing.UnitTestManager;
import coffee.khyonieheart.origami.testing.UnitTestResult;
import coffee.khyonieheart.origami.testing.UnitTestable;
import coffee.khyonieheart.origami.util.Folders;
import coffee.khyonieheart.origami.util.YamlUtils;

/**
 * Main class for the Origami API.
 * @author Khyonie
 * @since 1.0.0
 */
public class Origami extends JavaPlugin implements UnitTestable
{
    private static YamlConfiguration DEFAULT_CONFIG = YamlUtils.ofDefault(
        "providers.moduleManagerProvider", "internal/coffee.khyonieheart.craftorigami.module.OrigamiModuleManager",
        "providers.commandManagerProvider", "internal/coffee/khyonieheart.craftorigami.command.OrigamiCommandManager",
        "consoleColorCodes", "windows", // Either "windows" or "unix"
        "preferDiskConfigChanges", true, // If config was changed on disk, prefer those changes over the config in memory
        "disallowMultipleInnerModuleClasses", false,
        "enableVerboseLogging", false,
        "performUnitTests", false,
        "regularLoggingFlavor", "§9Origami §8> §7LOGGING §8> §7",
        "verboseLoggingFlavor", "§9Origami §8> §eVERBOSE §8> §7"
    );

    private static Origami INSTANCE;
    private static ModuleManager ACTIVE_MODULE_MANAGER;

    private static YamlConfiguration LOADED_CONFIGURATION = new YamlConfiguration();

    @Override
    public void onEnable()
    {
        INSTANCE = this;

        Folders.ensureFolders("./plugins/Origami", "providers/commands", "providers/modules", "modules");

        File configFile = new File("./plugins/Origami/origami.yml");

        try {
            boolean created = false;

            if (!configFile.exists())
            {
                DEFAULT_CONFIG.save(configFile);
                LOADED_CONFIGURATION.load(configFile);
                Logger.verbose("Config file does not exist, creating...");
            }
            
            if (!created)
                LOADED_CONFIGURATION.load(configFile);
            
            Logger.verbose("Loaded config!");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();

            LOADED_CONFIGURATION = DEFAULT_CONFIG;
        }

        Logger.verbose("Verifying config...");
        Logger.verbose("Looking for missing keys [1/2]");
        for (String key : DEFAULT_CONFIG.getKeys(true))
        {
            if (LOADED_CONFIGURATION.contains(key, false))
                continue;

            Logger.verbose("§e- Missing key: " + key + " (default: " + DEFAULT_CONFIG.get(key) + ")");
            LOADED_CONFIGURATION.set(key, DEFAULT_CONFIG.get(key));
        }

        Logger.verbose("Looking for unused keys [2/2]");
        for (String key : LOADED_CONFIGURATION.getKeys(true))
        {
            if (DEFAULT_CONFIG.contains(key, false))
                continue;

            Logger.verbose("- Unused key: " + key + "(value: " + LOADED_CONFIGURATION.get(key) + ")");
        }

        if (LOADED_CONFIGURATION.getBoolean("performUnitTests"))
        {
            UnitTestManager.performUnitTests(true, this);
        }
    }

    @Override
    public void onDisable()
    {
        saveOrigamiConfig();
    } 

    private void saveOrigamiConfig()
    {
        Logger.log("Saving config");

        File configFile = new File("./plugins/Origami/origami.yml");

        try {
            if (!configFile.exists())
            {
                DEFAULT_CONFIG.save(configFile);
                Logger.verbose("Config file does not exist, creating...");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.verbose("Checking for changes on disk...");

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

    public static ModuleManager getModuleManager()
    {
        return ACTIVE_MODULE_MANAGER;
    }

    public static Origami getInstance()
    {
        return INSTANCE;
    }

    public static Option getConfig(String key, ConfigurationType type)
    {
        if (!LOADED_CONFIGURATION.contains(key))
            return Option.none();

        if (key == null)
            return Option.none();

        return switch(type)
        {
            case BOOLEAN -> Option.some(LOADED_CONFIGURATION.getBoolean(key));
            case BOOLEAN_LIST -> Option.some(LOADED_CONFIGURATION.getBooleanList(key));
            case DOUBLE -> Option.some(LOADED_CONFIGURATION.getDouble(key));
            case DOUBLE_LIST -> Option.some(LOADED_CONFIGURATION.getDoubleList(key));
            case FLOAT_LIST -> Option.some(LOADED_CONFIGURATION.getFloatList(key));
            case INT -> Option.some(LOADED_CONFIGURATION.getInt(key));
            case INT_LIST -> Option.some(LOADED_CONFIGURATION.getIntegerList(key));
            case LONG -> Option.some(LOADED_CONFIGURATION.getLong(key));
            case LONG_LIST -> Option.some(LOADED_CONFIGURATION.getLongList(key));
            case STRING -> Option.some(LOADED_CONFIGURATION.getString(key));
            case STRING_LIST -> Option.some(LOADED_CONFIGURATION.getStringList(key));
            default -> Option.some(LOADED_CONFIGURATION.get(key));
        };
    }

    @Override
    @TestIdentifier("Origami core tests")
    public List<UnitTestResult> test() 
    {
        List<UnitTestResult> completedTests = new ArrayList<>();

        completedTests.add(new UnitTestResult(INSTANCE != null, "Null plugin instance", (INSTANCE != null ? null : "Origami static instance not set"), this));

        File origamiConfigFile = new File("./plugins/Origami/origami.yml");
        completedTests.add(new UnitTestResult(origamiConfigFile.exists(), "Main config exists", (origamiConfigFile.exists() ? null : "Main config file does not exist on disk"), this));

        // TODO Write more tests

        return completedTests;
    }
}