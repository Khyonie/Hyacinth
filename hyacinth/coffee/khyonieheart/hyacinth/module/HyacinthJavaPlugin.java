package coffee.khyonieheart.hyacinth.module;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import coffee.khyonieheart.anenome.Arrays;
import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.util.YamlUtils;

/**
 * Bridge between Hyacinth modules and Bukkit plugins.
 *
 * @author Khyonie 
 * @since 1.0.0
 */
public abstract class HyacinthJavaPlugin extends JavaPlugin implements HyacinthModule
{
	private YamlConfiguration config;

	@Override
	public void onEnable()
	{
		// Register self
		this.config = this.buildHyacinthConfig();
		Hyacinth.getModuleManager().registerModule(this);

		this.onPluginEnable();
	}

	public abstract void onPluginEnable();

	public JavaPlugin asJavaPlugin()
	{
		return this;
	}

	public HyacinthModule asHyacinthModule()
	{
		return this;
	}

	private YamlConfiguration buildHyacinthConfig()
	{
		return YamlUtils.of(
			"name", this.getDescription().getName(),
			"package", this.getClass().getPackageName(),
			"version", this.getDescription().getVersion(),
			"entry", this.getDescription().getMain(),
			"description", this.getDescription().getDescription() == null ? "(No description specified)" : this.getDescription().getDescription(),
			"author", this.getDescription().getAuthors().isEmpty() ? "(No author(s) specified)" : Arrays.toString(Arrays.toArray(String.class, this.getDescription().getAuthors()), ", ", null)
		);
	}

	@Override
	public YamlConfiguration getConfiguration()
	{
		return this.config;
	}
}
