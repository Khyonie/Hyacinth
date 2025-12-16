package coffee.khyonieheart.hibiscus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hibiscus.actionable.OpenCloseActionable;
import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.YamlUtils;

public class Hibiscus implements HyacinthModule
{
	private static YamlConfiguration configuration = YamlUtils.of(
		"name",        "Hibiscus",
		"description", "General purpose GUI library",
		"entry",       "coffee.khyonieheart.hibiscus.Hibiscus",
		"version",     "1.0.0",
		"author",      "Khyonie"
	);

	private static Map<Player, Gui> activeGuis = new HashMap<>();
	private static Hibiscus instance;

	@Override
	public void onEnable() 
	{
		instance = this;

		Hyacinth.getListenerManager().register(this.getClass(), new GuiListener());
	}

	@Override
	public void onDisable() 
	{
		activeGuis.forEach((player, gui) -> {
			if (gui instanceof OpenCloseActionable g)
			{
				g.onClose(player, gui);
			}
		});
	}

	@Override
	public YamlConfiguration getConfiguration()
	{
		return configuration;
	}

	public static void registerOpen(Player player, Gui gui)
	{
		activeGuis.put(player, gui);

		if (gui instanceof OpenCloseActionable g)
		{
			g.onOpen(player, gui, gui.getDefaultConfiguration());
		}
	}

	public static void registerClose(Player player)
	{
		Gui gui = activeGuis.remove(player);

		if (gui instanceof OpenCloseActionable g)
		{
			g.onClose(player, gui);
		}
	}

	public static Gui getOpenGui(Player player)
	{
		return activeGuis.get(player);
	}

	public static boolean isInGui(Player player)
	{
		return activeGuis.containsKey(player);
	}

	public static Hibiscus getInstance()
	{
		return instance;
	}
}
