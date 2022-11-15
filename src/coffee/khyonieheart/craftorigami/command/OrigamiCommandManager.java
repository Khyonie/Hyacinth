package coffee.khyonieheart.craftorigami.command;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import coffee.khyonieheart.origami.Logger;
import coffee.khyonieheart.origami.command.CommandManager;
import coffee.khyonieheart.origami.command.OrigamiCommand;
import coffee.khyonieheart.origami.util.marker.Nullable;

public class OrigamiCommandManager implements CommandManager
{
    private CommandMap activeCommandMap;
    private Map<String, OrigamiCommand> registeredCommands;

    @Override
    public void register(String name, OrigamiCommand command, Server server) 
    {
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
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void unregister(String name) 
    {
        
    }
    
    @Override
    @Nullable
    public OrigamiCommand getOrigamiCommand(String name) 
    {
        return null;
    }
}
