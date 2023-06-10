package coffee.khyonieheart.crafthyacinth.command.parser.validator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.ExecutionValidator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;

public class UserValidator
{
	private static final String IS_NOT_PLAYER_SUGGESTION = "§c(⚠ Only players may use this command)";
	private static final String IS_NOT_PLAYER_EXECUTION = "§cOnly players may use this command.";

	public static ExecutionValidator isPlayer()
	{
		return new ExecutionValidator() {
			@Override
			public Option validate(CommandSender sender, ValidatorContext context, CompletionBranch branch, String argument, int argumentIndex, String commandLabel, String[] args)
			{
				return (sender instanceof Player) ? Option.none() : Option.some(context.equals(ValidatorContext.TABCOMPLETE) ? IS_NOT_PLAYER_SUGGESTION : IS_NOT_PLAYER_EXECUTION);
			}
		};
	}
}
