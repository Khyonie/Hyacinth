package coffee.khyonieheart.crafthyacinth.killswitch.suggestions;

import java.util.ArrayList;
import java.util.List;

import coffee.khyonieheart.crafthyacinth.killswitch.FeatureManager;
import coffee.khyonieheart.hyacinth.command.parser.SuggestionGenerator;
import coffee.khyonieheart.hyacinth.killswitch.FeatureIdentifier;

public class KillswitchSuggestionEngine implements SuggestionGenerator
{
	@Override
	public List<String> generateSuggestions() 
	{
		List<String> results = new ArrayList<>();

		FeatureManager.getAll().forEach((module, targets) -> {
			targets.forEach((target) -> {
				if (target.getClass().isAnnotationPresent(FeatureIdentifier.class))
				{
					FeatureIdentifier id = target.getClass().getAnnotation(FeatureIdentifier.class);

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
