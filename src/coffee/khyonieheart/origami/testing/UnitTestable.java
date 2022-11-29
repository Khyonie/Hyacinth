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
     * @return A list of unit test results
     */
    @NoExceptions
    public List<UnitTestResult> test();
}