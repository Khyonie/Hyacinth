package coffee.khyonieheart.tidal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Strings
{
	public static List<String> fuzzySearch(String input, int maxDistance, List<String> options)
	{
		Set<String> filteredOptions = new HashSet<>(options);
		
		filteredOptions.removeIf((s) -> levenshteinDistance(input, s) >= maxDistance);

		if (filteredOptions.isEmpty())
		{
			filteredOptions = new HashSet<>(options);
			filteredOptions.removeIf((s) -> !s.contains(input));
		}

		return new ArrayList<>(filteredOptions);
	}

	public static List<String> fuzzySearchSorted(String input, int maxDistance, List<String> options)
	{
		List<String> searchedOptions = fuzzySearch(input, maxDistance, options);

		searchedOptions.sort(new DistanceComparator(input).reversed());

		return searchedOptions;
	}

	public static byte levenshteinDistance(String inputA, String inputB)
	{
		byte[][] data = new byte[inputA.length() + 1][inputB.length() + 1];

		for (byte i = 1; i <= inputA.length(); i++)
		{
			for (byte j = 1; j <= inputB.length(); j++)
			{
				data[i][j] = min3(
					(byte) (data[i - 1][j] + 1),
					(byte) (data[i][j - 1] + 1),
					(byte) (data[i - 1][j - 1] + (inputA.charAt(i - 1) != inputB.charAt(j - 1) ? 1 : 0))
				);
			}
		}

		return data[inputA.length()][inputB.length()];
	}

	private static byte min3(byte a, byte b, byte c)
	{
		if (a < b && a < c)
		{
			return a;
		}

		if (b < a && b < c)
		{
			return b;
		}

		return c;
	}

	private static class DistanceComparator implements Comparator<String>
	{
		private String input;
		public DistanceComparator(String input)
		{
			this.input = input;
		}

		@Override
		public int compare(String a, String b) 
		{
			return Math.min(levenshteinDistance(input, a), levenshteinDistance(input, b));
		}
	}
}
