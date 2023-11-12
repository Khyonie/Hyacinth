package coffee.khyonieheart.tidal.legacy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bukkit.command.CommandSender;

import coffee.khyonieheart.tidal.ArgumentType;
import coffee.khyonieheart.tidal.UserType;

@Deprecated
public class CommandBranchLegacy
{
	private String label;
	private Map<String, CommandBranchLegacy> branches = new HashMap<>();
	private ArgumentType<?> type;
	private UserType userType;
	private String permission;
	private boolean requiresNext = true;

	private CommandBranchLegacy nextBranch;

	public CommandBranchLegacy(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return this.label;
	}

	public CommandBranchLegacy get(String name)
	{
		return this.branches.get(name);
	}

	public Set<String> getBranches()
	{
		return branches.keySet();
	}

	public Collection<CommandBranchLegacy> getBranchInstances()
	{
		return this.branches.values();
	}

	public CommandBranchLegacy getNext()
	{
		if (this.branches.size() != 1)
		{
			throw new IllegalStateException("This branch does not have any connected branches");
		}

		return nextBranch;
	}

	//
	// Single constructs
	//

	public CommandBranchLegacy add(String name)
	{
		Objects.requireNonNull(name);

		if (name.length() == 0)
		{
			throw new IllegalArgumentException("Command branches cannot have \"\" as a name");
		}

		CommandBranchLegacy branch = new CommandBranchLegacy(name);
		branches.put(name, branch);

		if (nextBranch == null)
		{
			nextBranch = branch;
		}

		return branch;
	}

	public CommandBranchLegacy add(ArgumentType<?> type, String name)
	{
		return this.add(name).addType(type);
	}

	public CommandBranchLegacy add(String permission, String name)
	{
		return this.add(name).addPermission(permission);
	}

	public CommandBranchLegacy add(UserType userType, String name)
	{
		return this.add(name).addUserType(userType);
	}

	public CommandBranchLegacy add(ArgumentType<?> type, String permission, String name)
	{
		return this.add(name).addType(type).addPermission(permission);
	}

	public CommandBranchLegacy add(ArgumentType<?> type, UserType userType, String name)
	{
		return this.add(name).addType(type).addUserType(userType);
	}

	public CommandBranchLegacy add(UserType userType, String permission, String name)
	{
		return this.add(name).addUserType(userType).addPermission(permission);
	}

	public CommandBranchLegacy add(ArgumentType<?> type, UserType userType, String permission, String name)
	{
		return this.add(name).addType(type).addUserType(userType).addPermission(permission);
	}

	//
	// Group constructs
	//

	public GroupedCommandBranchLegacy add(String... branches)
	{
		CommandBranchLegacy[] branchData = new CommandBranchLegacy[branches.length];

		for (int i = 0; i < branches.length; i++)
		{
			branchData[i] = add(branches[i]);
		}

		return new GroupedCommandBranchLegacy(branchData);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, String... branches)
	{
		return this.add(branches).addType(type);
	}

	public GroupedCommandBranchLegacy add(UserType userType, String... branches)
	{
		return this.add(branches).addUserType(userType);
	}

	public GroupedCommandBranchLegacy add(String permission, String... branches)
	{
 		return this.add(branches).addPermission(permission);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, UserType userType, String... branches)
	{
		return this.add(branches).addType(type).addUserType(userType);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, String permission, String... branches)
	{
		return this.add(branches).addType(type).addPermission(permission);
	}

	public GroupedCommandBranchLegacy add(UserType userType, String permission, String... branches)
	{
		return this.add(branches).addUserType(userType).addPermission(permission);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, UserType userType, String premission, String... branches)
	{
		return this.add(branches).addType(type).addUserType(userType).addPermission(permission);
	}

	//
	// Instance methods
	//

	public CommandBranchLegacy addType(ArgumentType<?> type)
	{
		this.type = type;
		return this;
	}

	public CommandBranchLegacy addUserType(UserType type)
	{
		this.userType = type;
		return this;
	}

	public CommandBranchLegacy addPermission(String permission)
	{
		this.permission = permission;
		return this;
	}

	public ArgumentType<?> getType()
	{
		return this.type;
	}

	public CommandBranchLegacy setOptional()
	{
		this.requiresNext = false;
		return this;
	}

	public UserType getUserType()
	{
		return this.userType;
	}

	public String getPermission()
	{
		return this.permission;
	}

	public boolean isTyped()
	{
		return this.type != null;
	}

	public boolean isAuthorized(CommandSender sender)
	{
		if (this.userType != null)
		{
			if (!this.userType.getClassType().isAssignableFrom(sender.getClass()))
			{
				return false;
			}
		}

		if (permission == null)
		{
			return true;
		}
		
		return sender.hasPermission(this.permission);
	}

	public boolean isLeaf()
	{
		return this.branches.size() == 0;
	}

	public boolean requiresAuthorization()
	{
		return this.permission != null || (this.userType != null ? this.userType.equals(UserType.CONSOLE) : false);
	}

	public boolean requiresNext()
	{
		if (this.isLeaf())
		{
			return false;
		}

		return this.requiresNext;
	}
}
