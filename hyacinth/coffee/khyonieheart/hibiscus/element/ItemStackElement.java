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

public class ItemStackElement implements Element
{
	private ItemStack item;

	public ItemStackElement(ItemStack item)
	{
		this.item = item;
	}

	public ItemStackElement(Material material, String name, int amount, String... lore)
	{
		ItemStack item = new ItemStack(material, amount);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));

		item.setItemMeta(meta);

		this.item = item;
	}

	@Override
	public ItemStack toIcon() 
	{
		return item;
	}

	@Override
	public void onInteract(InventoryClickEvent event, Player player, int clickedSlot, InventoryAction action, InventoryView view, ItemStack itemOnCursor) 
	{
		return;
	}
}
