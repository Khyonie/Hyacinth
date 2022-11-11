package coffee.khyonieheart.origami;

import coffee.khyonieheart.origami.enums.ConfigurationType;

public class Logger 
{
    public static void log(String message)
    {
        Origami.getInstance().getServer().getConsoleSender().sendMessage(Origami.getConfig("regularLoggingFlavor", ConfigurationType.STRING).unwrap(String.class) + message);
    }   
    
    public static void verbose(String message)
    {
        Origami.getInstance().getServer().getConsoleSender().sendMessage(Origami.getConfig("verboseLoggingFlavor", ConfigurationType.STRING).unwrap(String.class) + message);
    }

    /**
     * @deprecated Remove all debug messages before releasing your module.
     */
    @Deprecated(forRemoval = false)
    public static void debug(String message)
    {
        Origami.getInstance().getServer().getConsoleSender().sendMessage("§1Origami §8> §d DEBUG §8 §8> §c" + message);
    }
}
