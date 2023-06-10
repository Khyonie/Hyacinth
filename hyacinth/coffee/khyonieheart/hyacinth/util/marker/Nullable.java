package coffee.khyonieheart.hyacinth.util.marker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import coffee.khyonieheart.hyacinth.option.Option;

/**
 * Marker annotation used to declare that a method may return <code>null</code>, or a parameter can legally be <code>null</code>.
 * In the case of methods, consider using an {@link Option} to encourage safety rather than returning a potential 
 * <code>null</code>.<p>
 * 
 * Retuning <code>null</code> on methods that do not have this annotation is considered to be an anti-pattern.
 * 
 * @author Khyonie
 * @since 1.0.0
 * 
 * @see {@link NotNull}, {@link NotEmpty}, {@link NoExceptions}, {@link Option}
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Nullable {}
