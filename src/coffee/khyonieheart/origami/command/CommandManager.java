package coffee.khyonieheart.origami.command;

import org.bukkit.Server;

import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

public interface CommandManager 
{
    public void register(@Nullable String name, @NotNull OrigamiCommand command, @Nullable Server server);
    public void unregister(@NotNull String name);

    public OrigamiCommand getOrigamiCommand(@NotNull String name);
}
