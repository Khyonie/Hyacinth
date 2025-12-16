package coffee.khyonieheart.hyacinth.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.crafthyacinth.command.intrinsic.ConcatenationIntrinsic;
import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.command.intrinsic.IntrinsicValidator;
import coffee.khyonieheart.hyacinth.command.parser.CompletionBranch;
import coffee.khyonieheart.hyacinth.command.parser.CompletionRoot;
import coffee.khyonieheart.hyacinth.command.parser.DualValidator;
import coffee.khyonieheart.hyacinth.command.parser.ExecutionValidator;
import coffee.khyonieheart.hyacinth.command.parser.Validator;
import coffee.khyonieheart.hyacinth.command.parser.ValidatorContext;
import coffee.khyonieheart.hyacinth.module.ModuleOwned;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;

public abstract class HyacinthCommand extends Command implements ModuleOwned
{
	private TabCompleter tabCompletionEngine;
	private CompletionRoot completionTrees = new CompletionRoot();

	// Validators that parse the entire command
	private List<IntrinsicValidator> intrinsicValidators = new ArrayList<>();

    public HyacinthCommand(
        @NotNull String label,
		@Nullable String permission
    ) {
        super(label.toLowerCase());
		this.setPermission(permission);
		this.addIntrinsic(new ConcatenationIntrinsic());
    }

    public HyacinthCommand(
        @NotNull String label, 
        @Nullable String usage, 
		@Nullable String permission,
        @NotEmpty String... aliases
    ) {
        super(label.toLowerCase(), "Hyacinth: /" + label, (usage == null ? "No example" : usage), List.of(aliases));
		this.setPermission(permission);
		this.addIntrinsic(new ConcatenationIntrinsic());
    }

	/**
	 * Adds an intrinsic (validator that handles an entire command) to this command
	 */
	public void addIntrinsic(
		@NotNull IntrinsicValidator validator
	) {
		this.intrinsicValidators.add(validator);
	}

	public List<IntrinsicValidator> getIntrinsics()
	{
		return Collections.unmodifiableList(this.intrinsicValidators);
	}

	public CompletionRoot getTrees()
	{
		return this.completionTrees;
	}

	public void setParser(
		@NotNull TabCompleter parser
	) {
		this.tabCompletionEngine = parser;	
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args)
	{
		if (this.tabCompletionEngine == null)
		{
			return super.tabComplete(sender, alias, args);
		}

		return this.tabCompletionEngine.onTabComplete(sender, this, alias, args);
	}

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
		if (!this.testPermission(sender))
		{
			return true;
		}

		// Process intrinsics
		for (IntrinsicValidator intrinsic : this.intrinsicValidators)
		{
			Option opt = intrinsic.validate(sender, ValidatorContext.EXECUTION, commandLabel, args.length, args);

			if (opt.isSome())
			{
				Message.send(sender, opt.unwrap(String.class));
				return true;
			}
		}

		// Preprocess quoted arguments 
		// Because of this process, all commands possess an intrinsic validator that ensures the command is correctly quoted
		List<String> processedArguments = new ArrayList<>(args.length);
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if (arg == null)
			{
				continue;
			}

			if (arg.startsWith("\""))
			{
				if (arg.endsWith("\""))
				{
					processedArguments.add(arg.replace("\"", ""));
					continue;
				}

				builder.append(arg.replace("\"", "") + " ");
				continue;
			}

			if (builder.length() == 0)
			{
				processedArguments.add(arg);
				continue;
			}

			if (arg.endsWith("\""))
			{
				builder.append(arg.replace("\"", ""));

				// Filter out empty and single-space strings
				if (!builder.isEmpty() && !builder.toString().equals(" "))
				{
					processedArguments.add(builder.toString());
				}

				builder = new StringBuilder();
				continue;
			}

			builder.append(arg + " ");
		}

		// String[] preProcessedArgs = args;
		args = processedArguments.toArray(new String[processedArguments.size()]);

		// Execute
        String prefix = this.getClass().isAnnotationPresent(SubcommandPrefix.class) ? this.getClass().getAnnotation(SubcommandPrefix.class).value() : "";
        Method commandMethod;

        try {
			if (args.length == 0)
			{
				// Search for a nosubcmd executor
				for (Method m : this.getClass().getMethods())
				{
					if (!m.isAnnotationPresent(NoSubCommandExecutor.class))
					{
						continue;
					}

					m.invoke(this, sender, args);

					return true;
				}
				throw new NoSuchMethodException();
			}

			try {
            	commandMethod = this.getClass().getMethod(prefix + (args.length == 0 ? commandLabel : args[0]).toLowerCase(), CommandSender.class, String[].class);
			} catch (NoSuchMethodException e) {
				commandMethod = this.getClass().getDeclaredMethod(prefix + (args.length == 0 ? commandLabel : args[0]).toLowerCase(), CommandSender.class, String[].class);
				commandMethod.setAccessible(true);
			}

			if (this.getTrees().getTree(args[0]) != null)
			{
				CompletionBranch branch = this.getTrees().getTree(args[0]);

				if (branch.hasValidator())
				{
					for (Validator validator : branch.getValidators())
					{
						if (!(validator instanceof DualValidator) && !(validator instanceof ExecutionValidator))
						{
							continue;
						}
						
						Option opt = validator.validate(sender, ValidatorContext.EXECUTION, branch, args[0], 0, commandLabel, args);
						if (opt.isNone())
						{
							continue;
						}

						//Message.send(sender, "§cSubcommand " + args[0] + " cannot be executed right now.");
						Message.send(sender, opt.unwrap(String.class));
						return true;
					}
				}

				CompletionBranch nextBranch;

				if (args.length > 0)
				{
					for (int i = 1; i < args.length; i++)
					{
						nextBranch = branch.get(args[i]);

						if (nextBranch == null)
						{
							break;
						}

						branch = nextBranch;
						if (!branch.hasValidator())
						{
							continue;
						}

						for (Validator validator : branch.getValidators())
						{
							if (!(validator instanceof DualValidator) && !(validator instanceof ExecutionValidator))
							{
								continue;
							}

							Option opt = validator.validate(sender, ValidatorContext.EXECUTION, branch, args[i], i, commandLabel, args);
							if (opt.isNone())
							{
								continue;
							}

							Message.send(sender, opt.unwrap(String.class));
							return true;
						}
					}
				}
			}

            commandMethod.invoke(this, sender, args);
        } catch (NoSuchMethodException e) {
            Message.send(sender, "§cNo such command \"/" + (args.length == 0 ? commandLabel : commandLabel + " " + args[0]).toLowerCase() + "\"");
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Message.send(sender, "§cCommand failed to execute.");
            e.printStackTrace();
        }

        return true;
    }

	public boolean testPermissionStringSilent(
		@NotNull CommandSender target, 
		@Nullable String permission
	) {
		if (permission == null)
		{
			return true;
		}
		if (permission.length() == 0)
		{
			return true;
		}

		for (String perm : permission.split(";"))
		{
			if (target.hasPermission(perm))
			{
				return true;
			}
		}

		return false;
	}
}
