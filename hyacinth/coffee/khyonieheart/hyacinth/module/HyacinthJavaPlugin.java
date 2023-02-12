package coffee.khyonieheart.hyacinth.module;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bridge between Hyacinth modules and Bukkit plugins.
 *
 * @author Khyonie 
 * @since 1.0.0
 */
public abstract class HyacinthJavaPlugin extends JavaPlugin implements HyacinthModule
{
	@Override
	public void onEnable()
	{
		super.onEnable();
	}

	public JavaPlugin asJavaPlugin()
	{
		return this;
	}

	public HyacinthModule asHyacinthModule()
	{
		return this;
	}
}
