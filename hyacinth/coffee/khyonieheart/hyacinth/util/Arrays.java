package coffee.khyonieheart.hyacinth.util;

import java.util.function.Function;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

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

        ArrayIterator<T> iter = create(array);
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
    public static <T> ArrayIterator<T> create(
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
	public static <T> ArrayIterator<T> create(
		@NotNull T[] data
	) {
		return create(data, 0);
	}
}
