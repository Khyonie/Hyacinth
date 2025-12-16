package coffee.khyonieheart.hyacinth.data;

import java.io.File;
import java.util.Map;

import org.bukkit.entity.Player;

import coffee.khyonieheart.crafthyacinth.data.CastableHashMap;

public interface PlayerDataManager
{
	public Map<String, CastableHashMap<String, Object>> load(File file);

	public File save(Player player);
}
