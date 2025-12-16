package coffee.khyonieheart.hyacinth.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Strings
{
	public static List<String> copyFuzzyMatches(String input, List<String> data)
	{
		List<String> matches = new ArrayList<>();

		int longest = Integer.MAX_VALUE;

		for (String s : data)
		{
			if (s.length() > longest)
			{
				longest = s.length();
			}
		}

		for (String s : data)
		{
			// Check for substrings
			if (s.contains(input.toLowerCase()))
			{
				matches.add(s);
				continue;
			}

			// Check that at least 80% of the characters given are present in the input
			if (containsMinCharacters(s, longest, 0.80, input.toCharArray()))
			{
				matches.add(s);
				continue;
			}
		}

		return matches;
	}

	private static boolean containsMinCharacters(String string, int longestOption, double matchThreshold, char[] chars)
	{
		Set<Character> matchedCharacters = new HashSet<>();
		char[] stringChars = string.toCharArray();

		for (char stringChar : stringChars)
		{
			for (char c : chars)
			{
				c = Character.toLowerCase(c);
				if (stringChar == c)
				{
					matchedCharacters.add(c);
					break;
				}
			}
		}

		// Number of possible matched characters
		return ((matchedCharacters.size() / (double) chars.length) * (1.0 - (string.length() / (double) longestOption))) >= matchThreshold;
	}
}
