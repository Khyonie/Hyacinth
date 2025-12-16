package coffee.khyonieheart.crafthyacinth.command.parser;

import java.util.List;

import org.bukkit.Bukkit;

import coffee.khyonieheart.hyacinth.command.parser.SuggestionGenerator;
import coffee.khyonieheart.hyacinth.util.Lists;

/**
 * @deprecated Will be supersceded by Tidal 2.0.
 */
@Deprecated
public class PlayerSuggestionEngine implements SuggestionGenerator
{
	@Override
	public List<String> generateSuggestions()
	{
		return Lists.map(Bukkit.getOnlinePlayers(), (player) -> { return player.getName(); });
	}
}
