package coffee.khyonieheart.hyacinth.listener;

import org.bukkit.event.Listener;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public interface ListenerManager
{
	/**
	 * Registers a listener to a module.
	 *
	 * @param moduleClass Module class listener belongs to
	 * @param listener Listener to register
	 *
	 * @since 1.0.0
	 */
    public void register(
		@NotNull Class<? extends HyacinthModule> moduleClass, 
		@NotNull Listener listener
	);
}
