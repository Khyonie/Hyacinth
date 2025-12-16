package coffee.khyonieheart.hyacinth.module;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.module.nouveau.ClassCoordinator;

/**
 * Represents a loaded module.
 * @author Khyonie
 * @since 1.0.0
 */
public interface HyacinthModule
{
    public void onEnable();

    public void onDisable();

	public default YamlConfiguration getConfiguration()
	{
		return ClassCoordinator.getOwningModule(this.getClass()).getConfiguration();
	}
}
