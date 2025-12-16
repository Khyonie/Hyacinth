package coffee.khyonieheart.hyacinth.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;

/**
 * Required annotation for command executors to be registered with Registration utility.
 *
 * @since 1.0.0
 * @author Khyonie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BukkitCommandMeta
{
	/** Command description */
	@NotNull String description();
	/** Command permission */
 	@Nullable String permission();
	/** Command permission failed message */
	@NotNull String permissionFailedMessage() default "§cYou do not have permission to use this command.";
	/** Command usage example */
	@NotNull String usage();
	/** Command aliases */
	@NotNull String[] aliases();
}
