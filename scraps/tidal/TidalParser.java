package coffee.khyonieheart.tidal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.print.Grammar;
import coffee.khyonieheart.hyacinth.util.Arrays;
import coffee.khyonieheart.hyacinth.util.Lists;
import coffee.khyonieheart.tidal.concatenation.Concatenation;
import coffee.khyonieheart.tidal.concatenation.ConcatenationFailureException;

public class TidalParser implements TabCompleter
{
	TidalCommand command;

	public TidalParser(TidalCommand command)
	{
		this.command = command;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command comand, String label, String[] args)
	{
		return onTabComplete(sender, command, label, args);
	}

	private List<String> onTabComplete(CommandSender sender, TidalCommand command, String label, String[] args) 
	{
		Logger.verbose("[Tidal] Preprocessing arguments prior to tab-completion...");

		try {
			args = Concatenation.concatenate(args, '\"');
		} catch (ConcatenationFailureException e) {
			Logger.verbose("[Tidal] §cConcatenation process failed. Reporting error.");
			String cause = switch (e.getType())
			{
				case UNEXPECTED_START -> "Unexpected quoted argument start @ position " + e.getIndex();
				case UNEXPECTED_END -> "Unexpected quoted argument end @ position " + e.getIndex();
				case UNTERMINATED_QUOTE -> "Trailing quoted argument @ position " + e.getIndex();
			};
			return List.of("§c(" + cause + ")");
		}

		Logger.verbose("[Tidal] §a## Starting new completion for (/" + label + " " + Arrays.toString(args, " ", null) + ")");

		for (int i = 0; i < args.length; i++)
		{
			Logger.verbose("[Tidal] Argument " + i + ": \"" + args[i] + "\"");
		}

		Logger.verbose("[Tidal] All valid subcommands for /" + label + ": ");
		for (String s : command.getRoots())
		{
			if (command.getRoot(s).requiresAuthorization())
			{
				Logger.verbose("[Tidal] - §e^" + s + " (User type: " + (command.getRoot(s).getUserType() != null ? command.getRoot(s).getUserType().name() : "*") + ", permission: " + command.getRoot(s).getPermission() + ")");
				continue;
			}

			Logger.verbose("[Tidal] - " + s);
		}

		if (args.length == 0)
		{
			// TODO Run no-subcommand executor
			return List.of();
		}

		if (args[0].equals(""))
		{
			return command.getRoots();
		}

		CommandBranch branch = command.getRoot(args[0]);
		if (branch == null)
		{
			return command.getRoots();
		}

		if (args.length == 1)
		{
			return List.of();
		}

		String arg = null;
		List<CommandExecutionIssue> issues = new ArrayList<>();
		int i = 1;
		for (; i < args.length; i++)
		{
			Logger.verbose("[Tidal] - - §fArgument " + i + ": \"" + args[i] + "\"");
			arg = args[i];
			if (branch.isLeaf())
			{
				Logger.verbose("[Tidal] - - §cCurrent branch is a leaf, no suggestions are available");
				return Collections.emptyList();
			}

			Logger.verbose("[Tidal] - - - Branches from here: [" + Lists.toString(new ArrayList<>(branch.getBranches()), null) + "]");
			if (branch.getBranches().size() == 1)
			{
				Logger.verbose("[Tidal] - - - Only one branch is available from here, is it a typed branch? " + branch.getNext().isTyped());
				if (branch.getNext().isTyped())
				{
					Logger.verbose("[Tidal] - - - - Branch is a typed branch, moving into it");
					branch = branch.getNext();
					Logger.verbose("[Tidal] - - - - New current branch: " + branch.getLabel());

					CommandExecutionIssue issue = branch.getType().validateTabcomplete(sender, args[i], i, label, args);

					if (issue != null && !arg.equals(""))
					{
						Logger.verbose("[Tidal] - - - - §dValidation failed, added to report");
						issues.add(issue);
					}

					continue;
				}
				Logger.verbose("[Tidal] - - - Branch is not a typed branch, checking for a static branch");
			}

			branch = branch.get(arg);
			if (branch == null)
			{
				Logger.verbose("[Tidal] - - - §cNo such static branch \"" + arg + "\"");
				issues.add(new CommandExecutionIssue("Invalid option \"" + arg + "\" @ position " + i, i));
				break;
			}
			Logger.verbose("[Tidal] - - - - Static branch exists, continuing");
		}

		if (!issues.isEmpty())
		{
			if (!branch.requiresNext())
			{
				Logger.verbose("[Tidal] §cIssues are present. Reporting.");
				return List.of("§c(⚠ " + issues.get(issues.size() - 1).getMessage() + (issues.size() > 1 ? " §o...+" + (issues.size() - 1) + " more " + Grammar.plural(issues.size() - 1, "error", "errors") + "§c)" : ")"));
			}
		}

		Logger.verbose("[Tidal] §6Handling final completions");

		if (branch.isTyped())
		{
			Logger.verbose("[Tidal] - Branch is typed, attempting to generate completions");
			List<String> completions = branch.getType().getCompletions();
			if (completions == null)
			{
				Logger.verbose("[Tidal] - - §aBranch does not support completions, returning branch label");
				return List.of(branch.getLabel());
			}

			Logger.verbose("[Tidal] - - §aBranch reported completions, suggesting after performing fuzzy search");
			return Strings.fuzzySearch(arg, 3, completions);
		}

		Logger.verbose("[Tidal] - Branch is not typed, suggesting available branches after performing fuzzy search");
		return Strings.fuzzySearch(arg, 3, new ArrayList<>(branch.getBranches()));
	}
}
