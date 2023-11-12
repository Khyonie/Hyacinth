package coffee.khyonieheart.tidal.validation;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.tidal.CommandExecutionIssue;

public interface ArgumentValidator
{
	public CommandExecutionIssue validateExecution(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args);

	public CommandExecutionIssue validateTabcomplete(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args);
}
