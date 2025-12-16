package coffee.khyonieheart.crafthyacinth.command.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coffee.khyonieheart.anenome.Arrays;
import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.Validator;
import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;

/**
 * @deprecated Will be supersceded by Tidal 2.0.
 */
@Deprecated
public class HyacinthCompletionBranch implements CompletionBranch
{
	private Map<String, CompletionBranch> attachedBranches = new HashMap<>();
	private List<Validator> validators = new ArrayList<>();

	@Override
	public CompletionBranch add(
		@NotNull String branch
	) {
		CompletionBranch b = new HyacinthCompletionBranch();
		attachedBranches.put(branch, b);

		return b;
	}

	@Override
	public CompletionBranch add(
		@NotNull String branch, 
		@NotEmpty Validator... validators
	) {
		CompletionBranch branchInstance = this.add(branch);
		for (Validator v : validators)
		{
			branchInstance.addValidator(v);
		}
		return branchInstance;
	}

	@Override
	public CompletionBranch get(
		@NotNull String branch
	) {
		// Handle user arguments
		if (attachedBranches.size() == 1)
		{
			// This sucks but I'm not seeing a better way
			String b = attachedBranches.keySet().iterator().next();
			if (b.startsWith("<") && b.endsWith(">"))
			{
				return attachedBranches.get(b);
			}
		}
		return attachedBranches.get(branch);
	}

	@Override
	public String[] allBranches()
	{
		return Arrays.toArray(String.class, new ArrayList<>(attachedBranches.keySet()));
	}

	@Override
	public boolean isLeaf()
	{
		return this.attachedBranches.size() == 0;
	}

	public static HyacinthCompletionBranch newTree()
	{
		return new HyacinthCompletionBranch();
	}

	@Override
	public boolean hasValidator() 
	{
		return this.validators.size() > 0;
	}

	@Override
	public List<Validator> getValidators() 
	{
		return this.validators;
	}

	@Override
	public void addValidator(
		@NotNull Validator validator) 
	{
		this.validators.add(validator);
	}
}
