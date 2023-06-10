package coffee.khyonieheart.crafthyacinth.command.parser.validator;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.DualValidator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.util.Arrays;

public class NumberValidator
{
	public static DualValidator longValidator()
	{
		return new DualValidator() {
			@Override
			public Option validate(CommandSender sender, ValidatorContext context, CompletionBranch branch, String argument, int argumentIndex, String commandLabel, String[] args)
			{
				try {
					Long.parseLong(argument);
					return Option.none();
				} catch (NumberFormatException e) {
					if (context.equals(ValidatorContext.TABCOMPLETE))
					{
						return Option.some("§c(⚠ Invalid long number \"" + argument + "\")");
					}

					args[argumentIndex] = "§n" + args[argumentIndex] + "§r§c (← Here)";
					return Option.some("§cInvalid long number \"" + argument + "\" in \"/" + commandLabel + " " + Arrays.toString(args, " ", null) + "\".");
				}
			}
		};
	}

	public static DualValidator integerValidator()
	{
		return new DualValidator() {
			@Override
			public Option validate(CommandSender sender, ValidatorContext context, CompletionBranch branch, String argument, int argumentIndex, String commandLabel, String[] args)
			{
				try {
					Integer.parseInt(argument);
					return Option.none();
				} catch (NumberFormatException e) {
					if (context.equals(ValidatorContext.TABCOMPLETE))
					{
						return Option.some("§c(⚠ Invalid integer \"" + argument + "\")");
					}

					args[argumentIndex] = "§n" + args[argumentIndex] + "§r§c (← Here)";
					return Option.some("§cInvalid integer \"" + argument + "\" in \"/" + commandLabel + " " + Arrays.toString(args, " ", null) + "\".");
				}
			}
		};
	}

	public static DualValidator doubleValidator()
	{
		return new DualValidator() {
			@Override
			public Option validate(CommandSender sender, ValidatorContext context, CompletionBranch branch, String argument, int argumentIndex, String commandLabel, String[] args)
			{
				try {
					Double.parseDouble(argument);
					return Option.none();
				} catch (NumberFormatException e) {
					if (context.equals(ValidatorContext.TABCOMPLETE))
					{
						return Option.some("§c(⚠ Invalid double \"" + argument + "\")");
					}

					args[argumentIndex] = "§n" + args[argumentIndex] + "§r§c (← Here)";
					return Option.some("§cInvalid double \"" + argument + "\" in \"/" + commandLabel + " " + Arrays.toString(args, " ", null) + "\".");
				}
			}
		};
	}
}
