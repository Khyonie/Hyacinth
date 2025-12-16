package coffee.khyonieheart.crafthyacinth.module.nouveau;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.nouveau.ClassCoordinator;
import coffee.khyonieheart.hyacinth.module.nouveau.ModuleFile;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;

public class ModuleShader implements ClassShader<HyacinthModule>
{
	@Override
	public HyacinthModule process(
		Class<? extends HyacinthModule> clazz,
		HyacinthModule instance
	) {
		if (instance != null)
		{
			attach(clazz, instance);
			Hyacinth.getModuleManager().registerModule(instance);
			return null;
		}

		Logger.verbose("Shading module class " + clazz.getName());
		HyacinthModule module = Reflect.simpleInstantiate(clazz);
		attach(clazz, module);
		Hyacinth.getModuleManager().registerModule(module);

		return module;
	}

	@Override
	public Class<HyacinthModule> getType() 
	{
		return HyacinthModule.class;
	}

	private void attach(
		Class<? extends HyacinthModule> clazz,
		HyacinthModule module
	) {
		ModuleFile moduleFile = ClassCoordinator.getOwningModule(clazz);
		try {
			Method method = moduleFile.getClass().getDeclaredMethod("attachModule", HyacinthModule.class);
			method.setAccessible(true);

			method.invoke(moduleFile, module);
		} catch (SecurityException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
