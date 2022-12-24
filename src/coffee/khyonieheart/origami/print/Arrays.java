package coffee.khyonieheart.origami.print;

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
     * @param delimeter String to seperate individual objects in array
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

        for (int i = 0; i < array.length; i++)
        {
            try {
                builder.append(mapper != null ? mapper.apply(array[i]) : array[i]);
            } catch (Exception e) {
                builder.append("--- EXCEPTION THROWN @ INDEX " + i + " ---");
                e.printStackTrace();
            }

            if (i + 1 != array.length)
            {
                builder.append(delimeter);
            }
        }

        return builder.toString();
    }
}
