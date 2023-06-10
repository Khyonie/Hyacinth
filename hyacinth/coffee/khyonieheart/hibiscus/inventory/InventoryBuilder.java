package coffee.khyonieheart.hibiscus.inventory;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryBuilder
{
	private Inventory inventory;

	private InventoryBuilder(int rows, String name)
	{
		inventory = Bukkit.createInventory(null, rows * 9, name);
	}

	public static InventoryBuilder builder(int rows, String name)
	{
		if (rows < 1 || rows > 6)
		{
			throw new IllegalArgumentException("Expected 1-6 rows, received " + rows);
		}

		return new InventoryBuilder(rows, name);
	}

	public InventoryBuilder setItem(int slot, Material material, String name, int count, String... lore)
	{
		ItemStack item = new ItemStack(material, count);

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name);
		meta.setLore(List.of(lore));

		item.setItemMeta(meta);

		return setItem(slot, item);
	}

	public InventoryBuilder setItem(int slot, ItemStack item)
	{
		inventory.setItem(slot, item);

		return this;
	}

	public InventoryBuilder setItems(Map<Integer, ItemStack> data)
	{
		data.forEach((slot, item) -> {
			if (slot < 0 || slot >= inventory.getSize())
			{
				return;
			}

			inventory.setItem(slot, item);
		});

		return this;
	}

	public InventoryBuilder paint(int x1, int y1, int x2, int y2, ItemStack item)
	{
		if (x1 < 0 || x1 > 8 || x2 < 0 || x2 > 8)
		{
			throw new IllegalArgumentException("X must be a positive integer from 0 to 8");
		}

		if (y1 < 0 || y1 >= (inventory.getSize() / 9) || y2 < 0 || y2 >= (inventory.getSize() / 9))
		{
			throw new IllegalArgumentException("Y must be a positive integer from 0 to " + (inventory.getSize() / 9) + " (size: " + inventory.getSize() + ")");
		}

		for (int iy = y1; iy < y2 + 1; iy++)
		{
			for (int ix = x1; ix < x2 + 1; ix++)
			{
				inventory.setItem((iy * 9) + ix, item);
			}
		}

		return this;
	}

	public Inventory create()
	{
		return inventory;
	}
}
