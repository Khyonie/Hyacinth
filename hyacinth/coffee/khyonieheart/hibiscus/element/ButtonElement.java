package coffee.khyonieheart.hibiscus.element;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import coffee.khyonieheart.hibiscus.Element;
import coffee.khyonieheart.hibiscus.ElementConsumer;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.hyacinth.util.marker.Range;

public class ButtonElement implements Element
{
	private ItemStack item;

	private ElementConsumer action = null;

	public ButtonElement(
		@NotNull ItemStack item
	) {
		Objects.requireNonNull(item);

		this.item = item;
	}

	public ButtonElement(
		@NotNull Material material, 
		@NotNull String name, 
		@Range(minimum = 0, maximum = Integer.MAX_VALUE) 
			int amount, 
		String... lore
	) {
		Objects.requireNonNull(material);
		Objects.requireNonNull(name);

		ItemStack item = new ItemStack(material, amount);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);

		this.item = item;
	}	

	@NotNull
	public ButtonElement setAction(
		@Nullable ElementConsumer action
	) {
		this.action = action;

		return this;
	}

	@Override
	public ItemStack toIcon() 
	{
		return item;
	}

	@Override
	public void onInteract(
		InventoryClickEvent event, 
		Player player, 
		int clickedSlot, 
		InventoryAction action, 
		InventoryView view, 
		ItemStack itemOnCursor
	) {
		if (this.action != null)
		{
			this.action.onInteract(event, player, clickedSlot, action, view, itemOnCursor);
		}
	}
}
