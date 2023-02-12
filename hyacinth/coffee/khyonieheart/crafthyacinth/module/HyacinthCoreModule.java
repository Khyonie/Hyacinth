package coffee.khyonieheart.crafthyacinth.module;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.YamlUtils;

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

	@Override
	public void onEnable() 
	{
		INSTANCE = this;	
	}

	@Override
	public void onDisable() 
	{
		
	}	

	public static HyacinthModule getInstance()
	{
		return INSTANCE;
	}

	public static YamlConfiguration getConfiguration()
	{
		return configuration;
	}
}
