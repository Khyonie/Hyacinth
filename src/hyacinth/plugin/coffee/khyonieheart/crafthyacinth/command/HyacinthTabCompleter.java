package coffee.khyonieheart.crafthyacinth.command;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import coffee.khyonieheart.anenome.Arrays;
import coffee.khyonieheart.crafthyacinth.command.parser.validator.PermissionValidator;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.command.HyacinthCommand;
import coffee.khyonieheart.hyacinth.command.NoSubCommandExecutor;
import coffee.khyonieheart.hyacinth.command.SubcommandPrefix;
import coffee.khyonieheart.hyacinth.command.intrinsic.IntrinsicValidator;
import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.CompletionRoot;
import coffee.khyonieheart.hyacinth.command.parser.DualValidator;
import coffee.khyonieheart.hyacinth.command.parser.SuggestionManager;
import coffee.khyonieheart.hyacinth.command.parser.SuggestionValidator;
import coffee.khyonieheart.hyacinth.command.parser.Validator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.util.Lists;
import coffee.khyonieheart.hyacinth.util.Strings;

/**
 * First party implementation of a command parser.
 *
 * @author Khyonie
 * @since 1.0.0
 *
 * @deprecated Will be supersceded by Tidal 2.0.
 */
@Deprecated
public class HyacinthTabCompleter implements TabCompleter
{
	private HyacinthCommand command;
	private static final Class<?>[] VALID_COMMAND_PARAMETERS = { CommandSender.class, String[].class };

	/**
	 * Constructor setting the command to handle tab completion for.
	 *
	 * @param command Command
	 *
	 * @since 1.0.0
	 */
	public HyacinthTabCompleter(HyacinthCommand command)
	{
		this.command = command;
	}

	@Override
	public List<String> onTabComplete(
		CommandSender sender, 
		Command command, 
		String label, 
		String[] args
	) {
		if (!(command instanceof HyacinthCommand))
		{
			return Collections.emptyList();
		}

		return this.onTabComplete(sender, label, args);
	}

	private List<String> onTabComplete(
		CommandSender sender,
		String label,
		String[] args
	) {
		List<String> prospectiveMethods = new ArrayList<>();

		Logger.verbose("### Starting new tab completion for input \"" + label + " " + Arrays.toString(args, " ", null) + "\"");

		Logger.verbose("Processing intrinsics");
		for (IntrinsicValidator intrinsic : command.getIntrinsics())
		{
			Option opt = intrinsic.validate(sender, ValidatorContext.TABCOMPLETE, label, args.length, args);
			if (opt.isSome())
			{
				return List.of(opt.unwrap(String.class));
			}
		}

		// Concatenate quoted strings
		List<String> processedArguments = new ArrayList<>(args.length);
		StringBuilder builder = new StringBuilder();
	
		Logger.verbose("Concatenating quoted strings");
		for (int i = 0; i < args.length; i++)
		{
			Logger.verbose("- Argument: \"" + args[i] + "\"");

			for (String s : processedArguments)
			{
				Logger.verbose("- - Processed argument: \"" + s + "\"");
			}

			String arg = args[i];
			if (arg == null)
			{
				continue;
			}

			if (arg.equals("\""))
			{
				Logger.verbose("- Argument is a floating mark, skipping");
				continue;
			}

			if (arg.startsWith("\""))
			{
				Logger.verbose("- Argument starts with a quotation mark");
				if (arg.endsWith("\""))
				{
					Logger.verbose("§a- Argument is a single quoted argument, adding");
					processedArguments.add(arg.replace("\"", ""));
					continue;
				}

				builder.append(arg.replace("\"", "") + " ");
				Logger.verbose("§b- Argument is the start of a quoted argument, adding to current buffer: \"" + builder.toString() + "\" (size " + builder.length() + ")");

				continue;
			}

			if (builder.length() == 0)
			{
				Logger.verbose("§a- Argument is outside of a quoted argument, adding");
				processedArguments.add(arg);
				continue;
			}

			if (arg.endsWith("\""))
			{
				builder.append(arg.replace("\"", ""));
				Logger.verbose("§b- Argument is the end of a quoted argument, buffer is finished: \"" + builder.toString() + "\", (size " + builder.length() + ")");

				// Filter out empty and single-space strings
				if (!builder.isEmpty() && !builder.toString().equals(" "))
				{
					Logger.verbose("§a- - Added quoted argument: \"" + builder.toString() + "\"");
					processedArguments.add(builder.toString());
				}

				builder = new StringBuilder();
				continue;
			}

			builder.append(arg + " ");
			Logger.verbose("§b- Argument is inside a quoted argument, adding to current buffer: \"" + builder.toString() + "\" (size " + builder.length() + ")");
		}

		// String[] preProcessedArgs = args;
		args = processedArguments.toArray(new String[processedArguments.size()]);

		Logger.verbose("Finished arguments: [" + Arrays.toString(args, "\", \"", null) + "]");

		Logger.verbose("Gathering valid command methods");
		for (Method m : command.getClass().getDeclaredMethods())
		{
			Logger.verbose("- Method: " + Modifier.toString(m.getModifiers()) + " " + m.getName());
			// Method signature
			if (m.getParameterCount() != 2)
			{
				Logger.verbose("- X Not enough parameters to be valid");
				continue;
			}

			if (!java.util.Arrays.equals(m.getParameterTypes(), VALID_COMMAND_PARAMETERS))
			{
				Logger.verbose("- X Parameters are not valid command parameters");
				continue;
			}

			if (m.isAnnotationPresent(NoSubCommandExecutor.class))
			{
				Logger.verbose("- X Method is a no-subcommand executor");
				continue;
			}

			// Hide not-public methods
			if (!Modifier.isPublic(m.getModifiers()))
			{
				Logger.verbose("- X Method is not public");
				continue;
			}

			Logger.verbose("- O Adding method to list");
			prospectiveMethods.add(m.getName());
		}

		// Trim prefix if present
		if (command.getClass().isAnnotationPresent(SubcommandPrefix.class))
		{
			Logger.verbose("Trimming prefixes");
			int prefixIndex = command.getClass().getAnnotation(SubcommandPrefix.class).value().length();
			prospectiveMethods.replaceAll((name) -> {
				return name.substring(prefixIndex);
			});
			Logger.verbose("Prefixes trimmed: [" + Lists.toString(prospectiveMethods, " ", null) + "]");
		}

		Logger.verbose("Valid command roots: [" + Lists.toString(prospectiveMethods, ", ", null) + "]");

		Logger.verbose("Testing permissions for all branches");

		CompletionRoot trees = command.getTrees();	
		prospectiveMethods.removeIf((b) -> {
			if (trees.getTree(b) == null)
			{
				return false;
			}

			for (Validator v : trees.getTree(b).getValidators())
			{
				if (v instanceof PermissionValidator pv)
				{
					if (!sender.hasPermission(pv.getPermission()))
					{
						return true;
					}
				}
			}

			return false;
		});

		// args[0] will always be the subcommand unless not present
		if (args.length == 0)
		{
			Logger.verbose("§a-> No subcommand is present, suggesting all roots as-is: [" + Lists.toString(prospectiveMethods, ", ", null) + "]");
			return prospectiveMethods;
		}

		Logger.verbose("- Subcommand is present: \"" + args[0] + "\"");
		List<String> matches = Strings.copyFuzzyMatches(args[0], prospectiveMethods);
		// StringUtil.copyPartialMatches(args[0], prospectiveMethods, matches);

		Logger.verbose("- Copied partial matches: [" + Lists.toString(matches, ", ", null) + "]");


		if (args.length == 1)
		{
			Logger.verbose("§a-> No subcommand arguments are present, suggesting roots: [" + Lists.toString(matches, ", ", null) + "]");
			return matches;
		}

		Logger.verbose("- Subcommand arguments are present");

		// Find an appropriate tree
		CompletionBranch branch = trees.getTree(args[0]);
		if (branch == null)
		{
			Logger.verbose("§c-> Root \"" + args[0] + "\" does not exist, no suggestions are available");
			return Collections.emptyList();
		}

		Logger.verbose("- Root exists, traversing to up to " + (args.length - 1) + " branch(es)");

		// Tree found, traverse until the last argument is reached
		int index = 1;
		CompletionBranch nextBranch;

		Logger.verbose("Acquiring next branch");
		while (index < args.length)
		{
			if (branch.isLeaf())
			{
				Logger.verbose("§c-> Branch is a leaf, no suggestions are available");
				return Collections.emptyList();
			}

			if (branch.allBranches().length == 1)
			{
				Logger.verbose("- (Userargs) - Branch has only one extending branch: [" + Arrays.toString(branch.allBranches(), ", ", null) + "]");
				CompletionBranch userArgument = branch.get(branch.allBranches()[0]);
				if (branch.allBranches()[0].startsWith(("<")) && branch.allBranches()[0].endsWith(">"))
				{
					Logger.verbose("- (Userargs) - Does user argument possess a validator: " + userArgument.hasValidator());
					if (userArgument.hasValidator() && args[index].length() > 0)
					{
						for (Validator v : userArgument.getValidators())
						{
							if (!(v instanceof DualValidator) && !(v instanceof SuggestionValidator))
							{
								continue;
							}

							Option opt = v.validate(sender, ValidatorContext.TABCOMPLETE, userArgument, args[index], index, label, args);
							if (opt.isSome())
							{
								Logger.verbose("§c-> Failed validation for argument \"" + args[index] + "\", unwrapping error");
								return List.of(opt.unwrap(String.class));
							}
						}
						Logger.verbose("- (Userargs) - Validation succeeded");
					}

					if ((index + 1) == args.length)
					{
						Logger.verbose("§a- (Userargs) - Branch is a user argument, and no more arguments exist to parse. Handling final completions");
						break;
					}
					Logger.verbose("§a- (Userargs) - Branch is a user argument, traversing into it");
					branch = branch.get(branch.allBranches()[0]);

					index++;
					continue;
				}
				Logger.verbose("- (Userargs) - Branch is not a user argument, moving on");
			}

			Logger.verbose("- Acquiring next branch \"" + args[index] + "\"");

			nextBranch = branch.get(args[index]);

			if (nextBranch == null)
			{
				Logger.verbose("§9 - X Branch does not exist, handling final completions");
				break;
			}

			branch = nextBranch;

			if (++index == args.length)
			{
				Logger.verbose("§a-> No more arguments to parse, no suggestions are available");
				return Collections.emptyList();
			}
		}

		Logger.verbose("Handling final completions");

		matches.clear();
		List<String> completions = List.of(branch.allBranches());

		Logger.verbose("Available completions before trim: [" + Lists.toString(completions, ", ", null) + "]");

		// Handle user arguments
		if (completions.size() == 1)
		{ 
			Logger.verbose("- (Userargs) - Branch only has one completion, checking if it is a user argument: [" + Lists.toString(completions, ", ", null) + "]");
			if (completions.get(0).startsWith("<") && completions.get(0).endsWith(">"))
			{
				Logger.verbose("- (Userargs) - Branch is a user argument, making sure it's valid (I.E != \"<>\")");
				if (completions.get(0).length() != 2)
				{
					String userArgument = completions.get(0).substring(1, completions.get(0).length() - 1);
					Logger.verbose("- (Userargs) - User argument is valid, trimming into \"" + userArgument + "\", and checking if a suggestion engine exists for it");
					
					if (SuggestionManager.hasEngine(userArgument))
					{
						Logger.verbose("§a- (Userargs) - Suggestion engine exists, generating suggestions");
						completions = SuggestionManager.getSuggestionEngine(userArgument).generateSuggestions();
						
						// If a completion contains a space, automatically surround it in quotes

						// Test for an immutable collection
						try {
							completions.addAll(Collections.emptyList());
						} catch (UnsupportedOperationException e) {
							completions = new ArrayList<>(completions);
						}

						for (int i = 0; i < completions.size(); i++)
						{
							if (completions.get(i).contains(" "))
							{
								completions.set(i, "\"" + completions.get(i) + "\"");
							}
						}
					} else {
						return List.of(userArgument);
					}
				}
			}
		}

		matches = Strings.copyFuzzyMatches(args[index], completions);
		//StringUtil.copyPartialMatches(args[index], completions, matches);
		Logger.verbose("§a-> Final completions: [" + Lists.toString(matches, ", ", null) + "]");

		return matches;
	}
}
