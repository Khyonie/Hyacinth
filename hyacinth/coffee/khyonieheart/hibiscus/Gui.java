package coffee.khyonieheart.hibiscus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public interface Gui
{
	/**
	 * Registers a configuration of elements to this GUI. If no configurations have been added to this GUI, the first configuration will be set as the default configuration.
	 *
	 * @param name Name of configuration, ex. "page1", "page2" for paged GUIs, or "main" for static configurations
	 * @param configuration Configuration data
	 */
	public void addConfiguration(String name, GuiConfiguration configuration);
	
	/**
	 * Sets the active configuration for a player, clearing all configurations and rebuilding the GUI with the new configuration.
	 *
	 * @param player Player to set configuration for
	 * @param configurationName Configuration to switch to
	 */
	public void setConfiguration(Player player, String configurationName);

	/**
	 * Adds a layer to a GUI.
	 *
	 * @param player Player to add layer for
	 * @param row Row of GUI to start layer, ex. 0 would embed to the top of the GUI, 1 would be the second row, etc.
	 * @param configurationName Configuration to be layered
	 */
	public void addLayer(Player player, int row, String configurationName);

	/**
	 * Removes the top layer of a GUI.
	 *
	 * @param player Player to remove top layer for
	 */
	public void removeLayer(Player player);

	/**
	 * Removes all extra layers from a GUI. 
	 */
	public void removeAllLayers(Player player);

	/**
	 * Obtains this GUI's default configuration.
	 */
	@Nullable
	public GuiConfiguration getDefaultConfiguration();

	/**
	 * Sets this GUI's default configuration.
	 *
	 * @param configurationName Default configuration name
	 */
	public void setDefaultConfiguration(String configurationName);

	/**
	 * Opens this GUI in its default state to a player. If a default configuration is set, it should be applied.
	 *
	 * @param player Player to open GUI for
	 */
	public void open(Player player);

	public void onClick(Player player, InventoryClickEvent event);

	public void regenerate(Player player);

	public void regenerate(Player player, int slot);

	//
	// Inventory mutations
	//
	
	/**
	 * Sets the number of rows for this GUI. This operation is computationally expensive as the GUI has to be completely regenerated.
	 *
	 * @param player Player to change number of rows for
	 * @param rows New number of rows
	 *
	 * @return The previous row count
	 */
	public int setRows(Player player, int rows);

	/**
	 * Sets the label for this GUI. This operation is computationally expensive as the GUI has to be completely regenerated.
	 *
	 * @param player Player to change GUI name for
	 * @param name New name
	 *
	 * @return The previous GUI name
	 */
	public String setName(Player player, String name);
}
