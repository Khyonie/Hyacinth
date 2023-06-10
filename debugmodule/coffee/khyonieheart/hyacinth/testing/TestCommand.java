package coffee.khyonieheart.hyacinth.testing;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.command.HyacinthCommand;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;

public class TestCommand extends HyacinthCommand
{
    public TestCommand() 
    {
        super("origami", "hyacinth.test");
    }

    public void origami(CommandSender sender, String[] args)
    {
        Message.send(sender, "Command executed");

		if (sender instanceof Player player)
		{
			player.kickPlayer("L + ratio");
		}
    }

    @Override
    public HyacinthModule getModule() 
    {
        return TestModule.getInstance();
    }
}
