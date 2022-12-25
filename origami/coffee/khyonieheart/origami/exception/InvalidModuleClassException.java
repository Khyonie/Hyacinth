package coffee.khyonieheart.origami.exception;

import coffee.khyonieheart.origami.module.OrigamiModule;

public class InvalidModuleClassException extends OrigamiModuleException
{
    public InvalidModuleClassException(String message)
    {
        this(null, message);
    }

    public InvalidModuleClassException(OrigamiModule module, String message)
    {
        super(module, message);
    }
}
