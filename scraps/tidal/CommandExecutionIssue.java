package coffee.khyonieheart.tidal;

public class CommandExecutionIssue
{
	private final String issue;
	private final int index;
	private String possibleFix;

	public CommandExecutionIssue(String issue, int index)
	{
		this.issue = issue;
		this.index = index;
	}

	public String getMessage()
	{
		return this.issue;
	}

	public int getIndex()
	{
		return this.index;
	}

	public void addPossibleFix(String possibleFix)
	{
		this.possibleFix = possibleFix;
	}

	public boolean hasPossibleFix()
	{
		return this.possibleFix != null;
	}

	public String getPossibleFix()
	{
		return this.possibleFix;
	}
}
