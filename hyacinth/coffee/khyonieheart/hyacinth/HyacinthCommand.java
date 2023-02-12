package coffee.khyonieheart.hyacinth;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.crafthyacinth.module.HyacinthCoreModule;
import coffee.khyonieheart.hyacinth.command.NoSubCommandExecutor;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;

/**
 * Internal Hyacinth command, tied to hyacinth core module.
 *
 * @since 1.0.0
 * @author Khyonie
 */
final class HyacinthCommand extends coffee.khyonieheart.hyacinth.command.HyacinthCommand
{
	/**
	 * Constructor
	 */
	public HyacinthCommand() 
	{
		super("hyacinth", "/hyacinth", "hyacinth.cmd", "h", "hya");
	}

	/**
	 * No subcommand executor.
	 *
	 * @param sender Command sender
	 * @param args Command args
	 */
	@NoSubCommandExecutor
	public void noSubCommand(CommandSender sender, String[] args)
	{
		Message.send(sender, "§9❀ Hyacinth " + Hyacinth.getMetadata().getString("version"));
		Message.send(sender, "§9❀ Because plugin development should make sense.");
	}

	@Override
	public HyacinthModule getModule() 
	{
		return HyacinthCoreModule.getInstance();
	}
}
