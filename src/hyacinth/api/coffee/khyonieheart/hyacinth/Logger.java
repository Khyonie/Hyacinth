package coffee.khyonieheart.hyacinth;

import coffee.khyonieheart.hyacinth.module.nouveau.ClassCoordinator;
import coffee.khyonieheart.hyacinth.module.nouveau.ModuleFile;

/**
 * Various utilities related to logging.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Logger 
{
	private static String previousClass;

	/**
	 * Prints a message in the console and adds it to the log.
	 *
	 * @param message Message to be logged.
	 */
    public static void log(String message)
    {
		ModuleFile owner = ClassCoordinator.getOwningModule(Thread.currentThread().getStackTrace()[2].getClassName());
		String name = (owner == null ? "Hyacinth" : owner.getConfiguration().getString("name"));

		String[] classNameSplit = Thread.currentThread().getStackTrace()[2].getClassName().split("\\.");
		String className = classNameSplit[classNameSplit.length - 1];
		char color = className.equals(previousClass) ? '8' : 'e'; 
		String contextLine = color == '8' ? "§e├ " : "§e┌ ";

        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage(Hyacinth.getConfig("regularLoggingFlavor", String.class).replace("%MODULE", name).replace("%CLASS", contextLine + "§" + color + className).replace("%METHOD", "§" + color + Thread.currentThread().getStackTrace()[2].getMethodName()) + message);

		previousClass = className;
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

		ModuleFile owner = ClassCoordinator.getOwningModule(Thread.currentThread().getStackTrace()[2].getClassName());
		String name = (owner == null ? "Hyacinth" : owner.getConfiguration().getString("name"));

		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		String[] classNameSplit = element.getClassName().split("\\.");
		String className = classNameSplit[classNameSplit.length - 1];
		char color = className.equals(previousClass) ? '8' : 'e'; 
		String contextLine = color == '8' ? "§e├ " : "§e┌ ";


        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage(Hyacinth.getConfig("verboseLoggingFlavor", String.class).replace("%MODULE", name).replace("%CLASS", contextLine + "§" + color + className).replace("%METHOD", "§" + color + element.getMethodName() + ":§6" + element.getLineNumber()) + message);

		previousClass = className;
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
		ModuleFile owner = ClassCoordinator.getOwningModule(Thread.currentThread().getStackTrace()[2].getClassName());
		String name = (owner == null ? "Hyacinth" : owner.getConfiguration().getString("name"));

		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		String[] classNameSplit = element.getClassName().split("\\.");
		String className = classNameSplit[classNameSplit.length - 1];
		char color = className.equals(previousClass) ? '8' : 'e'; 
		String contextLine = color == '8' ? "§e├ " : "§e┌ ";

        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage(Hyacinth.getConfig("debugLoggingFlavor", String.class).replace("%MODULE", name).replace("%CLASS", contextLine + "§" + color + className).replace("%METHOD", "§" + color + element.getMethodName() + ":§4" + element.getLineNumber()) + message);

		previousClass = className;
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
		ModuleFile owner = ClassCoordinator.getOwningModule(Thread.currentThread().getStackTrace()[2].getClassName());
		String name = (owner == null ? "Hyacinth" : owner.getConfiguration().getString("name"));

		String[] classNameSplit = Thread.currentThread().getStackTrace()[2].getClassName().split("\\.");
		String className = classNameSplit[classNameSplit.length - 1];
		char color = className.equals(previousClass) ? '8' : 'e'; 
		String contextLine = color == '8' ? "§e├ " : "§e┌ ";

        Hyacinth.getInstance().getServer().getConsoleSender().sendMessage(Hyacinth.getConfig("todoLoggingFlavor", String.class).replace("%MODULE", name).replace("%CLASS", contextLine + "§" + color + className).replace("%METHOD", "§" + color + Thread.currentThread().getStackTrace()[2].getMethodName()) + message);

		previousClass = className;
    }
}
