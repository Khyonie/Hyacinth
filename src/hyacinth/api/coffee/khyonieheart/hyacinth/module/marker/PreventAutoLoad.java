package coffee.khyonieheart.hyacinth.module.marker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker annotation to denote that a command or listener class should not be automatically registered.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventAutoLoad {}
