package coffee.khyonieheart.hyacinth;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import coffee.khyonieheart.crafthyacinth.command.HyacinthTabCompleter;
import coffee.khyonieheart.crafthyacinth.command.parser.HyacinthCompletionBranch;
import coffee.khyonieheart.crafthyacinth.command.parser.validator.PermissionValidator;
import coffee.khyonieheart.crafthyacinth.killswitch.KillswitchManager;
import coffee.khyonieheart.crafthyacinth.module.HyacinthCoreModule;
import coffee.khyonieheart.hyacinth.command.HyacinthCommand;
import coffee.khyonieheart.hyacinth.command.NoSubCommandExecutor;
import coffee.khyonieheart.hyacinth.command.parser.CompletionRoot;
import coffee.khyonieheart.hyacinth.killswitch.KillswitchIdentifier;
import coffee.khyonieheart.hyacinth.killswitch.KillswitchTarget;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.option.Option;
import coffee.khyonieheart.hyacinth.print.Grammar;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Internal Hyacinth command, tied to hyacinth core module.
 *
 * @since 1.0.0
 * @author Khyonie
 */
public class HyacinthCoreCommand extends HyacinthCommand
{
	/**
	 * Constructor
	 */
	public HyacinthCoreCommand() 
	{
		super("hyacinth", "/hyacinth", "hyacinth.cmd", "h", "hya");

		this.setParser(new HyacinthTabCompleter(this));	

		CompletionRoot root = this.getTrees();
		root.addRoots((name) -> { return new HyacinthCompletionBranch(); }, "features");
		root.getTree("features").addValidator(new PermissionValidator("hyacinth.killswitch", false));

		root.getTree("features").add("<AVAILABLE_KILLSWITCHES>").add("enable");
		root.getTree("features").get("<AVAILABLE_KILLSWITCHES>").add("disable");
	}

	public void features(CommandSender sender, String[] args)
	{	
		// Find appropriate target
		String[] targetName = args[1].split(":");
		
		if (targetName.length != 2)
		{
			Message.send(sender, "§cInvalid feature target \"" + args[1] + "\".");
			return;
		}

		Option moduleOption = Hyacinth.getModuleManager().getModule(targetName[0]);

		if (moduleOption.isNone())
		{
			Message.send(sender, "§cUnknown module \"" + targetName[0] + "\".");
			return;
		}

		HyacinthModule module = moduleOption.unwrap(HyacinthModule.class);
		List<KillswitchTarget> targets = KillswitchManager.get(module.getClass());

		if (targets == null)
		{
			Message.send(sender, "§cModule does not contain any toggle-able features.");
			return;
		}

		KillswitchTarget target = null;
		for (KillswitchTarget t : targets)
		{
			if (!t.getClass().isAnnotationPresent(KillswitchIdentifier.class))
			{
				continue;
			}

			for (String id : t.getClass().getAnnotation(KillswitchIdentifier.class).value())
			{
				if (targetName[1].equals(id))
				{
					target = t;
					break;
				}
			}
		}

		if (target == null)
		{
			Message.send(sender, "§cNo such feature \"" + targetName[1] + "\" exists for module \"" + targetName[0] + "\".");
			return;
		}

		// Run effect
		switch (args[2].toLowerCase())
		{
			case "enable" -> {
				try {
					if (target.reenable(targetName[1]))
					{
						Message.send(sender, "§aSuccessfully re-enabled feature \"" + targetName[1] + "\".");
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message.send(sender, "§cFailed to re-enable feature \"" + targetName[1] + "\". See console for details.");
			}
			case "disable" -> {
				try {
					if (target.kill(targetName[1]))
					{
						Message.send(sender, "§aSuccessfully killed feature \"" + targetName[1] + "\".");
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message.send(sender, "§cFailed to kill feature \"" + targetName[1] + "\". See console for details.");
			}
		}
	}

	/**
	 * No subcommand executor.
	 *
	 * @param sender Command sender
	 * @param args Command args
	 */
	@NoSubCommandExecutor
	public void noSubCommand(CommandSender sender, String[] args)
	{
		Message.send(sender, "§d❀ §bHyacinth");
		Message.send(sender, "§d❀ §9Because plugin development should make sense.");
		if (sender instanceof Player player)
		{
			TextComponent link = new TextComponent("§d❀ §9Wiki: https://github.com/Khyonie/Hyacinth/wiki");
			link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Khyonie/Hyacinth/wiki"));
			player.spigot().sendMessage(new ComponentBuilder().append(link).create());
		}
		Message.send(sender, "§d❀ §9(§a" + Hyacinth.getModuleManager().getLoadedModules().size() + "§9) " + Grammar.plural(Hyacinth.getModuleManager().getLoadedModules().size(), "module ", "modules ") + "loaded:");
		for (HyacinthModule m : Hyacinth.getModuleManager().getLoadedModules())
		{
			Message.send(sender, "§9 - " + Hyacinth.getModuleManager().getConfiguration(m).getString("name") + " " + Hyacinth.getModuleManager().getConfiguration(m).getString("version"));
		}
	}

	@Override
	public HyacinthModule getModule() 
	{
		return HyacinthCoreModule.getInstance();
	}
}
