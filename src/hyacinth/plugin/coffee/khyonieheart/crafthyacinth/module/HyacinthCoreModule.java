package coffee.khyonieheart.crafthyacinth.module;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import coffee.khyonieheart.crafthyacinth.command.parser.PlayerSuggestionEngine;
import coffee.khyonieheart.crafthyacinth.data.HyacinthDataManager;
import coffee.khyonieheart.crafthyacinth.data.HyacinthPlayerData;
import coffee.khyonieheart.crafthyacinth.event.ServerFinishLoadingEvent;
import coffee.khyonieheart.crafthyacinth.killswitch.suggestions.KillswitchSuggestionEngine;
import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.HyacinthCoreCommand;
import coffee.khyonieheart.hyacinth.command.parser.SuggestionManager;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.nouveau.ModuleFile;
import coffee.khyonieheart.hyacinth.util.Folders;
import coffee.khyonieheart.hyacinth.util.YamlUtils;

/**
 * Internally contained module that is intrinsically tied to Hyacinth as a codebase. Provides a host for commands and other utilities.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class HyacinthCoreModule implements HyacinthModule
{
	private static YamlConfiguration configuration = YamlUtils.of(
		"name",        "Hyacinth",
		"description", "Plugin development should be simple.",
		"entry",       "coffee.khyonieheart.crafthyacinth.module.HyacinthCoreModule",
		"version",     Hyacinth.getMetadata().getString("version"),
		"author",      "Khyonie"
	);

	private static HyacinthCoreModule INSTANCE;
	private static HyacinthDataManager dataManager;
	private static ModuleFile module;

	public HyacinthCoreModule()
	{
		try {
			module = new ModuleFile(new File("./plugins/Hyacinth.jar"));
			Field configField = module.getClass().getDeclaredField("config");
			configField.setAccessible(true);

			configField.set(module, configuration);

			Method setOwnerMethod = module.getClass().getDeclaredMethod("attachModule", HyacinthModule.class);
			setOwnerMethod.setAccessible(true);

			setOwnerMethod.invoke(module, this);
		} catch (IOException | SecurityException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() 
	{
		INSTANCE = this;	

		Folders.ensureFolder("./Hyacinth/playerdata/");

		Hyacinth.getCommandManager().register("hyacinth", new HyacinthCoreCommand(), Hyacinth.getInstance().getServer());
		dataManager = new HyacinthDataManager();
		Hyacinth.getListenerManager().register(this.getClass(), dataManager);

		SuggestionManager.register("ONLINE_PLAYERS", new PlayerSuggestionEngine());
		SuggestionManager.register("AVAILABLE_KILLSWITCHES", new KillswitchSuggestionEngine());

		for (Player p : Bukkit.getOnlinePlayers())
		{
			dataManager.onPlayerJoin(new PlayerJoinEvent(p, "§c(Fake join message)"));
		}

		HyacinthDataManager.registerDataCreator(this, (player) ->{
			return HyacinthPlayerData.initDefault(player);
		});

		Bukkit.getScheduler().runTaskLater(Hyacinth.getInstance(), () -> {
			Bukkit.getPluginManager().callEvent(new ServerFinishLoadingEvent());
		}, 1L);
	}

	@Override
	public void onDisable() 
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			dataManager.save(p);
		}
	}	

	@Override
	public YamlConfiguration getConfiguration()
	{
		return configuration;
	}

	public static HyacinthModule getInstance()
	{
		return INSTANCE;
	}

	public static ModuleFile getModuleFile()
	{
		return module;
	}
}
