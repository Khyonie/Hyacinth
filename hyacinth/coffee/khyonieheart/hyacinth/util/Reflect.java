package coffee.khyonieheart.hyacinth.util;

import java.lang.reflect.Constructor;

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
}
