package coffee.khyonieheart.origami.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation that allows adding a prefix to all subcommands contained in this class.
 * 
 *<pre><code>
class NormalPrefixedCommand extends OrigamiCommand {
    // - Snip constructors and required methods -  
    public void example(CommandSender sender, String[] args)
    {
        // - Snip -
    }
}
 *</code> 
 *<code>
&#64;SubcommandPrefix("subcommand_")
class CustomPrefixedCommand extends OrigamiCommand {
    // - Snip constructors and required methods
    public void subcommand_example(CommandSender sender, String[] args)
    {
        // - Snip -
    }
}
 *</code></pre>
 *
 * @author Khyonie
 * @since 1.0.0
 */
@Target({ ElementType.TYPE })
public @interface SubcommandPrefix 
{
    public String value(); 
}
