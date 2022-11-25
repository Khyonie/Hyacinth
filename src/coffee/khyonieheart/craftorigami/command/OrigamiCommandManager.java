package coffee.khyonieheart.craftorigami.command;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import coffee.khyonieheart.origami.Logger;
import coffee.khyonieheart.origami.command.CommandManager;
import coffee.khyonieheart.origami.command.OrigamiCommand;
import coffee.khyonieheart.origami.print.Grammar;
import coffee.khyonieheart.origami.util.marker.Nullable;

public class OrigamiCommandManager implements CommandManager
{
    private CommandMap activeCommandMap;
    private Map<String, OrigamiCommand> registeredCommands;

    @Override
    public void register(String name, OrigamiCommand command, Server server) 
    {
        Logger.verbose("Attempting to register command \"/" + name + "\"");

        Objects.requireNonNull(command);

        if (activeCommandMap == null)
        {
            Logger.verbose("No command map has been set, attempting to cache off of server \"" + (server == null ? "null" : server.getClass().getName() + "\""));
            if (server == null)
            {
                Logger.verbose("§cCommand \"" + name + "\" (class: " + (command == null ? "null" : command.getClass().getName()) + ") attempted to register with a null server before command map has been cached! Using Bukkit.getServer() as fallback...");
                server = Bukkit.getServer();
            }

            Field serverCommandMapField;
            try {
                serverCommandMapField = server.getClass().getDeclaredField("commandMap");
                serverCommandMapField.setAccessible(true);
    
                activeCommandMap = (CommandMap) serverCommandMapField.get(server);

                Objects.requireNonNull(activeCommandMap);

                Logger.verbose("Successfully cached command map of type \"" + activeCommandMap.getClass().getName() + "\"");
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                Logger.verbose("§cFailed to register command \"" + command.getName() + "\" of class " + command.getClass().getName());
                e.printStackTrace();
                return;
            }
        }

        registeredCommands.put(name, command);
        command.getAliases().forEach((alias) -> registeredCommands.put(alias, command));

        activeCommandMap.register(name, command);

        Logger.verbose("§9Successfully registered command \"/" + name + "\" with " + command.getAliases().size() + " " + Grammar.plural(command.getAliases().size(), "alias", "aliases"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unregister(String name) 
    {
        Logger.verbose("Unregistering command \"/" + name + "\"");
        registeredCommands.remove(name);

        // Unregister from Bukkit
        try {
            Method methodGetKnownCommands = activeCommandMap.getClass().getDeclaredMethod("getKnownCommands");
            Map<String, Command> commands = (Map<String, Command>) methodGetKnownCommands.invoke(activeCommandMap);

            commands.remove(name);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            Logger.verbose("§cFailed to unregister command \"/" + name + "\"");
            e.printStackTrace();
            return;
        }
    }
    
    @Override
    @Nullable
    public OrigamiCommand getOrigamiCommand(String name) 
    {
        return registeredCommands.get(name);
    }
}
