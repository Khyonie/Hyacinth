package coffee.khyonieheart.crafthyacinth.command.parser.validator;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.DualValidator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;

public class PermissionValidator implements DualValidator
{
	private static final String NO_PERMISSION_SUGGESTION = "§c(⚠ Missing authorization to use this command)";
	private static final String NO_PERMISSION_EXECUTION = "§cYou do not have permission to execute this command.";

	private String permission;

	public PermissionValidator(String permission, boolean silent)
	{
		this.permission = permission;
	}

	public String getPermission()
	{
		return this.permission;
	}

	@Override
	public Option validate(CommandSender sender, ValidatorContext context, CompletionBranch branch, String argument, int argumentIndex, String commandLabel, String[] arguments) 
	{
		return sender.hasPermission(permission) ? Option.none() : Option.some(context.equals(ValidatorContext.TABCOMPLETE) ? NO_PERMISSION_SUGGESTION : NO_PERMISSION_EXECUTION);
	}
}
