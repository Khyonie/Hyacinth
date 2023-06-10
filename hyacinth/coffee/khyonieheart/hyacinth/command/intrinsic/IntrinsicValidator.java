package coffee.khyonieheart.hyacinth.command.intrinsic;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

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
