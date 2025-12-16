package coffee.khyonieheart.crafthyacinth.module;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.ModuleManager;

public class HyacinthModuleManager implements ModuleManager
{
	private static Map<String, HyacinthModule> modules = new HashMap<>();

	@Override
	public HyacinthModule getModule(
		String identifier
	) {
		return modules.get(identifier);
	}

	@Override
	public Collection<HyacinthModule> getModules() 
	{
		return Collections.unmodifiableCollection(modules.values());
	}

	@Override
	public void registerModule(
		HyacinthModule module
	) {
		modules.put(module.getConfiguration().getString("name"), module);
	}
}
