package coffee.khyonieheart.hyacinth.command.parser;

import java.util.List;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;

/**
 * An object representing a either a root, branch, or leaf connected to a larger command argument tree.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public interface CompletionBranch
{
	/**
	 * Adds a branch to this tree.
	 *
	 * @param branch Branch label
	 *
	 * @return The newly created branch.
	 */
	@NotNull
	public CompletionBranch add(String branch);

	/**
	 * Adds a branch to this tree and attaches one or more validators to it.
	 *
	 * @param branch Branch label
	 * @param validators Validators to add to this branch.
	 *
	 * @return The newly created branch.
	 */
	@NotNull
	public CompletionBranch add(String branch, @NotEmpty Validator... validators);

	/**
	 * Gets a branch connected to this tree with the given label.
	 *
	 * @param branch Branch label
	 *
	 * @return A branch connected to this tree matching the given label.
	 */
	@Nullable
	public CompletionBranch get(@NotNull String branch);

	/**
	 * Obtains a String array of all branches connected to this tree.
	 *
	 * @return Array of all branches connected to this tree.
	 */
	@NotNull 
	public String[] allBranches();

	/**
	 * Gets whether or not this tree has zero attached branches.
	 *
	 * @return True if this branch has no attached branches, false if one or more such branches exist.
	 */
	public boolean isLeaf();

	/**
	 * Gets whether or not this tree has at least one validator.
	 *
	 * @return True if this branch has at least one validator, false if no validators.
	 */
	public boolean hasValidator();

	/**
	 * Gets a list of all validators added to this tree.
	 *
	 * @return A list of validators attached to this tree. May be empty.
	 */
	@NotNull
	public List<Validator> getValidators();

	/**
	 * Adds a validator to this tree.
	 *
	 * @param validator Validator to add
	 */
	public void addValidator(Validator validator);
}
