package coffee.khyonieheart.crafthyacinth.command.intrinsic;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.command.intrinsic.IntrinsicValidator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.print.Grammar;

public class MinimumArgumentsIntrinsic implements IntrinsicValidator
{
	private final int minimumArgs;

	public MinimumArgumentsIntrinsic(int minimumArgs)
	{
		this.minimumArgs = minimumArgs;
	}

	@Override
	public Option validate(CommandSender sender, ValidatorContext context, String commandLabel, int argsCount, String[] args) 
	{
		return switch (context)
		{
			case TABCOMPLETE -> Option.none();
			case EXECUTION -> Option.some("§cCommand /" + commandLabel + " requires at least " + this.minimumArgs + Grammar.plural(this.minimumArgs, " argument, ", " arguments, ") + "however " + argsCount + Grammar.indicative(argsCount) + " provided.");
		};
	}
}
