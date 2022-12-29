package coffee.khyonieheart.origami.testing;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.origami.Message;
import coffee.khyonieheart.origami.command.OrigamiCommand;
import coffee.khyonieheart.origami.module.OrigamiModule;
import net.md_5.bungee.api.ChatColor;

public class TestCommand extends OrigamiCommand
{
    public TestCommand() 
    {
        super("origami");
    }

    public void origami(CommandSender sender, String[] args)
    {
        Message.send(sender, "Command executed");
    }

    @Override
    public OrigamiModule getModule() 
    {
        return TestModule.getInstance();
    }
}
