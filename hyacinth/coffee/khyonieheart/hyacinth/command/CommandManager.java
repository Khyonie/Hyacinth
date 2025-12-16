package coffee.khyonieheart.hyacinth.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.hyacinth.module.ModuleOwned;

/**
 * A command manager which allows registration, deregistration, and lookups for Hyacinth commands.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public interface CommandManager 
{
    /**
     * Registers a command to the server.
     * @param name Label of the command, e.g "label" as "/label"
     * @param command Not-null command instance
     * @param server Optional server instance
     * @apiNote If no server is provided but a {@link CommandMap} is needed, acquire server via {@link Bukkit#getServer()}.
     */
    public <T extends Command & ModuleOwned> void register(
        @Nullable String name, 
        @NotNull T command, 
        @Nullable Server server
    );

    /**
     * Unregisters a command from the server.
     * @param name Label of command to be unregistered.
     */
    public void unregister(
        @NotNull String name
    );

    /**
     * Obtains a command that has been registered to the server.
     * @param name Name or alias of command.
     * @return a command with the given name or alias.
     */
    public Command getRegisteredCommand(
        @NotNull String name
    );

	@Nullable
	public CommandMap getCommandMap();
}
