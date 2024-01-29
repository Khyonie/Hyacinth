package coffee.khyonieheart.hyacinth.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

/**
 * Various utilities related to Java lists.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class Lists
{
	@NotNull
	public static <E> String toString(
		@NotNull List<E> list, 
		@NotNull String delimiter,
		@Nullable Function<E, String> mapper
	) {
		StringBuilder builder = new StringBuilder();

		Iterator<E> iter = list.iterator();
		while (iter.hasNext())
		{
            try {
                builder.append(mapper != null ? mapper.apply(iter.next()) : iter.next());
            } catch (Exception e) {
                builder.append("--- EXCEPTION THROWN @ INDEX ---");
                e.printStackTrace();
            }

			if (iter.hasNext())
			{
				builder.append(delimiter);
			}
		}

		return builder.toString();
	}

	@NotNull
	public static <E> String toString(
		@NotNull List<E> list,
		@Nullable Function<E, String> mapper
	) {
		return toString(list, ", ", mapper);
	}

	@NotNull
	public static <T, R> List<R> map(
		@NotNull Collection<T> input, 
		@NotNull Function<T, R> mapper
	) {
		List<R> data = new ArrayList<>();

		input.forEach((e) -> data.add(mapper.apply(e)));

		return data;
	}
}
