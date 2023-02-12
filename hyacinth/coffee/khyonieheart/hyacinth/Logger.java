package coffee.khyonieheart.hyacinth;

/**
 * Various utilities related to logging.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Logger 
{
	// TODO Add persistent logs

	/**
	 * Prints a message in the console and adds it to the log.
	 *
	 * @param message Message to be logged.
	 */
    public static void log(String message)
    {
        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage(Hyacinth.getConfig("regularLoggingFlavor", String.class) + message);
    }   
    
	/**
	 * Prints a message in the console if verbose logging is enabled, and adds it to the log.
	 *
	 * @param message Message to be logged.
	 */
    public static void verbose(String message)
    {
        if (!Hyacinth.getConfig("enableVerboseLogging", Boolean.class))
        {
            return;
        }

        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage(Hyacinth.getConfig("verboseLoggingFlavor", String.class) + message);
    }

    /**
	 * Prints a message in the console.
	 *
	 * @param message Message to be logged.
	 *
     * @deprecated Remove all debug messages before releasing your module.
     */
    @Deprecated(forRemoval = false)
    public static void debug(String message)
    {
        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage("§1Hyacinth §a> §d DEBUG §8 §8> §c" + message);
    }

    /**
	 * Prints a message in the console.
	 *
	 * @param message Message to be logged.
	 *
     * @deprecated Resolve all todo messages before releasing your module. 
     */
    @Deprecated(forRemoval = false)
    public static void todo(String message)
    {
        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage("§cHyacinth §a> §4 TO-DO §8 §8> §c" + message);
    }
}
