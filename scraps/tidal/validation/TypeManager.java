package coffee.khyonieheart.tidal.validation;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import coffee.khyonieheart.tidal.ArgumentType;

public class TypeManager
{
	private static Map<Class<?>, ArgumentType<?>> registeredTypes = new HashMap<>(Map.of(
		Integer.TYPE, ArgumentType.integer(),
		Boolean.TYPE, ArgumentType.bool(),
		Float.TYPE, ArgumentType.float32(),
		Player.class, ArgumentType.onlinePlayer()
	));

	public static void addType(Class<?> type, ArgumentType<?> validator)
	{
		registeredTypes.put(type, validator);
	}

	@SuppressWarnings("unchecked")
	public static <T> ArgumentType<T> getValidator(Class<? extends T> type)
	{
		if (!registeredTypes.containsKey(type))
		{
			return null;
		}

		return (ArgumentType<T>) registeredTypes.get(type);
	}
}
