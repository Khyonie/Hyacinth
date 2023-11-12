package coffee.khyonieheart.tidal;

import java.util.Deque;

import coffee.khyonieheart.tidal.structure.CommandBranch;

public class TraversalResult
{
	private Deque<CommandBranch<?>> branches;
	private TraversalStatus status;

	public TraversalResult(
		Deque<CommandBranch<?>> branches, 
		TraversalStatus status
	) {
		this.branches = branches;
		this.status = status;
	}

	public static enum TraversalStatus
	{
		OK_REACHED_LEAF,
		OK_REQUIRES_MORE, // Fail slow
		ERR_STATIC_BRANCH_MISSING, // Fail fast
		ERR_NO_ROOT,
		ERR_FAILED_VALIDATION, // Fail slow
		ERR_NO_PERMISSION, // Fail fast
		ERR_ILLEGAL_USER // Fail fast
		;
	}

	public static enum TraversalContext
	{
		EXECUTION,
		TABCOMPLETE
		;
	}
}
