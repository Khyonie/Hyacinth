package coffee.khyonieheart.hyacinth.command.parser;

/**
 * Context for when a validator is run.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public enum ValidatorContext
{
	/** Validator is run during tab complete suggestion generation */
	TABCOMPLETE,
	/** Validator is run during command execution */
	EXECUTION,
	;
}
