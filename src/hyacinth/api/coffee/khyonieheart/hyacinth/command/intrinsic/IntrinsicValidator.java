package coffee.khyonieheart.hyacinth.command.intrinsic;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;

public interface IntrinsicValidator
{
	public Option validate(
		@NotNull CommandSender sender, 
		@NotNull ValidatorContext context,
		@NotNull String commandLabel,
		int argsCount,
		String[] args
	);
}
