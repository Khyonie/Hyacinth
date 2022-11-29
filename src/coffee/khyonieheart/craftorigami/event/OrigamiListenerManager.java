package coffee.khyonieheart.craftorigami.event;

import org.bukkit.event.Listener;

import coffee.khyonieheart.origami.Origami;

public class OrigamiListenerManager 
{
    public static void register(Listener listener)
    {
        // TODO Flesh this out a bit more
        Origami.getInstance().getServer().getPluginManager().registerEvents(listener, Origami.getInstance());
    }    
}
