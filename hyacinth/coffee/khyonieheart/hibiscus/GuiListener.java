package coffee.khyonieheart.hibiscus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener
{
	@EventHandler
	public void onClick(InventoryClickEvent event)
	{
		if (!Hibiscus.isInGui((Player) event.getWhoClicked()))	
		{
			return;
		}

		Hibiscus.getOpenGui((Player) event.getWhoClicked()).onClick((Player) event.getWhoClicked(), event);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event)
	{
		Hibiscus.registerClose((Player) event.getPlayer());
	}
}
