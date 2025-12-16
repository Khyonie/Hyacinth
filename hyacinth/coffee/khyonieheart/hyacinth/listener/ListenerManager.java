package coffee.khyonieheart.hyacinth.listener;

import org.bukkit.event.Listener;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;

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
