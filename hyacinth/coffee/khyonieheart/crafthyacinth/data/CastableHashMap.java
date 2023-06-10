package coffee.khyonieheart.crafthyacinth.data;

import java.util.HashMap;
import java.util.Map;

import coffee.khyonieheart.hyacinth.util.CastableMap;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

/**
 * Extension of java.util.HashMap which allows casting of mapped values.
 *
 * @param <K> Key type
 * @param <V> Value type
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class CastableHashMap<K, V> extends HashMap<K, V> implements CastableMap<K, V>
{
	private static final long serialVersionUID = -6197512855613945438L;

	/**
	 * Constructs an empty CastableHashMap.
	 *
	 * @since 1.0.0
	 */
	public CastableHashMap() { }

	/**
	 * Constructs a new CastableHashMap with the same mappings as the given map.
	 *
	 * @param initial Initial mappings
	 *
	 * @since 1.0.0
	 */
	public CastableHashMap(
		@NotNull Map<K, V> initial
	) {
		super(initial);
	}


	@Override
	@Nullable
	public <T> T get(
		@Nullable K key, 
		@NotNull Class<? extends T> type
	) {
		return type.cast(get(key));
	}
}
