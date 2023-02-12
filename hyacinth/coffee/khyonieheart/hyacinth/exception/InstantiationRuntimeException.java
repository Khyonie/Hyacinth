package coffee.khyonieheart.hyacinth.exception;

public class InstantiationRuntimeException extends RuntimeException
{
    public InstantiationRuntimeException(String message)
    {
        super(message);
    }

    public InstantiationRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
