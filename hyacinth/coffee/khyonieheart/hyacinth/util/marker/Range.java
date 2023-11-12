package coffee.khyonieheart.hyacinth.util.marker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marker annotation used to denote a minimum/maximum range for a long/int parameter.
 *
 * @since 1.0.0
 * @author Khyonie
 */
@Documented
@Target(ElementType.PARAMETER)
public @interface Range
{
	int minimum();
	int maximum();
}
