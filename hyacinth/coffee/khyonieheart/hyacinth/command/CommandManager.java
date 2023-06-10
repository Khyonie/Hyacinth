package coffee.khyonieheart.hyacinth.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import coffee.khyonieheart.crafthyacinth.command.HyacinthCommandManager;
import coffee.khyonieheart.crafthyacinth.module.provider.HyacinthProviderPrimer;
import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.exception.HyacinthModuleException;
import coffee.khyonieheart.hyacinth.exception.InstantiationRuntimeException;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

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
    public void register(
        @Nullable String name, 
        @NotNull HyacinthCommand command, 
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
     * Obtains a Hyacinth command that has been registered to the server.
     * @param name Name or alias of Hyacinth command.
     * @return a Hyacinth command with the given name or alias.
     */
    public HyacinthCommand getHyacinthCommand(
        @NotNull String name
    );

	public static CommandManager sourceCommandManager(
		@NotNull String sourcePath
	)
		throws FileNotFoundException
	{
		Logger.verbose("Sourcing command manager from " + sourcePath);
		String[] splitString = sourcePath.split("/");

		if (splitString.length != 2)
		{
			Logger.log("§cInvalid command manager source: \"" + sourcePath + "\"");
			return null;
		}

		String source = splitString[0].replace("\\", "");
		String classPath = splitString[1];

		Class<?> providerClass;
		CommandManager manager;

		if (source.equals("internal"))
		{
			try {
				Class<?> potentialProviderClass = Class.forName(classPath, false, Hyacinth.getClassloader());

				if (potentialProviderClass == null)
				{
					throw new NullPointerException();
				}

				providerClass = potentialProviderClass.asSubclass(CommandManager.class);
			} catch (ClassNotFoundException | NullPointerException e) {
				Logger.log("§cFailed to find internal command manager \"" + classPath + "\", falling back to Hyacinth command manager");
				providerClass = HyacinthCommandManager.class;
			} catch (ClassCastException e) {
				Logger.log("§cPotential command manager provider \"" + classPath + "\" is not a valid CommandManager, falling back to Hyacinth command manager");
				providerClass = HyacinthCommandManager.class;

				e.printStackTrace();
			}

			try {
				manager = (CommandManager) Reflect.simpleInstantiate(providerClass);
				return manager;
			} catch (InstantiationRuntimeException e) {
				Logger.log("§cFailed to instantiate command manager class \"" + providerClass.getName() + "\"");
				e.printStackTrace();
			}

			return new HyacinthCommandManager();
		}

		File providerSource = providerFileWithName(source);
		if (!providerSource.exists())
		{
			throw new FileNotFoundException("Proposed command manager file \"" + source + ".jar\" does not exist");
		}

		HyacinthProviderPrimer primer = new HyacinthProviderPrimer();

		try {
			primer.loadModule(providerSource);
			
			providerClass = primer.getGlobalClass(classPath, Hyacinth.getClassloader());

			if (providerClass == null)
			{
				throw new HyacinthModuleException("Proposed external command manager class \"" + classPath + "\" does not exist");
			}
		} catch (IllegalArgumentException | IOException | HyacinthModuleException e) {
			Logger.log("§cFailed to locate command manager provider class \"" + classPath + "\", falling back to Hyacinth command manager");
			e.printStackTrace();

			return new HyacinthCommandManager();
		}

		try {
			manager = (CommandManager) Reflect.simpleInstantiate(providerClass);

			Logger.verbose("Instantiated new command mananger " + manager.getClass().getName());

			return manager;
		} catch (InstantiationRuntimeException e) {
			Logger.log("§cFailed to instantiate external command manager provider class \"" + providerClass.getName() + "\", falling back to Hyacinth command manager");
			e.printStackTrace();

			return new HyacinthCommandManager();
		}
	}

	public static File providerFileWithName(String name)
	{
		return new File ("./Hyacinth/providers/commands/" + name + ".jar");
	}
}
