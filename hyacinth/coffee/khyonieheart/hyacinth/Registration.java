package coffee.khyonieheart.hyacinth;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import coffee.khyonieheart.crafthyacinth.command.HyacinthCommandManager;
import coffee.khyonieheart.hyacinth.command.BukkitCommandMeta;
import coffee.khyonieheart.hyacinth.command.HyacinthCommand;
import coffee.khyonieheart.hyacinth.util.marker.NotEmpty;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Various tools to register commands and listeners. 
 * All methods here bypass command managers and the listener manager.
 */
public class Registration
{
	private static CommandMap commandMap;

	/**
	 * Registers a Hyacinth command to the current server.
	 *
	 * @param command Hyacinth command to register
	 * @param owner Java plugin that owns the command
	 *
	 * @deprecated This bypasses the Command manager.
	 */
	@Deprecated(forRemoval = true)
	public static void registerHyacinthCommand(
		@NotNull HyacinthCommand command,
		@NotNull JavaPlugin owner
	) {
		if (Hyacinth.getConfig("enableModules", Boolean.class))
		{
			throw new IllegalStateException("Library-mode method cannot be used in module mode. Please see Hyacinth#getCommandManager.");
		}

		if (commandMap == null)
		{
			quickCacheCommandmap();
		}

		commandMap.register(command.getLabel(), command);
	}

	private static void quickCacheCommandmap()
	{
		Logger.verbose("Attempting to quick-cache Bukkit command map...");
		try {
			cacheCommandMap(Hyacinth.getInstance().getServer());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Attempts to register a CommandExecutor as a PluginCommand to the server, with the provided plugin as its owner. Executors must be annotated with {@link BukkitCommandMeta} to be registered.
	 * @param executor Command executor to be run 
	 * @param owner Owning plugin instance
	 * @param name Command label, as in /name
	 */
	public static void registerCommandExecutor(
		@NotNull CommandExecutor executor, 
		@NotNull JavaPlugin owner, 
		@NotNull String name
	) {
		if (commandMap == null)
		{
			quickCacheCommandmap();
		}

		if (!executor.getClass().isAnnotationPresent(BukkitCommandMeta.class))
		{
			throw new IllegalArgumentException("Command executors registered with Registration#registerCommandExecutor must be annotated with @BukkitCommandMeta.");
		}

		// More reflection, this is really unnecessary MD_5
		PluginCommand command;
		try {
			Constructor<PluginCommand> pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			pluginCommandConstructor.setAccessible(true);
			command = pluginCommandConstructor.newInstance(name, owner);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}

		// Set meta
		BukkitCommandMeta meta = executor.getClass().getAnnotation(BukkitCommandMeta.class);
		command.setExecutor(executor);	
		command.setDescription(meta.description());
		command.setUsage(meta.usage());	
		command.setPermission(meta.permission());
		command.setPermissionMessage(meta.permissionFailedMessage());

		if (meta.aliases().length != 0)
		{
			command.setAliases(List.of(meta.aliases()));
		}

		commandMap.register(name, command);
		Logger.verbose("Registered Bukkit /" + name + " command without incident.");
	}

	/**
	 * Registers a single listener to the server.
	 *
	 * @param listener Listener
	 * @param owner Plugin that owns this listener
	 */
	public static void registerListener(
		@NotNull Listener listener, 
		@NotNull JavaPlugin owner
	) {
		Hyacinth.getInstance().getServer().getPluginManager().registerEvents(listener, owner);
	}

	/**
	 * Registers multiple listeners to the server.
	 *
	 * @param owner Plugin that owns all given listeners
	 * @param listeners Listeners to register
	 */
	public static void registerListeners(
		@NotNull JavaPlugin owner,
		@NotEmpty Listener... listeners
	) {
		for (Listener l : listeners)
		{
			registerListener(l, owner);
		}
	}

	/**
	 * @implNote This method is inherently much less safe than {@link HyacinthCommandManager}
	 * @return Command map from server
	 */
	private static CommandMap cacheCommandMap(
		@NotNull Server server
	) 
		throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException 
	{
		if (commandMap != null)
			return commandMap;

		Field commandMapField = server.getClass().getDeclaredField("commandMap");
		commandMapField.setAccessible(true);
		commandMap = (CommandMap) commandMapField.get(server);

		return commandMap;
	}
}
