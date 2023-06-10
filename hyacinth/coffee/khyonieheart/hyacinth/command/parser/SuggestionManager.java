package coffee.khyonieheart.hyacinth.command.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

/**
 * Utility class that handles registration of tab complete suggestion generators.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class SuggestionManager
{
	private static Map<String, SuggestionGenerator> suggestionEngines = new HashMap<>();

	/**
	 * Registers a suggestion engine. 
	 * By convention, engine labels should be UPPER_CASE, and names should be unique.
	 *
	 * @param label UPPER_CASE engine label
	 * @param engine Suggestion engine
	 *
	 * @since 1.0.0
	 */
	public static void register(
		@NotNull String label, 
		@NotNull SuggestionGenerator engine
	) {
		suggestionEngines.put(label, engine);
	}

	/**
	 * Obtains a suggestion engine by name.
	 *
	 * @param label UPPER_CASE engine label
	 *
	 * @return A suggestion engine. May return null.
	 */
	@Nullable
	public static SuggestionGenerator getSuggestionEngine(
		@NotNull String label
	) {
		return suggestionEngines.get(label);
	}

	/**
	 * Checks whether a suggestion engine exists by the given label.
	 *
	 * @param label UPPER_CASE engine label
	 *
	 * @return True if engine exists by the given label, false if no such engine exists
	 */
	public static boolean hasEngine(
		@NotNull String label
	) {
		return suggestionEngines.containsKey(label);
	}

	/**
	 * Obtains a list of all suggestion engines available.
	 *
	 * @return A list of all suggestion engines by name.
	 */
	@NotNull
	public static List<String> getAvailableEngines()
	{
		return new ArrayList<>(suggestionEngines.keySet());
	}
}
