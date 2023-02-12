package coffee.khyonieheart.hyacinth.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import coffee.khyonieheart.hyacinth.Message;
import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public abstract class HyacinthCommand extends Command implements IHyacinthCommand
{
    public HyacinthCommand(
        @NotNull String label,
		@Nullable String permission
    ) {
        super(label.toLowerCase());
		this.setPermission(permission);
    }

    public HyacinthCommand(
        @NotNull String label, 
        @Nullable String usage, 
		@Nullable String permission,
        @NotEmpty String... aliases
    ) {
        super(label.toLowerCase(), "Hyacinth: /" + label, (usage == null ? "No example" : usage), List.of(aliases));
		this.setPermission(permission);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
		if (!this.testPermission(sender))
		{
			return true;
		}

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

            commandMethod = this.getClass().getMethod(prefix + (args.length == 0 ? commandLabel : args[0]).toLowerCase(), CommandSender.class, String[].class);
            
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
