package coffee.khyonieheart.hyacinth.util.marker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation used to declare either that a parameter cannot legally be <code>null</code>, or
 * a method should never return <code>null</code>. Returning null out of a method annotated with this annotation
 * is considered to be an anti-pattern.
 * 
 * @author Khyonie
 * @since 1.0.0
 * @see {@link Nullable}, {@link NotEmpty}, {@link NoExceptions}
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface NotNull {}
