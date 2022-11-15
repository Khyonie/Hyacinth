package coffee.khyonieheart.origami.util.marker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marker annotation used to declare either that a parameter cannot legally be <code>null</code>, or
 * a method should never return <code>null</code>. Returning null out of a method annotated with this annotation
 * is considered to be bad practice.
 * 
 * @author Khyonie
 * @since 1.0.0
 * @see {@link Nullable}, {@link NotEmpty}
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface NotNull {}