package coffee.khyonieheart.tidal;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.YamlUtils;

public class Tidal implements HyacinthModule
{
	private static HyacinthModule instance;

	private static YamlConfiguration configuration = YamlUtils.of(
		"name",        "Tidal",
		"description", "Command creation and parser utility",
		"entry",       "coffee.khyonieheart.tidal.Hibiscus",
		"version",     "1.0.0",
		"author",      "Khyonie"
	);

	@Override
	public void onEnable()
	{
		instance = this;
	}

	@Override
	public void onDisable()
	{

	}

	public static HyacinthModule getInstance()
	{
		return instance;
	}

	public static YamlConfiguration getConfiguration()
	{
		return configuration;
	}
}
