package coffee.khyonieheart.origami.util.marker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import coffee.khyonieheart.origami.option.Option;

/**
 * Marker annotation used to declare that a method may return <code>null</code>, or a parameter can legally be <code>null</code>.
 * In the case of methods, consider using an {@link Option} to encourage safety rather than returning a potential 
 * <code>null</code>.<p>
 * 
 * Retuning <code>null</code> on methods that do not have this annotation is considered to be bad
 * practice.
 * 
 * @author Khyonie
 * @since 1.0.0
 * 
 * @see {@link NotNull}, {@link NotEmpty}, {@link Option}
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface Nullable {}
