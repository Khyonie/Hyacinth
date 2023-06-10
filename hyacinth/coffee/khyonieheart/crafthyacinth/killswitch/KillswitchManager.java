package coffee.khyonieheart.crafthyacinth.killswitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coffee.khyonieheart.hyacinth.killswitch.KillswitchTarget;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;

public class KillswitchManager
{
	private static Map<Class<? extends HyacinthModule>, List<KillswitchTarget>> registeredKillswitches = new HashMap<>();

	public static void register(Class<? extends HyacinthModule> module, KillswitchTarget target)
	{
		if (!registeredKillswitches.containsKey(module))
		{
			registeredKillswitches.put(module, new ArrayList<>());
		}

		registeredKillswitches.get(module).add(target);
	}

	public static List<KillswitchTarget> get(Class<? extends HyacinthModule> module)
	{
		return registeredKillswitches.get(module);
	}

	public static Map<Class<? extends HyacinthModule>, List<KillswitchTarget>> getAll()
	{
		return registeredKillswitches;
	}
}
