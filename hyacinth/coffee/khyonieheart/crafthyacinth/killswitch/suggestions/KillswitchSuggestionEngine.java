package coffee.khyonieheart.crafthyacinth.killswitch.suggestions;

import java.util.ArrayList;
import java.util.List;

import coffee.khyonieheart.crafthyacinth.killswitch.KillswitchManager;
import coffee.khyonieheart.hyacinth.command.parser.SuggestionGenerator;
import coffee.khyonieheart.hyacinth.killswitch.KillswitchIdentifier;

public class KillswitchSuggestionEngine implements SuggestionGenerator
{
	@Override
	public List<String> generateSuggestions() 
	{
		List<String> results = new ArrayList<>();

		KillswitchManager.getAll().forEach((module, targets) -> {
			targets.forEach((target) -> {
				if (target.getClass().isAnnotationPresent(KillswitchIdentifier.class))
				{
					KillswitchIdentifier id = target.getClass().getAnnotation(KillswitchIdentifier.class);

					for (String s : id.value())
					{
						results.add(module.getSimpleName() + ":" + s);
					}

					return;
				}

				// Placeholder name
				results.add(module.getSimpleName() + ":killswitch");
			});
		});

		return results;
	}
}
