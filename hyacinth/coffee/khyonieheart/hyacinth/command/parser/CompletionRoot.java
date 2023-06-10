package coffee.khyonieheart.hyacinth.command.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Represents the root of an argument tree.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class CompletionRoot
{
	private Map<String, CompletionBranch> attachedBranches = new HashMap<>();

	/**
	 * Adds a tree to this structure.
	 *
	 * @param root Root of tree to add
	 * @param branch Tree to be added
	 * @param validators Validators for this branch
	 *
	 * @return This object
	 *
	 * @since 1.0.0
	 */
	public CompletionRoot addRoot(
		@NotNull String root, 
		@NotNull CompletionBranch branch,
		Validator... validators
	) {
		this.attachedBranches.put(root, branch);

		return this;
	}

	/**
	 * Adds one or more tree roots to this structure.
	 *
	 * @param branchCreator A function with the String name of this tree being created, which returns a new branch instance
	 * @param roots Not-empty list of strings labelling each branch to be created
	 *
	 * @since 1.0.0
	 */
	public void addRoots(
		@NotNull Function<String, ? extends CompletionBranch> branchCreator,
		@NotEmpty String... roots
	) {
		for (String s : roots)
		{
			this.addRoot(s, branchCreator.apply(s));
		}
	}

	/**
	 * Obtains a branch/subcommand root representing a subcommand label.
	 *
	 * @param root Subcommand label root
	 *
	 * @return Completion branch for the given subcommand label.
	 */
	public CompletionBranch getTree(String root)
	{
		return this.attachedBranches.get(root);
	}
}
