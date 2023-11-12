package coffee.khyonieheart.hyacinth;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Utilities for sending messages to entities.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Message 
{
	/**
	 * Sends a message to the given command sender.
	 *
	 * @param target Message recipient
	 * @param message Message to be sent
	 *
	 * @since 1.0.0
	 */
    public static void send(CommandSender target, String message)
    {
        if (target instanceof ConsoleCommandSender)
        {
            Logger.log(message);
            return;
        }

        target.sendMessage(message);
    }   

	public static void send(CommandSender target, BaseComponent[] message)
	{
		target.spigot().sendMessage(message);
	}
}
