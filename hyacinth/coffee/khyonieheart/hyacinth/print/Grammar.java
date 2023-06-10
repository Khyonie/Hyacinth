package coffee.khyonieheart.hyacinth.print;

/**
 * Declarative methods to create grammatically correct messages.
 */
public class Grammar 
{
    public static String plural(int count, String single, String plural)
    {
        return count == 1 ? single : plural; 
    }

	public static String indicative(int count)
	{
		return count == 1 ? "was" : "were";
	}
}
