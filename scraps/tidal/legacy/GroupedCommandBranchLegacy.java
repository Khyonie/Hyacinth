package coffee.khyonieheart.tidal.legacy;

import coffee.khyonieheart.tidal.ArgumentType;
import coffee.khyonieheart.tidal.UserType;

@Deprecated
public class GroupedCommandBranchLegacy
{
	private CommandBranchLegacy[] branches;

	public GroupedCommandBranchLegacy(CommandBranchLegacy[] branches)
	{
		this.branches = branches;
	}

	public CommandBranchLegacy[] getBranches()
	{
		return this.branches;
	}

	public GroupedCommandBranchLegacy add(String name)
	{
		CommandBranchLegacy[] branchData = new CommandBranchLegacy[branches.length];

		for (int i = 0; i < branches.length; i++)
		{
			branchData[i] = branches[i].add(name);
		}

		return new GroupedCommandBranchLegacy(branchData);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, String name)
	{
		CommandBranchLegacy[] branchData = new CommandBranchLegacy[branches.length];

		for (int i = 0; i < branches.length; i++)
		{
			branchData[i] = branches[i].add(type, name);
		}

		return new GroupedCommandBranchLegacy(branchData);
	}

	public GroupedCommandBranchLegacy add(String... branches)
	{
		CommandBranchLegacy[] branchData = new CommandBranchLegacy[this.branches.length * branches.length];

		for (int i = 0; i < branches.length; i++)
		{
			for (int o = 0; o < this.branches.length; o++)
			{
				branchData[(i * this.branches.length) + o] = this.branches[i].add(branches[i]);
			}
		}

		return new GroupedCommandBranchLegacy(branchData);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, String... branches)
	{
		return add(branches).addType(type);
	}

	public GroupedCommandBranchLegacy add(UserType userType, String... branches)
	{
		return add(branches).addUserType(userType);
	}

	public GroupedCommandBranchLegacy add(String permission, String... branches)
	{
		return add(branches).addPermission(permission);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, UserType userType, String... branches)
	{
		return add(branches).addType(type).addUserType(userType);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, String permission, String... branches)
	{
		return add(branches).addType(type).addPermission(permission);
	}

	public GroupedCommandBranchLegacy add(UserType userType, String permission, String... branches)
	{
		return add(branches).addUserType(userType).addPermission(permission);
	}

	public GroupedCommandBranchLegacy add(ArgumentType<?> type, UserType userType, String permission, String... branches)
	{
		return add(branches).addType(type).addUserType(userType).addPermission(permission);
	}

	//
	//
	//
	
	public GroupedCommandBranchLegacy addType(ArgumentType<?> type)
	{
		for (CommandBranchLegacy b : this.branches)
		{
			b.addType(type);
		}

		return this;
	}

	public GroupedCommandBranchLegacy addUserType(UserType userType)
	{
		for (CommandBranchLegacy b : this.branches)
		{
			b.addUserType(userType);
		}

		return this;
	}

	public GroupedCommandBranchLegacy addPermission(String permission)
	{
		for (CommandBranchLegacy b : this.branches)
		{
			b.addPermission(permission);
		}

		return this;
	}
}
