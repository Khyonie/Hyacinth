package coffee.khyonieheart.crafthyacinth.data;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.bukkit.entity.Player;

import coffee.khyonieheart.anenome.NotNull;

public class HyacinthPlayerData
{
	@NotNull
	public static CastableHashMap<String, Object> initDefault(
		@NotNull Player player
	) {
		Objects.requireNonNull(player);

		return new CastableHashMap<>(Map.of(
			"lastKnownIp", player.getAddress().getAddress().getHostAddress(),
			"addedPermissions", new ArrayList<String>(), // Gson needs a little help as to what to coerce these types into
			"permissionGroups", new ArrayList<String>(),
			"lastKnownUsername", player.getName()
		));
	}
}
