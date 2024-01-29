package coffee.khyonieheart.hyacinth.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import coffee.khyonieheart.hyacinth.api.RuntimeConditions;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.hyacinth.util.marker.Range;

/**
 * Various utilities for arrays.
 *
 * @since 1.0.0
 * @author Khyonie
 */
public class Arrays 
{
    /**
     * Formats an array to a string representation with an optional function to modify each array object,
     * otherwise normal Java {@code toString()} will be used.
     * @param <T> Type of array
     * @param array Array to format
     * @param delimeter String to separate individual objects in array
     * @param mapper Optional 
     * @return String-formatted array
     */
	@NotNull
    public static <T> String toString(
        @NotNull T[] array, 
        @NotNull String delimeter,
        @Nullable Function<T, String> mapper
    ) {
        if (array.length == 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        ArrayIterator<T> iter = iterator(array);
        while (iter.hasNext())
        {
            try {
                builder.append(mapper != null ? mapper.apply(iter.next()) : iter.next());
            } catch (Exception e) {
                builder.append("--- EXCEPTION THROWN @ INDEX " + iter.getIndex() + " ---");
                e.printStackTrace();
            }

            if (iter.hasNext())
            {
                builder.append(delimeter);
            }
        }

        return builder.toString();
    }

	public static <T> String toString(
		@NotNull T[] array,
		@NotNull String delimiter
	) {
		return toString(array, delimiter, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] genericArray(
		@NotNull Class<T> type,
		@Range(minimum = 0, maximum = 255) int size
	) {
		RuntimeConditions.requireWithinRange(size, 0, 255);

		return (T[]) java.lang.reflect.Array.newInstance(type, size);
	}

	@NotNull
	public static <T> ArrayList<T> toArrayList(
		T[] data
	) {
		Objects.requireNonNull(data);

		ArrayList<T> list = new ArrayList<>();
		for (T t : data)
		{
			list.add(t);
		}

		return list;
	}

	/**
	 * Converts a list to an array.
	 *
	 * @param clazz Array class type
	 * @param collection List to convert
	 *
	 * @return List converted to array
	 */
	@NotNull
	public static <T> T[] toArray(
		@NotNull Class<? extends T[]> clazz,
		@NotNull List<T> collection
	) {
		Object[] data = new Object[collection.size()];

		for (int i = 0; i < data.length; i++)
		{
			data[i] = collection.get(i);
		}

		return java.util.Arrays.copyOf(data, data.length, clazz);
	}
   
	/**
	 * Creates a new array iterator for the given array, starting the index at the given starting position.
	 *
	 * @param <T> Type of array
	 * @param data Array to iterate over
	 * @param startingIndex Starting index for iterator
	 *
	 * @return A new array iterator with the given data.
	 *
	 * @since 1.0.0
	 */
	@NotNull
    public static <T> ArrayIterator<T> iterator(
		@NotNull T[] data, 
		int startingIndex
	) {
        return new ArrayIterator<T>(data, startingIndex);
    }

	/**
	 * Creates a new array iterator for the given array, starting the index at the start of the array.
	 *
	 * @param <T> Type of array
	 * @param data Array to iterate over
	 *
	 * @return A new array iterator with the given data.
	 *
	 * @since 1.0.0
	 */
	@NotNull
	public static <T> ArrayIterator<T> iterator(
		@NotNull T[] data
	) {
		return iterator(data, 0);
	}

	/**
	 * Maps an array to a different type.
	 *
	 * @param <T> Initial type
	 * @param <R> Mapped type
	 *
	 * @param data Array to map
	 * @param clazz Type of data to map to
	 * @param mapper Mapping function that takes in T and returns R
	 *
	 * @return An array of R
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public static <T, R> R[] map(
		@NotNull T[] data,
		@NotNull Class<? extends R> clazz,
		@NotNull Function<T, R> mapper
	) {
		R[] array = (R[]) Array.newInstance(clazz, data.length);
		
		for (int i = 0; i < data.length; i++)
		{
			array[i]  = mapper.apply(data[i]);
		}

		return array;
	}
}
