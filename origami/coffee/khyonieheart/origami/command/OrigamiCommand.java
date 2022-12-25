package coffee.khyonieheart.origami.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import coffee.khyonieheart.origami.Message;
import coffee.khyonieheart.origami.util.marker.NotEmpty;
import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

public abstract class OrigamiCommand extends Command implements IOrigamiCommand
{
    public OrigamiCommand(
        @NotNull String label
    ) {
        super(label.toLowerCase());
    }

    public OrigamiCommand(
        @NotNull String label, 
        @Nullable String usage, 
        @NotEmpty String... aliases
    ) {
        super(label.toLowerCase(), "Origami: /" + label, (usage == null ? "No example" : usage), List.of(aliases));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args)
    {
        String prefix = this.getClass().isAnnotationPresent(SubcommandPrefix.class) ? this.getClass().getAnnotation(SubcommandPrefix.class).value() : "";

        Method commandMethod;

        try {
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
}
