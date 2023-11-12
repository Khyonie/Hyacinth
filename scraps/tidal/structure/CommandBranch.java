package coffee.khyonieheart.tidal.structure;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.tidal.ArgumentType;

public class CommandBranch<T>
{
	private String label;
	private Class<T> type;

	private ArgumentType<T> handler;
	private Class<? extends CommandSender> senderType;
	private String permission;

	private Map<String, CommandBranch<?>> branches = new HashMap<>();

	public CommandBranch(
		@NotNull String label,
		@NotNull Class<T> type
	) {
		this.label = label;
		this.type = type;
	}

	public CommandBranch<?> add(CommandBranch<?> branch)
	{
		this.branches.put(branch.getLabel(), branch);

		return branch;
	}

	public CommandBranch<?> get(String arg)
	{
		return this.branches.get(arg);
	}

	public boolean hasArg(String arg)
	{
		return this.branches.containsKey(arg);
	}

	//
	// Required properties
	//

	@NotNull
	public String getLabel()
	{
		return this.label;
	}

	@NotNull
	public Class<T> getType()
	{
		return this.type;
	}

	//
	// Optional properties
	//


	@Nullable
	public Class<? extends CommandSender> getSenderType()
	{
		return this.senderType;
	}

	@Nullable
	public ArgumentType<T> getArgumentType()
	{
		return this.handler;
	}

	@Nullable
	public String getPermission()
	{
		return this.permission;
	}

	//
	// Setters
	//
	
	public void setSenderType(Class<? extends CommandSender> senderType)
	{
		this.senderType = senderType;
	}

	@SuppressWarnings("unchecked")
	public void setHandler(ArgumentType<?> handler)
	{
		this.handler = (ArgumentType<T>) handler;
	}

	public void setPermission(String permission)
	{
		this.permission = permission;
	}
}
