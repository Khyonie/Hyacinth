package coffee.khyonieheart.hyacinth.exception;

public class NumberOutOfRangeException extends RuntimeException
{
	private final Object number;

	public NumberOutOfRangeException(
		Object number
	) {
		this.number = number;
	}

	public NumberOutOfRangeException(
		Object number,
		String message
	) {
		super(message);
		this.number = number;
	}

	public Object getNumber()
	{
		return this.number;
	}
}
