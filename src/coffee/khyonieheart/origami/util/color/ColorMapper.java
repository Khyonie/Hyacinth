package coffee.khyonieheart.origami.util.color;

public class ColorMapper 
{
    /**
     * Converts a minecraft color code into an ANSI color escape code.
     * @param colorCode
     * @return
     */
    public static String toUnixColor(String colorCode)
    {
        if (colorCode.startsWith("§"))
        {
            colorCode = colorCode.substring(1);
        }
        
        return switch (colorCode)
        {
            // Bright
            case "7" -> "\u001B[0"; // Reset
            case "9" -> "\u001B[34m;1m";
            case "a" -> "\u001B[32m;1m";
            case "b" -> "\u001B[36m;1m";
            case "c" -> "\u001B[31m;1m";
            case "d" -> "\u001B[35m;1m";
            case "e" -> "\u001B[33m;1m";
            case "f" -> "\u001B[37m;1m";

            // Dark
            case "0" -> "\u001B[30m";
            case "1" -> "\u001B[34m";
            case "2" -> "\u001B[32m";
            case "3" -> "\u001B[36m";
            case "4" -> "\u001B[31m";
            case "5" -> "\u001B[35m";
            case "6" -> "\u001B[33m";
            case "8" -> "\u001B[30m;1m";

            default -> "";
        };
    }  

    public static String to256Color(int id)
    {
        return "\u001B[38;5;" + id + "m";
    }
}
