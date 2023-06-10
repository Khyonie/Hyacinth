package coffee.khyonieheart.crafthyacinth.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Listener;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Listener management, registration, and unregistration.
 *
 * @author Khyonie 
 * @since 1.0.0
 */
public class HyacinthListenerManager 
{
	private static Map<Class<? extends HyacinthModule>, List<Listener>> data = new HashMap<>();

	/**
	 * Registers a listener to a module.
	 *
	 * @param moduleClass Module class listener belongs to
	 * @param listener Listener to register
	 *
	 * @since 1.0.0
	 */
    public static void register(
		@NotNull Class<? extends HyacinthModule> moduleClass, 
		@NotNull Listener listener
	) {
		if (!data.containsKey(moduleClass))
		{
			data.put(moduleClass, new ArrayList<>());
		}

		data.get(moduleClass).add(listener);
        Hyacinth.getInstance().getServer().getPluginManager().registerEvents(listener, Hyacinth.getInstance());
    }    
}
