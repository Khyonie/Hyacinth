package coffee.khyonieheart.origami.util;

import java.util.function.Function;

import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

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

        ArrayIterator<T> iter = ArrayIterator.create(array, 0);
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

    public static <T> ArrayIterator<T> iterator(T[] data, int startingIndex)
    {
        return new ArrayIterator<T>(data, startingIndex);
    }
}
