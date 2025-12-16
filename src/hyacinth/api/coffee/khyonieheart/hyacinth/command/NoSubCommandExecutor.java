package coffee.khyonieheart.hyacinth.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker that declares what command will be executed when no explicit subcommand is given.
 *
 * @author Khyonie 
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoSubCommandExecutor {}
