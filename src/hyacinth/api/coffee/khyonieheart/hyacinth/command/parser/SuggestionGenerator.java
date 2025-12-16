package coffee.khyonieheart.hyacinth.command.parser;

import java.util.List;

/**
 * A command argument suggestion generator.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public interface SuggestionGenerator
{
	/**
	 * Generates a list of suggestions.
	 *
	 * @return A String list of suggestions
	 */
	public List<String> generateSuggestions();
}
