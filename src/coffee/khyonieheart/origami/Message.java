package coffee.khyonieheart.origami;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class Message 
{
    public static void send(CommandSender target, String message)
    {
        // switch (target)
        // {
        //     case ConsoleCommandSender s -> s.sendMessage(message);
        //     default -> target.sendMessage(message);
        // }

        if (target instanceof ConsoleCommandSender)
        {
            Logger.log(message);
            return;
        }

        target.sendMessage(message);
    }   
}
