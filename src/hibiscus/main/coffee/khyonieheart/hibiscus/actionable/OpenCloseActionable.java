package coffee.khyonieheart.hibiscus.actionable;

import org.bukkit.entity.Player;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.hibiscus.Gui;
import coffee.khyonieheart.hibiscus.GuiConfiguration;

public interface OpenCloseActionable
{
	public void onOpen(
		@NotNull Player player, 
		@NotNull Gui gui,
		@Nullable GuiConfiguration defaultConfiguration
	);

	public void onClose(
		@NotNull Player player,
		@NotNull Gui gui
	);
}
