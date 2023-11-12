package coffee.khyonieheart.hyacinth.module.nouveau;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import coffee.khyonieheart.crafthyacinth.module.HyacinthCoreModule;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class ClassCoordinator
{
	private static Map<String, NouveauClassloader> activeClassloaders = new HashMap<>();
	private static Map<Class<?>, NouveauClassloader> classRelations = new HashMap<>();
	private static Map<String, Class<?>> cachedClasses = new HashMap<>();

	public static void addClassloader(
		@NotNull String identifier,
		@NotNull NouveauClassloader classloader
	) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(classloader);

		activeClassloaders.put(identifier, classloader);
	}

	public static Class<?> getGlobalClass(
		@NotNull String name,
		@NotNull ModuleFile accessor
	) 
		throws ClassNotFoundException
	{
		Objects.requireNonNull(name);
		Objects.requireNonNull(accessor);

		Class<?> result = cachedClasses.get(name);

		if (result != null)
		{
			try {
				checkAccess(result, accessor);
			} catch (IllegalAccessException e) {
				throw new ClassNotFoundException("Class access disallowed", e);
			}

			return result;
		}


		for (NouveauClassloader classloader : activeClassloaders.values())
		{
			try {
				result = classloader.findClass(name, false);

				if (result == null)
				{
					continue;
				}	
			} catch (ClassNotFoundException e) {
				continue;
			}

			classRelations.put(result, classloader);
			cachedClasses.put(name, result);
			
			try {
				checkAccess(result, accessor);
			} catch (IllegalAccessException e) {
				throw new ClassNotFoundException("Class access disallowed", e);
			}

			return result;
		}

		throw new ClassNotFoundException();
	}

	@NotNull
	public static ModuleFile getOwningModule(
		@NotNull Class<?> clazz
	) {
		if (classRelations.containsKey(clazz))
		{
			return classRelations.get(clazz).getModuleFile();
		}

		return HyacinthCoreModule.getModuleFile();
	}

	@NotNull
	public static ModuleFile getOwningModule(
		@NotNull String classPath
	) {
		if (!cachedClasses.containsKey(classPath))
		{
			return HyacinthCoreModule.getModuleFile();
		}

		return getOwningModule(cachedClasses.get(classPath));
	}

	static Collection<NouveauClassloader> getClassloaders()
	{
		return activeClassloaders.values();
	}

	private static void checkAccess(
		@NotNull Class<?> clazz,
		@NotNull ModuleFile accessor
	)
		throws IllegalAccessException
	{
		if (clazz.isAnnotationPresent(ModuleAccess.class))
		{
			switch (clazz.getAnnotation(ModuleAccess.class).value())
			{
				case PUBLIC -> { return; }
				case PROTECTED -> {
					ModuleFile relation = classRelations.get(clazz).getModuleFile();

					if (relation.equals(accessor))
					{
						return;
					}

					if (relation.getConfiguration().getString("package").equals(accessor.getConfiguration().getString("package")))
					{
						return;
					}
				}
				case PRIVATE -> {
					if (classRelations.get(clazz).getModuleFile().equals(accessor))
					{
						return;
					}
				}
			}
			throw new IllegalAccessException("Module " + accessor.getConfiguration().getString("name") + " cannot access class " + clazz.getName() + "(owned by " + accessor.getConfiguration().getString("name") + ") with access flag " + clazz.getAnnotation(ModuleAccess.class).value().name());
		}
	}
}
