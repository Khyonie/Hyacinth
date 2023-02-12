package coffee.khyonieheart.hyacinth.exception;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;

public class InvalidModuleClassException extends HyacinthModuleException
{
    public InvalidModuleClassException(String message)
    {
        this(null, message);
    }

    public InvalidModuleClassException(HyacinthModule module, String message)
    {
        super(module, message);
    }
}
