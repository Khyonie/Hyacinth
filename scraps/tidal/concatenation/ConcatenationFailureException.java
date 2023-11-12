package coffee.khyonieheart.tidal.concatenation;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class ConcatenationFailureException extends Exception
{
	private final ConcatenationFailureType type;
	private final int position;
	private final String argument;

	public ConcatenationFailureException(
		@NotNull ConcatenationFailureType type, 
		int position, 
		@NotNull String argument)
	{
		this.type = type;
		this.position = position;
		this.argument = argument;
	}

	@NotNull
	public ConcatenationFailureType getType()
	{
		return this.type;
	}

	public int getIndex()
	{
		return this.position;
	}

	@NotNull
	public String getArgument()
	{
		return this.argument;
	}
}
