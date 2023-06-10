package coffee.khyonieheart.hibiscus.element;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import coffee.khyonieheart.hibiscus.Element;
import coffee.khyonieheart.hibiscus.ElementConsumer;

public class ButtonElement implements Element
{
	private ItemStack item;

	private ElementConsumer action = null;

	public ButtonElement(ItemStack item)
	{
		this.item = item;
	}

	public ButtonElement(Material material, String name, int amount, String... lore)
	{
		ItemStack item = new ItemStack(material, amount);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));

		this.item = item;
	}	

	public ButtonElement setAction(ElementConsumer action)
	{
		this.action = action;

		return this;
	}

	@Override
	public ItemStack toIcon() 
	{
		return item;
	}

	@Override
	public void onInteract(InventoryClickEvent event, Player player, int clickedSlot, InventoryAction action, InventoryView view, ItemStack itemOnCursor) 
	{
		if (this.action != null)
		{
			this.action.onInteract(event, player, clickedSlot, action, view, itemOnCursor);
		}
	}
}
