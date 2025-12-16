package coffee.khyonieheart.crafthyacinth.module.nouveau;

import coffee.khyonieheart.crafthyacinth.killswitch.FeatureManager;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.killswitch.Feature;
import coffee.khyonieheart.hyacinth.module.nouveau.ClassCoordinator;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.util.Reflect;

public class FeatureShader implements ClassShader<Feature>
{
	@Override
	public Feature process(
		Class<? extends Feature> clazz,
		Feature instance
	) {
		if (instance != null)
		{
			FeatureManager.register(ClassCoordinator.getOwningModule(clazz).getModule().getClass(), instance);
			return null;
		}

		Logger.verbose("Shading feature class " + clazz.getName());
		Feature object = Reflect.simpleInstantiate(clazz);
		FeatureManager.register(ClassCoordinator.getOwningModule(clazz).getModule().getClass(), object);

		return object;
	}

	@Override
	public Class<Feature> getType() 
	{
		return Feature.class;
	}
}
