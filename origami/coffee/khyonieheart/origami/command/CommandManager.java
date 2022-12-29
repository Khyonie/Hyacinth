package coffee.khyonieheart.origami.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

/**
 * A command manager which allows registration, deregistration, and lookups for Origami commands.
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
     * @apiNote If no server is provided but an {@link CommandMap} is needed, acquire server via {@link Bukkit#getServer()}.
     */
    public void register(
        @Nullable String name, 
        @NotNull OrigamiCommand command, 
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
     * Obtains an Origami command that has been registered to the server.
     * @param name Name or alias of Origami command.
     * @return An Origami command with the given name or alias.
     */
    public OrigamiCommand getOrigamiCommand(
        @NotNull String name
    );
}
