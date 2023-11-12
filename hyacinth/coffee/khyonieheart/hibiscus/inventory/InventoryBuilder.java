package coffee.khyonieheart.hibiscus.inventory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import coffee.khyonieheart.hyacinth.api.RuntimeConditions;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.hyacinth.util.marker.Range;

public class InventoryBuilder
{
	private Inventory inventory;

	private InventoryBuilder(
		@Range(minimum = 1, maximum = 6) int rows, 
		@NotNull String name
	) {
		Objects.requireNonNull(name);
		RuntimeConditions.requireWithinRange(rows, 1, 6);

		inventory = Bukkit.createInventory(null, rows * 9, name);
	}

	@NotNull
	public static InventoryBuilder builder(
		@Range(minimum = 1, maximum = 6) int rows, 
		@NotNull String name
	) {
		Objects.requireNonNull(name);
		RuntimeConditions.requireWithinRange(rows, 1, 6);

		return new InventoryBuilder(rows, name);
	}

	@NotNull 
	public InventoryBuilder setItem(
		int slot, 
		@NotNull Material material, 
		@NotNull String name, 
		@Range(minimum = 0, maximum = Integer.MAX_VALUE) int count, 
		String... lore
	) {
		Objects.requireNonNull(material);
		Objects.requireNonNull(name);
		RuntimeConditions.requirePositive(count);

		ItemStack item = new ItemStack(material, count);

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name);
		meta.setLore(List.of(lore));

		item.setItemMeta(meta);

		return setItem(slot, item);
	}

	@NotNull 
	public InventoryBuilder setItem(
		int slot, 
		@Nullable ItemStack item
	) {
		inventory.setItem(slot, item);

		return this;
	}

	@NotNull 
	public InventoryBuilder setItems(
		@NotNull Map<Integer, ItemStack> data
	) {
		Objects.requireNonNull(data);

		data.forEach((slot, item) -> {
			if (slot < 0 || slot >= inventory.getSize())
			{
				return;
			}

			inventory.setItem(slot, item);
		});

		return this;
	}

	@NotNull 
	public InventoryBuilder paint(
		int x1, 
		int y1, 
		int x2, 
		int y2, 
		@Nullable ItemStack item
	) {
		RuntimeConditions.requireWithinRange(x1, 0, 8);
		RuntimeConditions.requireWithinRange(x1, 0, 8);

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

	@NotNull
	public Inventory create()
	{
		return inventory;
	}
}
