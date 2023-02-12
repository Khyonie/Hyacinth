package coffee.khyonieheart.hyacinth.util;

import java.util.Iterator;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * An iterator that allows enumeration over an array. This implementation is read-only and does not support addition or removal of elements.
 *
 * @param <T> Type of array
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class ArrayIterator<T> implements Iterator<T>
{
    private T[] data;
    private int index;

	/**
	 * Constructor that takes in an array and a starting index.
	 *
	 * @param data Array to iterate over
	 * @param startingIndex Index to start on
	 *
	 * @since 1.0.0
	 */
    protected ArrayIterator(
		@NotNull T[] data, 
		int startingIndex
	) {
        if (startingIndex < 0)
        {
            throw new IllegalArgumentException("Starting index must be positive");
        }

        this.data = data;
        this.index = startingIndex;
    }

	/**
	 * Obtains the current index on this iterator.
	 *
	 * @return The current index
	 *
	 * @since 1.0.0
	 */
    public int getIndex()
    {
        return this.index;
    }

	/**
	 * Returns true if this iterator has not reached the end of the array.
	 *
	 * @return True if this iterator has not reached the end of the array.
	 *
	 * @since 1.0.0
	 */
    @Override
    public boolean hasNext() 
    {
        return (index + 1) < data.length;
    }

	/**
	 * Returns the element at the current position in the array then increments the index.
	 *
	 * @return The next element in the array.
	 *
	 * @since 1.0.0
	 * @throws ArrayIndexOutOfBoundsException If the index is greater than the number of elements contained.
	 */
    @Override
    public T next() 
    {
        return data[index++];
    }

	/**
	 * Returns true if this iterator has more elements in the array preceding the current index.
	 *
	 * @return True if this iterator has more elements in the array preceding the current index.
	 *
	 * @since 1.0.0
	 * @implNote This is equivalent to (#getIndex() > 0)
	 */
    public boolean hasPrevious()
    {
        return index > 0;
    }

	/**
	 * Decrements the index and returns the element at that position in the array.
	 *
	 * @return The previous element in the array.
	 *
	 * @since 1.0.0
	 * @throws ArrayIndexOutOfBoundsException If the index is less than 0.
	 */
    public T previous()
    {
        return data[--index];
    }
}
