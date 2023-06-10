package coffee.khyonieheart.crafthyacinth.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.reflect.TypeToken;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.data.PlayerDataManager;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.JsonUtils;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Internal implementation of a player data manager.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class HyacinthDataManager implements PlayerDataManager, Listener
{
	private static Map<Player, Map<String, CastableHashMap<String, Object>>> loadedData = new HashMap<>();
	private static Map<HyacinthModule, Function<Player, CastableHashMap<String, Object>>> registeredDataCreators = new HashMap<>();

	/** 
	 * Join listener. Loads or creates player data upon joining.
	 *
	 * @param event Player join event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		File file = new File("./Hyacinth/playerdata/" + event.getPlayer().getUniqueId().toString() + ".json");

		// Load from file, or if not present, create new data
		Map<String, CastableHashMap<String, Object>> data = load(file);
		
		// Automatically create data if not present
		for (HyacinthModule mod : registeredDataCreators.keySet())
		{
			String name = Hyacinth.getModuleManager().getConfiguration(mod).getString("name");
			if (data.containsKey(name))
			{
				continue;
			}

			Logger.verbose("Creating new player data section for module \"" + name + "\"");

			data.put(name, registeredDataCreators.get(mod).apply(event.getPlayer()));
		}

		// Finished loading
		loadedData.put(event.getPlayer(), data);
	}

	/**
	 * Leave listener. Saves player data to Hyacinth/playerdata/.
	 *
	 * @param event Player quit event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		save(event.getPlayer());
	}

	public static Map<String, CastableHashMap<String, Object>> get(
		@NotNull Player player
	) {
		return loadedData.get(player);
	}

	public static Map<String, Object> get(
		@NotNull Player player, 
		@NotNull String module
	) {
		return loadedData.get(player).get(module);
	}

	public static Map<String, Object> get(Player player, HyacinthModule module)
	{
		return loadedData.get(player).get(Hyacinth.getModuleManager().getConfiguration(module).getString("name"));
	}

	public static void registerDataCreator(HyacinthModule mod, Function<Player, CastableHashMap<String, Object>> creator)
	{
		registeredDataCreators.put(mod, creator);
	}

	@Override
	public Map<String, CastableHashMap<String, Object>> load(File file) 
	{
		Map<String, CastableHashMap<String, Object>> data;

		if (!file.exists())
		{
			data = new HashMap<>();
			JsonUtils.toFile(file.getAbsolutePath(), data);
			return data;
		}

		try {
			data = JsonUtils.fromJson(file.getAbsolutePath(), new TypeToken<Map<String, CastableHashMap<String, Object>>>() {}.getType());
			if (data == null)
			{
				throw new NullPointerException("Data file could not be read");
			}

			// Adapt LinkedTreeMaps to a HashMap
			HashMap<String, CastableHashMap<String, Object>> newData = new HashMap<>(data);

			data = newData;

			return data;
		} catch (FileNotFoundException | NullPointerException | ClassCastException e) {
			Logger.log("§cFailed to load player data file \"" + file.getName() + "\"");
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public File save(Player player) 
	{
		Map<String, CastableHashMap<String, Object>> data = loadedData.get(player); 
		loadedData.remove(player);

		return JsonUtils.toFile("./Hyacinth/playerdata/" + player.getUniqueId().toString() + ".json", data);
	}
}
