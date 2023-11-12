package coffee.khyonieheart.crafthyacinth.module.nouveau;

import org.bukkit.event.Listener;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.nouveau.ClassCoordinator;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;

public class ListenerShader implements ClassShader<Listener>
{
	@Override
	public Listener process(
		Class<? extends Listener> clazz,
		Listener instance
	) {
		if (instance != null)
		{
			Hyacinth.getListenerManager().register(ClassCoordinator.getOwningModule(clazz).getModule().getClass(), instance);
			return null;
		}

		Logger.verbose("Shading listener class " + clazz.getName());
		Listener listener = Reflect.simpleInstantiate(clazz);
		Hyacinth.getListenerManager().register(ClassCoordinator.getOwningModule(clazz).getModule().getClass(), listener);

		return listener;
	}

	@Override
	public Class<Listener> getType() 
	{
		return Listener.class;
	}
}
