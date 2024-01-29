package coffee.khyonieheart.hyacinth.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import coffee.khyonieheart.hyacinth.exception.InstantiationRuntimeException;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Various reflection utilities.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class Reflect 
{
    /**
     * Instantiates a class reflectively. This method attempts to find an appropriate constructor automatically.
     * @param <T> Type of object to instantiate
     * @param clazz Class of object
     * @param args Arguments to be passed to T's constructor
     * @return An object of type T
     * 
     * @throws InstantiationRuntimeException Thrown when another exception is thrown.
     * 
     * @implNote Because of how {@link Class#getConstructor(Class...)} works, downcasting arguments may be necessary.
	 *
	 * @since 1.0.0
     */
	@NotNull
    public static <T> T simpleInstantiate(
        @NotNull Class<T> clazz, 
        Object... args
    )
        throws InstantiationRuntimeException
    {
        Class<?>[] classArgs = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
        {
            classArgs[i] = args[i].getClass();
        }

        return simpleInstantiate(clazz, classArgs, args);
    }   
    
    /**
     * Instantiates an object reflectively.
	 *
     * @param <T> Type of object to instantiate
	 *
     * @param clazz Class of object
     * @param constructorArgs Class arguments of constructor to obtain
     * @param args Arguments to be passed to T's constructor
     * 
	 * @return An object of type T
     * 
	 * @throws InstantiationRuntimeException Thrown when another exception is thrown.
     * 
     * @see Reflect#simpleInstantiate(Class, Object...)
     */
	@NotNull
    public static <T> T simpleInstantiate(
        @NotNull Class<T> clazz,
        @NotNull Class<?>[] constructorArgs,
        Object... args
    )
        throws InstantiationRuntimeException
    {
        if (constructorArgs == null)
        {
            constructorArgs = new Class<?>[0];
        }

        T obj;

        try {
            Constructor<T> constructor = clazz.getConstructor(constructorArgs);

            obj = constructor.newInstance(args);
        } catch (Exception e) {
            throw new InstantiationRuntimeException(e);
        }

        return obj;
    }

	private static final Set<Class<?>> SERIALIZABLE_TYPES = Set.of(
		Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, String.class
	);

	@SuppressWarnings("unchecked")
	public static <T> T instantiateFromMap(
		@NotNull Class<T> type,
		@NotNull Map<String, Object> data
	)
		throws NoSuchMethodException
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(data);

		Constructor<T> constructor;
		try {
			constructor = type.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			constructor = type.getConstructor();
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}

		constructor.setAccessible(true);
		T obj;
		try {
			obj = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

		for (String fieldName : data.keySet())
		{
			Field field;
			try
			{
				field = type.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				try {
					field = type.getField(fieldName);
				} catch (NoSuchFieldException e2) {
					continue;
				}
			} catch (SecurityException e) {
				e.printStackTrace();
				continue;
			}

			field.setAccessible(true);
			try {
				if (!SERIALIZABLE_TYPES.contains(field.getType()))
				{
					field.set(obj, instantiateFromMap(field.getType(), (Map<String, Object>) data.get(fieldName)));
					continue;
				}
				field.set(obj, data.get(fieldName));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
		}

		return obj;
	}
}
