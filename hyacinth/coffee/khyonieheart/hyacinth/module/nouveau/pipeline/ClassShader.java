package coffee.khyonieheart.hyacinth.module.nouveau.pipeline;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public interface ClassShader<T>
{
	/**
	 * Processes an individual class. If the class is instantiated here, return that instance to be passed to other shaders.
	 *
	 * @param clazz Class to process
	 * @param instance Instance object. Can be null.
	 *
	 * @return An instance of the given class, if one was instantiated. Return null otherwise.
	 */
	@Nullable
	public T process(
		@NotNull Class<? extends T> clazz,
		@Nullable T instance
	);

	public Class<T> getType();
}
