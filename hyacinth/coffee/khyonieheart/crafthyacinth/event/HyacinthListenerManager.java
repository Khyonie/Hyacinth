package coffee.khyonieheart.crafthyacinth.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Listener;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;

/**
 * Listener management, registration, and unregistration.
 *
 * @author Khyonie 
 * @since 1.0.0
 */
public class HyacinthListenerManager 
{
	private static Map<Class<? extends HyacinthModule>, List<Listener>> data = new HashMap<>();

    public static void register(Class<? extends HyacinthModule> moduleClass, Listener listener)
    {
		if (!data.containsKey(moduleClass))
		{
			data.put(moduleClass, new ArrayList<>());
		}

		data.get(moduleClass).add(listener);
        Hyacinth.getInstance().getServer().getPluginManager().registerEvents(listener, Hyacinth.getInstance());
    }    
}
