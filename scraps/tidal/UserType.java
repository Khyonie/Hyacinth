package coffee.khyonieheart.tidal;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum UserType
{
	CONSOLE(ConsoleCommandSender.class),
	PLAYER(Player.class),
	OTHER(CommandSender.class)
	;

	private Class<?> classType;

	private UserType(Class<?> classType)
	{
		this.classType = classType;
	}

	public Class<?> getClassType()
	{
		return this.classType;
	}
}
