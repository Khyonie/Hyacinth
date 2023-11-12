package coffee.khyonieheart.hyacinth.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public class Collections
{
	public static <T> String toString(
		@NotNull Collection<T> collection,
		@NotNull String delimitter,
		@Nullable Function<T, String> mapper
	) {
		Objects.requireNonNull(collection);
		delimitter = Objects.requireNonNullElse(delimitter, ", ");

		StringBuilder builder = new StringBuilder();

		Iterator<T> iter = collection.iterator();
		while (iter.hasNext())
		{
			builder.append(mapper == null ? iter.next() : mapper.apply(iter.next()));
			if (iter.hasNext())
			{
				builder.append(delimitter);
			}
		}

		return builder.toString();
	}

	public static <T> String toString(
		@NotNull Collection<T> collection,
		@NotNull String delimitter
	) {
		return toString(collection, delimitter, null);
	}	

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> mapOf(
		@NotNull Class<? extends K> keyClass,
		@NotNull Class<? extends V> valClass,
		Object... data
	) {
		Objects.requireNonNull(keyClass);
		Objects.requireNonNull(valClass);

		if ((data.length & 1) == 1)
		{
			throw new IllegalArgumentException("Cannot create a map with an odd number of elements.");
		}

		Map<K, V> map = new HashMap<>(data.length >> 1);

		for (int i = 0; i < data.length; i++)
		{
			map.put((K) data[i], (V) data[++i]);
		}

		return java.util.Collections.unmodifiableMap(map);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> mapOf(
		@NotNull Supplier<V> valueSupplier,
		Object... data
	) {
		Objects.requireNonNull(valueSupplier);

		Map<K, V> map = new HashMap<>();
		for (Object k : data)
		{
			map.put((K) k, valueSupplier.get());
		}

		return map;
	}

	public static <K, V> Map<K, V> mapOfArray(
		@NotNull Supplier<V> valueSupplier,
		@NotNull K[] data
	) {
		Objects.requireNonNull(valueSupplier);
		Objects.requireNonNull(data);

		Map<K, V> map = new HashMap<>();
		for (K k : data)
		{
			map.put(k, valueSupplier.get());
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T, C extends Collection<T> & Iterable<T>> C reverse(
		C input
	) {
		C copy = (C) Reflect.simpleInstantiate(input.getClass(), new Class<?>[] { Integer.TYPE }, 10);

		return copy;
	}
}
