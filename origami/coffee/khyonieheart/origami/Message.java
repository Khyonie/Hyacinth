package coffee.khyonieheart.origami;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Utilities for sending messages to entities.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Message 
{
    public static void send(CommandSender target, String message)
    {
        if (target instanceof ConsoleCommandSender)
        {
            Logger.log(message);
            return;
        }

        target.sendMessage(message);
    }   
}
