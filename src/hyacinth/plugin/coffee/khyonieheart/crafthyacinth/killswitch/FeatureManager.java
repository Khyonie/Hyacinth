package coffee.khyonieheart.crafthyacinth.killswitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.hyacinth.killswitch.Feature;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;

public class FeatureManager
{
	private static Map<Class<? extends HyacinthModule>, List<Feature>> registeredKillswitches = new HashMap<>();

	public static void register(
		@NotNull Class<? extends HyacinthModule> module, 
		@NotNull Feature target
	) {
		Objects.requireNonNull(module);
		Objects.requireNonNull(target);

		if (!registeredKillswitches.containsKey(module))
		{
			registeredKillswitches.put(module, new ArrayList<>());
		}

		registeredKillswitches.get(module).add(target);
	}

	public static List<Feature> get(
		@NotNull Class<? extends HyacinthModule> module
	) {
		Objects.requireNonNull(module);

		return registeredKillswitches.get(module);
	}

	public static Map<Class<? extends HyacinthModule>, List<Feature>> getAll()
	{
		return registeredKillswitches;
	}
}
