package coffee.khyonieheart.hyacinth.command.parser;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.option.Option;

/**
 * An attachment to a command branch that allows for argument validation.
 *
 * There are three extensions of this interface:
 * - ArgumentValidator, run only on tabcomplete suggestion generation,
 * - ExecutionValidator, run only on command execution, to check sent arguments,
 * - DualValidator, run on both execution and suggestion generation.
 */
public interface Validator
{
	/**
	 * Determines whether or not a given argument from a branch is valid.
	 *
	 * @param sender Command sender
	 * @param context Validator context, whether or not this validator is being run when suggestions are generated, or the command is executed.
	 * @param branch Completion branch that this argument belongs to
	 * @param argument Command argument
	 * @param argumentIndex Index of the argument being validated
	 * @param commandLabel The root label of the command
	 * @param arguments A complete array of arguments presented.
	 *
	 * @return See optional
	 * @hyacinth.optional String containing a message about why validation failed. State will be NONE if validation succeeded.
	 *
	 * @since 1.0.0
	 */
	public Option validate(CommandSender sender, ValidatorContext context, CompletionBranch branch, String argument, int argumentIndex, String commandLabel, String[] arguments);
}
