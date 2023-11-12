package coffee.khyonieheart.crafthyacinth.command.intrinsic;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.command.intrinsic.IntrinsicValidator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.util.Arrays;

/**
 * Intrinsic command validator that ensures all Hyacinth commands that support quoted arguments (I.E grouping arguments together as a single 
 * string with start/stop quotation marks) contains its quotes in correct locations.
 *
 * @since 1.0.0
 * @author Khyonie
 *
 * @deprecated Will be supersceded by Tidal 2.0.
 */
@Deprecated
public class ConcatenationIntrinsic implements IntrinsicValidator
{
	@Override
	public Option validate(
		CommandSender sender, 
		ValidatorContext context, 
		String commandLabel, 
		int argsCount, 
		String[] args
	) {
		boolean started = false;
		int startPosition = -1;

		for (int i = 0; i < argsCount; i++)
		{
			String arg = args[i];

			// Check for start quote
			if (arg.startsWith("\""))
			{
				if (started)
				{
					return switch (context) {
						case EXECUTION -> {
							args[i] = "§n" + arg + ("§r§c (← Here)");
							yield Option.some("§cUnexpected quoted argument start in /" + commandLabel + " " + Arrays.toString(args, " ", null) + ".");
						}
						case TABCOMPLETE -> Option.some("§c(⚠ Unexpected quoted argument start at position " + i + ")");
					};
				}

				startPosition = i;
				started = true;
			}

			// Check for end quote
			if (arg.endsWith("\""))
			{
				if (!started)
				{
					return switch (context)
					{
						case EXECUTION -> {
							args[i] = "§n" + arg + "§r§c (← Here)";
							yield Option.some("§cUnexpected quoted argument end in /" + commandLabel + " " + Arrays.toString(args, " ", null) + ".");
						}
						case TABCOMPLETE -> Option.some("§c(⚠ Unexpected quoted argument end at position " + i + ": " + (arg) + ")");
					};
				}

				started = false;
				startPosition = -1;
			}
		}

		if (started)
		{
			return switch (context)
			{
				case EXECUTION -> {
					args[startPosition] = "§n" + args[startPosition] + "§r§c (← Started here)§n";
					yield Option.some("§cQuoted argument is not properly closed in /" + commandLabel + " " + Arrays.toString(args, " ", null) + ".");
				}
				case TABCOMPLETE -> Option.some("§c(⚠ Quoted argument is not properly closed starting at position " + startPosition + ")");
			};
		}

		return Option.none();
	}
}
