package coffee.khyonieheart.tidal.concatenation;

import java.util.ArrayList;
import java.util.List;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class Concatenation
{
	public static String[] concatenate(
		@NotNull String[] data, 
		@NotNull char concatChar
	)
		throws ConcatenationFailureException
	{
		List<String> processedArguments = new ArrayList<>();
		StringBuilder builder = null;

		String concatStr = new String("" + concatChar);

		int index = -1;
		for (String arg : data)
		{
			index++;
			if (arg.startsWith(concatStr))
			{
				if (builder != null)
				{
					throw new ConcatenationFailureException(ConcatenationFailureType.UNEXPECTED_START, index, arg);
				}

				if (arg.endsWith(concatStr))
				{
					processedArguments.add(arg.replace(concatStr, ""));
					continue;
				}

				builder = new StringBuilder(arg.substring(1));
				continue;
			}

			if (arg.endsWith(concatStr))
			{
				if (builder == null)
				{
					throw new ConcatenationFailureException(ConcatenationFailureType.UNEXPECTED_END, index, arg);
				}

				builder.append(" " + arg.substring(0, arg.length() - 1));
				processedArguments.add(builder.toString());
				builder = null;
				continue;
			}

			if (builder == null)
			{
				processedArguments.add(arg);
				continue;
			}

			builder.append(" " + arg);
		}

		if (builder != null)
		{
			throw new ConcatenationFailureException(ConcatenationFailureType.UNTERMINATED_QUOTE, data.length - 1, builder.toString());
		}

		//processedArguments.removeIf((s) -> s.length() == 0);

		return processedArguments.toArray(new String[processedArguments.size()]); 
	}
}
