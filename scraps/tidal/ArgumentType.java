package coffee.khyonieheart.tidal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import coffee.khyonieheart.hyacinth.util.Arrays;
import coffee.khyonieheart.hyacinth.util.Lists;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;
import coffee.khyonieheart.tidal.validation.ArgumentValidator;

public abstract class ArgumentType<T> implements ArgumentValidator
{
	private final Class<T> clazz;

	public ArgumentType(Class<T> clazz)
	{
		this.clazz = clazz;
	}

	public Class<T> getType()
	{
		return this.clazz;
	}

	protected abstract T toType(String input);

	/**
	 * Generate a list of completions for this type. A null list indicates that no completions are available for this type.
	 */
	@Nullable
	protected abstract List<String> getCompletions();

	/**
	 * Converts an argument to a String. As all strings begin as strings regardless, the string given is returned as-is.
	 */
	public static ArgumentType<String> string()
	{
		return new ArgumentType<>(String.class)
		{
			@Override
			protected String toType(String input)
			{
				return input;
			}

			@Override
			public CommandExecutionIssue validateExecution(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				return null;
			}

			@Override
			public CommandExecutionIssue validateTabcomplete(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				return null;
			}

			@Override
			protected List<String> getCompletions()
			{
				return null;
			}
		};
	}

	public static ArgumentType<Boolean> bool()
	{
		return new ArgumentType<Boolean>(Boolean.TYPE) 
		{
			@Override
			public CommandExecutionIssue validateExecution(
				CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args
			)
			{
				return switch (argument.toLowerCase()) {
					case "true" -> null;
					case "false" -> null;
					default -> new CommandExecutionIssue("Cannot compute \"" + argument + "\" as a boolean",  argumentIndex);
				};
			}

			@Override
			public CommandExecutionIssue validateTabcomplete(
				CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args
			)
			{
				return switch (argument.toLowerCase()) {
					case "true" -> null;
					case "false" -> null;
					default -> new CommandExecutionIssue("Invalid boolean \"" + argument + "\"",  argumentIndex);
				};
			}

			@Override
			protected Boolean toType(
				String input
			)
			{
				return Boolean.parseBoolean(input.toLowerCase());
			}

			@Override
			protected List<String> getCompletions()
			{
				return List.of("true", "false");
			}
		};
	}

	public static ArgumentType<Integer> integer()
	{
		return new ArgumentType<>(Integer.TYPE)
		{
			@Override
			public Integer toType(String input)
			{
				return Integer.parseInt(input);
			}

			@Override
			public CommandExecutionIssue validateExecution(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				try {
					Integer.parseInt(argument);	
					return null;
				} catch (NumberFormatException e) {
					CommandExecutionIssue issue = new CommandExecutionIssue("Cannot compute \"" + argument + "\" as an integer", argumentIndex);
					
					try {
						Float.parseFloat(argument);
						issue.addPossibleFix("Round " + argument + " to " + Math.round(Float.parseFloat(argument)));
					} catch (NumberFormatException ea) { }

					return issue;
				}
			}

			@Override
			public CommandExecutionIssue validateTabcomplete(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				try {
					Integer.parseInt(argument);
					return null;
				} catch (NumberFormatException e) {
					return new CommandExecutionIssue("Invalid integer \"" + argument + "\"", argumentIndex);
				}
			}

			@Override
			protected List<String> getCompletions() 
			{
				return null;
			}
		};
	}

	public static ArgumentType<Float> float32()
	{
		return new ArgumentType<Float>(Float.TYPE)
		{
			@Override
			public CommandExecutionIssue validateExecution(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				try {
					Float.parseFloat(argument);
					return null;
				} catch (NumberFormatException e) {
					return new CommandExecutionIssue("Cannot compute \"" + argument + "\" as a floating point number", argumentIndex);
				}
			}

			@Override
			public CommandExecutionIssue validateTabcomplete(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				try {
					Float.parseFloat(argument);
					return null;
				} catch (NumberFormatException e) {
					return new CommandExecutionIssue("Invalid float \"" + argument + "\"", argumentIndex);
				}
			}

			@Override
			protected Float toType(String input) 
			{
				return Float.parseFloat(input);
			}

			@Override
			protected List<String> getCompletions() 
			{
				return null;
			}
		};
	}

	public static ArgumentType<Player> onlinePlayer()
	{
		return new ArgumentType<>(Player.class)
		{

			@Override
			public CommandExecutionIssue validateExecution(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				return Bukkit.getPlayerExact(argument) != null ? null : new CommandExecutionIssue("No player is online with the name \"" + argument + "\"", argumentIndex);
			}

			@Override
			public CommandExecutionIssue validateTabcomplete(CommandSender sender, String argument, int argumentIndex,	String commandLabel, String... args)
			{
				return Bukkit.getPlayerExact(argument) != null ? null : new CommandExecutionIssue("No player named \"" + argument + "\" is online", argumentIndex);

			}

			@Override
			protected Player toType(String input)
			{
				return Bukkit.getPlayerExact(input);
			}

			@Override
			protected List<String> getCompletions()
			{
				return Lists.map(Bukkit.getOnlinePlayers(), (p) -> p.getName());
			}
		};
	}

	public static <T extends Enum<T>> ArgumentType<T> enumerator(Class<T> enumType)
	{
		return enumerator(enumType, null);
	}

	public static <T extends Enum<T>> ArgumentType<T> enumerator(Class<T> enumType, Predicate<String> filter)
	{
		return new ArgumentType<>(enumType)
		{
			// Cache and filter enum names to cut down on processing time
			private final List<String> processedEnums = new ArrayList<>(List.of(Arrays.map(enumType.getEnumConstants(), String.class, (e) -> e.name())))
				.stream()
				.filter(filter != null ? filter : (s) -> false)
				.toList();

			@Override
			public CommandExecutionIssue validateExecution(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				try {
					Enum.valueOf(enumType, argument);
					return null;
				} catch (IllegalArgumentException e) {
					CommandExecutionIssue issue = new CommandExecutionIssue("Unknown " + enumType.getSimpleName().toLowerCase() + " \"" + argument + "\"", argumentIndex);

					// Attempt to find similar enum to argument
					List<String> fuzzySearchResults = Strings.fuzzySearchSorted(argument, 3, new ArrayList<>(processedEnums));
					if (!fuzzySearchResults.isEmpty())
					{
						String[] options = new String[Math.min(3, fuzzySearchResults.size())];
						for (int i = 0; i < options.length; i++)
						{
							options[i] = fuzzySearchResults.get(i);
						}

						issue.addPossibleFix("Did you mean to type one of these?: " + Arrays.toString(options, ", ", null));
					}

					return issue;
				}
			}

			@Override
			public CommandExecutionIssue validateTabcomplete(CommandSender sender, String argument, int argumentIndex, String commandLabel, String... args) 
			{
				try {
					Enum.valueOf(enumType, argument);
					return null;
				} catch (IllegalArgumentException e) {
					return new CommandExecutionIssue("Invalid " + enumType.getSimpleName().toLowerCase() + " \"" + argument + "\"", argumentIndex);
				}
			}

			@Override
			protected T toType(String input) 
			{
				return Enum.valueOf(enumType, input);
			}

			@Override
			protected List<String> getCompletions() 
			{
				return processedEnums;
			}
		};
	}
}
