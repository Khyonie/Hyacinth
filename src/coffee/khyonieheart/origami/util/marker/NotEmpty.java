package coffee.khyonieheart.origami.util.marker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marker annotation used to declare that a variable-length argument cannot legally contain 0 entries.<p>
 * 
 * E.g Given the method "foo" with a varargs String parameter:
 * <pre><code>
 *void foo(@NotEmpty String... varargs) 
 *{ 
 *  // - Snip - 
 *}
 *
 * // Legal
 *foo("bar");
 *foo("bol", "baz");
 *
 * // Illegal
 *foo();
 *</code></pre>
 *
 * This is a programmer declaration that the code contained inside a method does not check the length of<p>
 * a varargs parameter, and care should be exercised in passing zero-length arrays to this method.
 * 
 * @author Khyonie
 * @since 1.0.0
 * 
 * @see {@link NotNull}, {@link Nullable}, {@link NoExceptions}
 */
@Target(ElementType.PARAMETER)
public @interface NotEmpty {}