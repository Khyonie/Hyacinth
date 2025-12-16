package coffee.khyonieheart.crafthyacinth.module.nouveau;

import org.bukkit.command.Command;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.ModuleOwned;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;

public class CommandShader<T extends Command & ModuleOwned> implements ClassShader<Command>
{
	@SuppressWarnings("unchecked")
	@Override
	public Command process(
		Class<? extends Command> clazz,
		Command instance
	) {
		if (!ModuleOwned.class.isAssignableFrom(clazz))
		{
			Logger.verbose("Skipping non-module-owned command class " + clazz.getName());
			return null;
		}

		if (instance != null)
		{
			Hyacinth.getCommandManager().register(instance.getName(), (T) instance, Hyacinth.getInstance().getServer());
			return null;
		}

		Logger.verbose("Shading command class " + clazz.getName());
		Class<T> typedCommand = (Class<T>) clazz;
		T object = Reflect.simpleInstantiate(typedCommand);
		Hyacinth.getCommandManager().register(object.getName(), object, Hyacinth.getInstance().getServer());

		return object;
	}

	@Override
	public Class<Command> getType() 
	{
		return Command.class;
	}
}
