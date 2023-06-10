package coffee.khyonieheart.crafthyacinth.command.parser.validator;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.DualValidator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.print.Grammar;

public class CommandValidator
{
	private static final String SUGGESTION_MESSAGE = "§c(⚠ Not enough arguments for command)";

	public static DualValidator argsCount(int minArgs)
	{
		return new DualValidator() 
		{
			@Override
			public Option validate(CommandSender sender, ValidatorContext context, CompletionBranch branch, String argument, int argumentIndex, String commandLabel, String[] args) 
			{
				return args.length < minArgs ? Option.some(context.equals(ValidatorContext.TABCOMPLETE) ? SUGGESTION_MESSAGE : "§cCommand \"/" + commandLabel + " " + args[0] + "\" requires a minimum of " + (minArgs - 1) + Grammar.plural(minArgs - 1, " argument, however ", "arguments, however ") + (args.length - 1) + " " + Grammar.indicative(args.length - 1) + " provided.") : Option.none();
			}
		};
	}
}
