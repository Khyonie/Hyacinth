package coffee.khyonieheart.origami.testing;

import java.util.List;

import coffee.khyonieheart.origami.util.marker.NoExceptions;

/**
 * Allows a class instance to be unit-tested.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public interface UnitTestable 
{
    /**
     * Performs a unit test. This method should not legally throw exceptions.
     * @return Whether or not the test was passed.
     */
    @NoExceptions
    public List<UnitTestResult> test();
}