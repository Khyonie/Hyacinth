package coffee.khyonieheart.tidal;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.killswitch.KillswitchIdentifier;
import coffee.khyonieheart.hyacinth.killswitch.KillswitchTarget;
import coffee.khyonieheart.hyacinth.module.ModuleOwned;
import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;
import coffee.khyonieheart.hyacinth.print.Grammar;
import coffee.khyonieheart.hyacinth.util.ArrayIterator;
import coffee.khyonieheart.hyacinth.util.Arrays;
import coffee.khyonieheart.hyacinth.util.Lists;
import coffee.khyonieheart.tidal.TraversalResult.TraversalContext;
import coffee.khyonieheart.tidal.TraversalResult.TraversalStatus;
import coffee.khyonieheart.tidal.concatenation.Concatenation;
import coffee.khyonieheart.tidal.concatenation.ConcatenationFailureException;
import coffee.khyonieheart.tidal.structure.BranchMeta;
import coffee.khyonieheart.tidal.structure.CommandBranch;
import coffee.khyonieheart.tidal.structure.Root;
import coffee.khyonieheart.tidal.structure.Static;
import coffee.khyonieheart.tidal.validation.TypeManager;

@PreventAutoLoad
@KillswitchIdentifier({ "tidalCommandExecution" })
public abstract class TidalCommand extends Command implements ModuleOwned, KillswitchTarget
{
	private TabCompleter completionEngine = null;
	private static boolean executionEnabled = true;
	private Map<String, CommandBranch<?>> branches = new HashMap<>();

	@Deprecated
	private TidalCommand() 
	{
		super(null, null, null, null);
	}

	public TidalCommand(String name, String description, String usageMessage, String permission, String... aliases)
	{
		super(name, description, usageMessage, List.of(aliases));

		setPermission(permission);

		// Preprocess command to generate structure
		for (Method m : this.getClass().getDeclaredMethods())
		{
			if (!m.isAnnotationPresent(Root.class))
			{
				continue;
			}

			if (m.getParameterCount() == 0)
			{
				continue;
			}

			merge(m.getAnnotation(Root.class).value(), m.getParameters());
		}
	}

	private CommandBranch<?> merge(String root, Parameter[] args)
	{
		CommandBranch<?> branch = branches.get(root);

		if (branch == null)
		{
			branch = new CommandBranch<>(root, Void.class);
		}

		for (int i = 1; i < args.length; i++)
		{
			if (branch.hasArg(args[i].getName()))
			{
				branch = branch.get(args[i].getName());
				continue;
			}

			CommandBranch<?> newBranch = new CommandBranch<>(args[i].getName(), args[i].getType());

			if (args[i].isAnnotationPresent(BranchMeta.class))
			{
				newBranch.setSenderType(args[i].getAnnotation(BranchMeta.class).sender());

				if (!args[i].getAnnotation(BranchMeta.class).permission().equals("VOID"))
				{
					newBranch.setPermission(args[i].getAnnotation(BranchMeta.class).permission());
				}
			}

			if (args[i].isAnnotationPresent(Static.class))
			{
				branch = branch.add(newBranch);
				continue;
			}

			ArgumentType<?> validator;
			if ((validator = TypeManager.getValidator(args[i].getType())) != null)
			{
				newBranch.setHandler((ArgumentType<?>) validator);
			}

			branch = branch.add(newBranch);
		}

		return branch;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args)
	{
		if (!executionEnabled && !sender.hasPermission("tidal.bypassdisable"))
		{
			Message.send(sender, "§cTidal commands have been temporarily disabled.");
			return true;
		}

		if (args.length == 0)
		{
			// TODO No subcommand executor
			return true;
		}

		long time = System.currentTimeMillis();

		List<CommandExecutionIssue> issues = new ArrayList<>();
		List<Integer> argumentsToNotParse = new ArrayList<>();

		// Concatenate arguments
		
		try {
			args = Concatenation.concatenate(args, '"');
		} catch (ConcatenationFailureException e) {
			issues.add(new CommandExecutionIssue(
				switch (e.getType())
				{
					case UNEXPECTED_START -> "Unexpected quoted argument start";
					case UNEXPECTED_END -> "Unexpected quoted argument end";
					case UNTERMINATED_QUOTE -> "Unterminated quoted argument";
				}, 
				e.getIndex())
			);
			argumentsToNotParse.add(e.getIndex());
		}

		// Make args[0] lowercase
		args[0] = args[0].toLowerCase();

		// Preprocess arguments
		if (!this.branches.containsKey(args[0]))
		{
			issues.add(new CommandExecutionIssue("Unknown subcommand \"" + args[0] + "\"", 0));
			failWithIssueMessages(sender, issues, label, args);
			return true;
		}

		CommandBranch rootBranch = this.branches.get(args[0]);

		if (!rootBranch.isAuthorized(sender))
		{
			sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));
			return true;
		}

		Set<CommandBranch> involvedBranches = new LinkedHashSet<>();
		int traversalStatus = this.followBranches(args, involvedBranches);

		if (traversalStatus > 0)
		{
			issues.add(new CommandExecutionIssue("Unknown option \"" + args[0] + "\"", traversalStatus));
		}

		// Check if we need more arguments
		
		Iterator<CommandBranch> iterator = involvedBranches.iterator();
		boolean isComplete = true;
		while (iterator.hasNext())
		{
			CommandBranch branch = iterator.next();

			if (iterator.hasNext())
			{
				continue;
			}

			if (branch.requiresNext())
			{
				CommandExecutionIssue issue = new CommandExecutionIssue("Command is incomplete", args.length);
				
				switch (branch.getBranches().size())
				{
					case 1 -> issue.addPossibleFix("Append argument \"" + branch.getNext().getLabel() + "\" to the end of the command.");
					default -> issue.addPossibleFix("Append one of [ " + Lists.toString(new ArrayList<>(branch.getBranches()), ", ", null) + " ] to the end of the command.");
				}

				issues.add(issue);

				isComplete = false;
				break;
			}
		}
	
		// Obtain method and generate parameters

		Class<?>[] methodClasses = new Class<?>[involvedBranches.size() + 1];
		Object[] parameters = new Object[methodClasses.length];
		parameters[0] = sender;

		methodClasses[0] = rootBranch.getUserType().getClassType();

		int index = 0;
		for (CommandBranch branch : involvedBranches)
		{ 
			index++;

			if (branch.isTyped())
			{
				methodClasses[index] = branch.getType().getType();

				if (argumentsToNotParse.contains(index))
				{
					parameters[index] = args[index];
					continue;
				}

				CommandExecutionIssue issue = branch.getType().validateExecution(sender, args[index], index, label, args);
				if (issue != null)
				{
					issues.add(issue);
					continue;
				}

				parameters[index] = branch.getType().toType(args[index]);
				continue;
			}

			methodClasses[index] = String.class;
			parameters[index] = args[index];
		}

		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod(args[0], methodClasses);
		} catch (NoSuchMethodException | SecurityException e) {
			if (isComplete) 
			{
				issues.add(new CommandExecutionIssue("§cNo command exists for the given syntax: [" + Arrays.toString(methodClasses, ", ", (c) -> c.getSimpleName()) + "]", 0));
			}
		}

		Logger.debug("Command execution took " + (System.currentTimeMillis() - time) + "ms");

		// Fail if any issues arise
		if (!issues.isEmpty())
		{
			Message.send(sender, "§cCould not execute command /" + label + " " + Arrays.toString(args, " ", null));
			if (!isComplete)
			{
				String[] newArgs = new String[args.length + 1];
				for (int i = 0; i < args.length; i++)
				{
					newArgs[i] = args[i];
				}
				newArgs[args.length] = "...";
				args = newArgs;
			}

			failWithIssueMessages(sender, issues, label, args);
			return true;
		}

		// Invoke
		try {
			method.invoke(this, parameters);
		} catch (Exception e) {
			Message.send(sender, "§cAn error occurred when invoking this command.");
			e.printStackTrace();
		}

		return true;
	}

	private static void failWithIssueMessages(CommandSender sender, List<CommandExecutionIssue> issues, String commandLabel, String[] args)
	{
		int index = 0;
		for (CommandExecutionIssue issue : issues)
		{
			Message.send(sender, "§cError " + (index + 1) + ": §7" + issue.getMessage() + " at position " + issue.getIndex());
			//Message.send(sender, "§c" + (switch (index) { case 0 -> "Your first fuck-up"; case 1 -> "You also did this wrong"; case 2 -> "Holy shit there's more?!"; default -> null;}) + ": §7" + issue.getMessage() + " at position " + issue.getIndex());
			String argumentBackup = args[issue.getIndex()];

			args[issue.getIndex()] = "§e§n" + args[issue.getIndex()] + "§e (← Here)§7";

			Message.send(sender, "§c§l⤷ §7/" + commandLabel + " " + Arrays.toString(args, " ", null));

			if (issue.hasPossibleFix())
			{
				Message.send(sender, "§7 §9 §o Possible solution: " + issue.getPossibleFix());
			}

			args[issue.getIndex()] = argumentBackup;

			if (index == 2 && issues.size() > 3)
			{
				Message.send(sender, "§7§o... +" + (issues.size() - 3) + Grammar.plural(issues.size() - 3, " additional error ", " additional errors ") + "not shown");
				break;
			}

			index++;
		}
	}

	public TraversalResult traverse(String rootLabel, String commandLabel, CommandSender sender, TraversalContext context, List<CommandExecutionIssue> issues, String... args)
	{
		Deque<CommandBranch<?>> branches = new ArrayDeque<>();
		CommandBranch<?> branch = this.branches.get(rootLabel);

		if (branch == null)
		{
			return new TraversalResult(branches, TraversalStatus.ERR_NO_ROOT);
		}

		branches.push(branch);

		// /command subcommand arg1 <int> arg3 

		List<CommandExecutionIssue> issues = new ArrayList<>();
		for (int i = 1; i < args.length; i++)
		{
			String arg = args[i];

			if (branch.leadsToTyped() && i + 1 < args.length)
			{
				branch = branch.nextTyped();
				branches.push(branch);

				arg = args[++i];

				// Check user permission against branch permission
				if (branch.getPermission() != null)
				{
					if (!sender.hasPermission(branch.getPermission()))
					{
						return new TraversalResult(branches, TraversalStatus.ERR_NO_PERMISSION);
					}
				}

				// Check sender type against branch expected type
				if (branch.getSenderType() != null)
				{
					if (!branch.getSenderType().isAssignableFrom(sender.getClass()))
					{
						return new TraversalResult(branches, TraversalStatus.ERR_ILLEGAL_USER);
					}
				}

				// Validate typed argument based on context
				if (branch.getArgumentType() != null)
				{
					CommandExecutionIssue issue = switch (context)
					{
						case TABCOMPLETE -> branch.getArgumentType().validateTabcomplete(sender, arg, i, commandLabel, args);
						case EXECUTION -> branch.getArgumentType().validateExecution(sender, arg, i, commandLabel, args);
					};

					if (issue != null)
					{
						issues.add(issue);
					}
				}
				
				continue;
			}

			if (!branch.hasArg(arg))
			{

			}
		}
	}

	public void addParser(TabCompleter parser)
	{
		this.completionEngine = parser;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args)
	{
		if (this.completionEngine == null)
		{
			return super.tabComplete(sender, alias, args);
		}

		return this.completionEngine.onTabComplete(sender, this, alias, args);
	}

	// Killswitch

	@Override
	public boolean kill(String target)
	{
		return switch (target)
		{
			case "executionEnabled" -> {
				if (executionEnabled)
				{
					yield !(executionEnabled = false);
				}
				yield false;
			}
			default -> false;
		};
	}

	@Override
	public boolean reenable(String target)
	{
		return switch (target)
		{
			case "executionEnabled" -> {
				if (!executionEnabled)
				{
					yield executionEnabled = true;
				}

				yield false;
			}
			default -> false;
		};
	}

	@Override
	public boolean isEnabled(String target)
	{
		return switch (target)
		{
			case "executionEnabled" -> executionEnabled;
			default -> false;
		};
	}
}
