package coffee.khyonieheart.origami.util;

import java.util.Iterator;

/**
 * An iterator that allows enumeration over an array. This implementation is read-only and does not support addition or removal of elements.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class ArrayIterator<T> implements Iterator<T>
{
    private T[] data;
    private int index;

    ArrayIterator(T[] data, int startingIndex)
    {
        if (startingIndex < 0)
        {
            throw new IllegalArgumentException("Starting index must be positive");
        }

        this.data = data;
        this.index = startingIndex;
    }

    public int getIndex()
    {
        return this.index;
    }

    @Override
    public boolean hasNext() 
    {
        return (index + 1) < data.length;
    }

    @Override
    public T next() 
    {
        return data[index++];
    }

    public boolean hasPrevious()
    {
        return index > 0;
    }

    public T previous()
    {
        return data[--index];
    }
    
    public static <T> ArrayIterator<T> create(T[] data, int startingIndex)
    {
        return new ArrayIterator<T>(data, startingIndex);
    }
}
