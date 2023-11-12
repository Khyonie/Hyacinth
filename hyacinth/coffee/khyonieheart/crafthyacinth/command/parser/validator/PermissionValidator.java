package coffee.khyonieheart.crafthyacinth.command.parser.validator;

import java.util.Objects;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.DualValidator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Validator collection that verifies a user's permission.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class PermissionValidator implements DualValidator
{
	private static final String NO_PERMISSION_SUGGESTION = "§c(⚠ Missing authorization to use this command)";
	private static final String NO_PERMISSION_EXECUTION = "§cYou do not have permission to execute this command.";

	private String permission;

	public PermissionValidator(
		@NotNull String permission, 
		boolean silent
	) {
		Objects.requireNonNull(permission);
		this.permission = permission;
	}

	@NotNull
	public String getPermission()
	{
		return this.permission;
	}

	@Override
	public Option validate(
		CommandSender sender, 
		ValidatorContext context, 
		CompletionBranch branch, 
		String argument, 
		int argumentIndex, 
		String commandLabel, 
		String[] arguments
	) {
		return sender.hasPermission(permission) ? Option.none() : Option.some(context.equals(ValidatorContext.TABCOMPLETE) ? NO_PERMISSION_SUGGESTION : NO_PERMISSION_EXECUTION);
	}
}
