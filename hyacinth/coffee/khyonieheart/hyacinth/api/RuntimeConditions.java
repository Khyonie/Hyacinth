package coffee.khyonieheart.hyacinth.api;

import coffee.khyonieheart.hyacinth.exception.NumberOutOfRangeException;

public class RuntimeConditions
{
	public static int requireWithinRange(
		int input, 
		int minimum, 
		int maximum
	)
		throws NumberOutOfRangeException
	{
		if (input < minimum || input > maximum)
		{
			throw new NumberOutOfRangeException(input, "Expected number between " + minimum + " and " + maximum + ", received " + input);
		}

		return input;
	}

	public static int requirePositive(
		int input
	)
		throws NumberOutOfRangeException
	{
		if (input < 0)
		{
			throw new NumberOutOfRangeException(input, "Expected positive number, received " + input);
		}

		return input;
	}
}
