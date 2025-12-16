package coffee.khyonieheart.hyacinth.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import coffee.khyonieheart.anenome.ArrayIterator;
import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.crafthyacinth.data.CastableHashMap;

/**
 * Specialization of a Map that allows casting stored values.
 * Several default methods are provided to easily cast to common types.
 *
 * @param <K> Key type
 * @param <V> Value type
 *
 * @author Khyonie
 * @since 1.0.0
 */
public interface CastableMap<K, V> extends Map<K, V>
{
	/**
	 * Gets a value from a key inside this map, casting it to type T.
	 *
	 * @param <T> Type to cast to
	 *
	 * @param key Key
	 * @param type Class of T
	 *
	 * @return A value mapped to this map by the given key, casted to the given type.
	 *
	 * @since 1.0.0
	 */
	@Nullable
	public <T> T get(
		@NotNull K key, 
		@NotNull Class<? extends T> type
	);

	/**
	 * Gets a value from a key inside this map, using the given mapping function to convert it from its stored type.
	 *
	 * @param <R> Mapped type
	 *
	 * @param key Key
	 * @param mapper Function that converts an instance of V to R
	 *
	 * @return A value mapped to this map by the given key, converted to the given type.
	 *
	 * @since 1.0.0
	 */
	@Nullable
	public default <R> R get(
		@NotNull K key,
		@NotNull Function<V, R> mapper
	) {
		Objects.requireNonNull(mapper);
		Objects.requireNonNull(key);

		return mapper.apply(get(key));
	}

	/**
	 * Gets an int value inside this map.
	 *
	 * @param key Key
	 *
	 * @return An integer stored in this map
	 *
	 * @throws ClassCastException If the type represented by the value is not castable to an int
	 *
	 * @since 1.0.0
	 */
	public default int getInt(
		@NotNull K key
	) {
		Objects.requireNonNull(key);

		try {
			return (int) Math.round((double) get(key));
		} catch (ClassCastException e) {
			return (int) get(key);
		}
	}

	/**
	 * Gets a boolean value inside this map.
	 *
	 * @param key Key
	 *
	 * @return A boolean stored in this map
	 *
	 * @throws ClassCastException If the type represented by the value is not castable to a boolean
	 *
	 * @since 1.0.0
	 */
	public default boolean getBoolean(
		@NotNull K key
	) {
		Objects.requireNonNull(key);
		return get(key, Boolean.TYPE);
	}

	/**
	 * Gets a double value inside this map.
	 *
	 * @param key Key
	 *
	 * @return A double stored in this map
	 *
	 * @throws ClassCastException If the type represented by the value is not castable to a double
	 *
	 * @since 1.0.0
	 */
	public default double getDouble(
		@NotNull K key
	) {
		Objects.requireNonNull(key);
		return get(key, Double.TYPE);
	}

	/**
	 * Gets a String object inside this map.
	 *
	 * @param key Key
	 *
	 * @return A String stored in this map. May be null.
	 *
	 * @throws ClassCastException If the type represented by the value is not castable to a String
	 *
	 * @since 1.0.0
	 */
	@Nullable
	public default String getString(
		@NotNull K key
	) {
		Objects.requireNonNull(key);
		return get(key, String.class);
	}

	/**
	 * Creates a new modifiable, castable map with the specified mappings. 
	 * If no mappings are provided, an empty map is returned.
	 *
	 * @param <K> Key type. All odd elements in the array MUST be of this type (I.E Element 0 must be of the same type as element 2, 4, and so on)
	 * @param pairs Mappings with which to construct a map
	 *
	 * @return A modifiable, castable map with the specified mappings.
	 *
	 * @throws IllegalArgumentException If the array of pairs contains an odd number of elements
	 * @throws ClassCastException If one or more key object classes does not match the class of the first key object.
	 * 
	 * @since 1.0.0
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public static <K> CastableMap<K, Object> of(
		Object... pairs
	) 
		throws IllegalArgumentException, ClassCastException
	{
		if (pairs.length == 0)
		{
			return new CastableHashMap<>();
		}

		if ((pairs.length & 1) == 1) // Check if there are an odd number of elements
		{
			throw new IllegalArgumentException("Must be an odd number of key/value pairs");
		}

		CastableMap<K, Object> map = new CastableHashMap<>();
		Iterator<Object> iter = new ArrayIterator<>(pairs);

		while (iter.hasNext())
		{
			map.put((K) iter.next(), iter.next());
		}

		return map;
	}
}
