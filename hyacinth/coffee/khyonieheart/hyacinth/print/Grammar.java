package coffee.khyonieheart.hyacinth.print;

public class Grammar 
{
    public static String plural(int count, String single, String plural)
    {
        return count == 1 ? single : plural; 
    }
}
