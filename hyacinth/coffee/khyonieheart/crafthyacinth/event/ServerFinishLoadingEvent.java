package coffee.khyonieheart.crafthyacinth.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

public class ServerFinishLoadingEvent extends ServerEvent
{
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() 
	{
		return HANDLERS;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}
}
