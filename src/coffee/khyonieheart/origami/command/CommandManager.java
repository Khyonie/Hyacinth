package coffee.khyonieheart.origami.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

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
     * @param name
     * @return
     */
    public OrigamiCommand getOrigamiCommand(
        @NotNull String name
    );
}
