package coffee.khyonieheart.crafthibiscus;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import coffee.khyonieheart.hibiscus.ClickOffAction;
import coffee.khyonieheart.hibiscus.Element;
import coffee.khyonieheart.hibiscus.Gui;
import coffee.khyonieheart.hibiscus.GuiConfiguration;
import coffee.khyonieheart.hibiscus.Hibiscus;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.hyacinth.util.marker.Range;

/**
 * Base implementation of a Hibiscus GUI.
 */
public class HibiscusGui implements Gui
{
	private Inventory base;
	private String label;
	private int rows;

	private Map<String, GuiConfiguration> configurations = new HashMap<>();
	private String defaultConfiguration;

	private Deque<GuiConfiguration> layers = new ArrayDeque<>();

	// Simple default click filter
	private static Set<InventoryAction> defaultClickTypes = Set.of(
		InventoryAction.PICKUP_ALL, // Left click
		InventoryAction.PICKUP_HALF, // Right click
		InventoryAction.MOVE_TO_OTHER_INVENTORY // Shift-left click
	);

	private Predicate<InventoryClickEvent> clickFilter = (event) -> { 
		if (event.getRawSlot() >= base.getSize())
		{
			return false;
		}

		if (event.getRawSlot() == -999)
		{
			return false;
		}

		if (!defaultClickTypes.contains(event.getAction()))
		{
			return false;
		}

		event.setCancelled(true);
		return true;
	};

	private ClickOffAction clickOffAction = ClickOffAction.DRILLDOWN;

	public HibiscusGui(
		@Nullable String inventoryLabel, 
		@Range(minimum = 1, maximum = 6) int rows, 
		@NotNull Inventory inventory
	) {
		if (rows < 1 || rows > 6)
		{
			throw new IllegalArgumentException("GUI must have 1-6 rows, received " + rows);
		}

		Objects.requireNonNull(inventory);

		this.base = inventory;
		this.rows = rows;
		this.label = inventoryLabel;
	}

	@NotNull
	public HibiscusGui setFilter(
		@NotNull Predicate<InventoryClickEvent> clickFilter
	) {
		Objects.requireNonNull(clickFilter);

		this.clickFilter = clickFilter;

		return this;
	}

	@NotNull
	public HibiscusGui setClickOffAction(
		@NotNull ClickOffAction action
	) {
		Objects.requireNonNull(action, "Click-off action cannot be null");

		this.clickOffAction = action;

		return this;
	}

	@Override
	public void addConfiguration(
		String name, 
		GuiConfiguration configuration
	) {
		Objects.requireNonNull(configuration);

		configurations.put(name, configuration);

		if (defaultConfiguration == null)
		{
			defaultConfiguration = name;
		}
	}

	@Override
	public void setConfiguration(
		Player player, 
		String configurationName
	) {
		Objects.requireNonNull(configurations.get(configurationName), "Unknown configuration \"" + configurationName + "\"");

		InventoryView view = player.getOpenInventory();

		// Clear GUI
		layers.clear();
		for (int i = 0; i < base.getSize(); i++)
		{
			view.setItem(i, base.getItem(i));
		}

		// Apply the new configuration
		addLayer(player, 0, configurationName);
	}

	@Override
	public void addLayer(
		Player player, 
		int row, 
		String configurationName
	) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(configurations.get(configurationName), "Unknown configuration \"" + configurationName + "\"");
		
		InventoryView view = player.getOpenInventory();
		GuiConfiguration configuration = configurations.get(configurationName);

		// Apply configuration
		configuration.getData().forEach((slot, element) -> view.setItem(slot, element != null ? element.toIcon() : null));
		layers.push(configuration);
	}

	@Override
	public void removeLayer(
		Player player
	) {
		GuiConfiguration removedConfiguration = layers.pop();
		GuiConfiguration topConfiguration = layers.getFirst();

		InventoryView view = player.getOpenInventory();

		removedConfiguration.getData().forEach((slot, element) -> {
			if (topConfiguration.getData().containsKey(slot))
			{
				Element e = topConfiguration.getData().get(slot);
				view.setItem(slot, e != null ? e.toIcon() : null);
			}
		});
	}

	/**
	 * @implNote This implementation is faster than calling this{@link #removeLayer(Player)} over and over with many large layers, but is slower than this{@link #setConfiguration(Player, String)} when many layers with small changes are present
	 */
	@Override
	public void removeAllLayers(
		Player player
	) {
		Set<Integer> modifiedIntegers = new HashSet<>();
		GuiConfiguration bottomConfiguration = layers.getLast();

		InventoryView view = player.getOpenInventory();

		while (layers.size() > 1)
		{
			GuiConfiguration configuration = layers.pop();

			for (int slot : configuration.getData().keySet())
			{
				if (modifiedIntegers.contains(slot))
				{
					continue;
				}

				view.setItem(slot, bottomConfiguration.getData().containsKey(slot) ? bottomConfiguration.getData().get(slot).toIcon() : base.getItem(slot));
				modifiedIntegers.add(slot);
			}
		}
	}

	@Override
	public GuiConfiguration getDefaultConfiguration()
	{
		return configurations.get(defaultConfiguration);
	}

	@Override
	public void setDefaultConfiguration(
		String configurationName
	) {
		Objects.requireNonNull(configurations.get(configurationName), "Unknown configuration \"" + configurationName + "\"");
		
		this.defaultConfiguration = configurationName;
	}

	@Override
	public void open(
		Player player
	) {
		Objects.requireNonNull(player);

		InventoryView view = player.openInventory(base);
		Hibiscus.registerOpen(player, this);

		if (defaultConfiguration == null)
		{
			return;
		}

		GuiConfiguration configuration = configurations.get(defaultConfiguration);
		configuration.getData().forEach((slot, element) -> {
			view.setItem(slot, element != null ? element.toIcon() : null);
		});
		layers.push(configuration);
	}

	@Override
	public int setRows(
		Player player, 
		int rows
	) {
		Objects.requireNonNull(player);

		if (this.rows == rows)
		{
			return rows;
		}

		if (rows > 6 || rows < 1)
		{
			throw new IllegalArgumentException("GUI must have 1-6 rows, received " + rows);
		}

		int oldRows = this.rows;
		this.rows = rows;

		player.closeInventory();

		Inventory newBase = Bukkit.createInventory(null, rows * 9, this.label);
		for (int i = 0; i < newBase.getSize(); i++)
		{
			if (i <= base.getSize())
			{
				break;
			}

			newBase.setItem(i, base.getItem(i));
		}

		base = newBase;
		InventoryView view = player.openInventory(base);

		rebuildElements(view);
		
		return oldRows;
	}

	@Override
	public String setName(
		Player player, 
		String name
	) {
		Objects.requireNonNull(player);

		String oldName = this.label;
		this.label = name;

		player.closeInventory();

		Inventory newBase = Bukkit.createInventory(null, rows * 9, this.label);
		newBase.setContents(base.getContents());

		base = newBase;
		InventoryView view = player.openInventory(base);	

		rebuildElements(view);

		return oldName;
	}

	private void rebuildElements(
		@NotNull InventoryView view
	) {
		Iterator<GuiConfiguration> iter = layers.descendingIterator();
		while (iter.hasNext())
		{
			GuiConfiguration configuration = iter.next();

			configuration.getData().forEach((slot, element) -> {
				if (slot >= base.getSize())
				{
					return;
				}

				view.setItem(slot, element != null ? element.toIcon() : null);
			});
		}
	}

	@Override
	public void onClick(
		Player player, 
		InventoryClickEvent event
	) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(event);

		if (!this.clickFilter.test(event))	
		{
			return;
		}

		if (layers.isEmpty())
		{
			return;
		}

		GuiConfiguration head = layers.getFirst();
		if (head.getData().containsKey(event.getRawSlot()))
		{
			if (head.getData().get(event.getRawSlot()) == null)
			{
				return;
			}

			head.getData().get(event.getRawSlot()).onInteract(event, player, event.getRawSlot(), event.getAction(), event.getView(), event.getCursor());
			return;
		}

		switch (this.clickOffAction)
		{
			case DRILLDOWN -> {
				Iterator<GuiConfiguration> iter = layers.iterator();

				while (iter.hasNext())
				{
					GuiConfiguration configuration = iter.next();

					if (!configuration.getData().containsKey(event.getRawSlot()))
					{
						continue;
					}

					if (configuration.getData().get(event.getRawSlot()) == null)
					{
						return;
					}

					configuration.getData().get(event.getRawSlot()).onInteract(event, player, event.getRawSlot(), event.getAction(), event.getView(), event.getCursor());
					break;
				}
			}
			case NONE -> {
				return;
			}
			case REMOVE_DOWN_TO_CLICKED -> {
				while (layers.isEmpty())
				{
					GuiConfiguration configuration = layers.getFirst();

					if (configuration.getData().containsKey(event.getRawSlot()))
					{
						break;
					}

					this.removeLayer(player);
				}
			}
			case REMOVE_ONE_LAYER -> {
				this.removeLayer(player);
			}
		}
	}

	@Override
	public void regenerate(
		Player player
	) {
		rebuildElements(player.getOpenInventory());
	}

	@Override
	public void regenerate(
		Player player, 
		int slot
	) {
		Iterator<GuiConfiguration> iter = layers.iterator();
		while (iter.hasNext())
		{
			GuiConfiguration config = iter.next();

			if (!config.getData().containsKey(slot))
			{
				continue;
			}

			if (config.getData().get(slot) == null)
			{
				continue;
			}

			player.getOpenInventory().setItem(slot, config.getData().get(slot).toIcon());
		}
	}
}
