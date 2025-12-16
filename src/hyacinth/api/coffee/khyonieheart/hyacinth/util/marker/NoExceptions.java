package coffee.khyonieheart.hyacinth.util.marker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation used to declare that a method should not legally throw exceptions or be the cause of any exceptions.
 * 
 * Throwing exceptions from a method with this annonation or this method being the cause of a thrown exception is considered
 * to be an anti-practice.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface NoExceptions {}
