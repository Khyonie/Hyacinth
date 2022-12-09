package coffee.khyonieheart.origami;

public class Logger 
{
    public static void log(String message)
    {
        Origami.getInstance().getServer().getConsoleSender().sendMessage(Origami.getConfig("regularLoggingFlavor", String.class) + message);
    }   
    
    public static void verbose(String message)
    {
        if (!Origami.getConfig("enableVerboseLogging", Boolean.class))
        {
            return;
        }

        Origami.getInstance().getServer().getConsoleSender().sendMessage(Origami.getConfig("verboseLoggingFlavor", String.class) + message);
    }

    /**
     * @deprecated Remove all debug messages before releasing your module.
     */
    @Deprecated(forRemoval = false)
    public static void debug(String message)
    {
        Origami.getInstance().getServer().getConsoleSender().sendMessage("§1Origami §a> §d DEBUG §8 §8> §c" + message);
    }

    /**
     * @deprecated Resolve all todo messages before releasing your module. 
     */
    @Deprecated(forRemoval = false)
    public static void todo(String message)
    {
        Origami.getInstance().getServer().getConsoleSender().sendMessage("§cOrigami §a> §4 TO-DO §8 §8> §c" + message);
    }
}
