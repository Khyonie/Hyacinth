package coffee.khyonieheart.hibiscus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public interface Element
{
	@NotNull
	public ItemStack toIcon();

	public void onInteract(
		@NotNull InventoryClickEvent event,
		@NotNull Player player,
		int clickedSlot,
		@NotNull InventoryAction action,
		@NotNull InventoryView view,
		@Nullable ItemStack itemOnCursor
	);
}
