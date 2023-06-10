package coffee.khyonieheart.crafthyacinth.data;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.entity.Player;

public class HyacinthPlayerData
{
	public static CastableHashMap<String, Object> initDefault(Player player)
	{
		return new CastableHashMap<>(Map.of(
			"lastKnownIp", player.getAddress().getAddress().getHostAddress(),
			"addedPermissions", new ArrayList<String>(), // Gson needs a little help as to what to coerce these types into
			"permissionGroups", new ArrayList<String>(),
			"lastKnownUsername", player.getName()
		));
	}
}
