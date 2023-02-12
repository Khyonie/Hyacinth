package coffee.khyonieheart.hyacinth.exception;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

/**
 * Parent class for all Hyacinth module-loading exceptions.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class HyacinthModuleException extends Exception
{
    private final HyacinthModule associatedModule;

    public HyacinthModuleException(
        @Nullable HyacinthModule associatedModule, 
        @Nullable String message, 
        @Nullable Throwable cause
    ) {
        super(message, cause);
        this.associatedModule = associatedModule;
    }

    public HyacinthModuleException()
    {
        this.associatedModule = null;
    }

    public HyacinthModuleException(
        @Nullable String message
    ) {
        super(message);
        this.associatedModule = null;
    }

    public HyacinthModuleException(
        @Nullable Throwable cause
    ) {
        super(cause);
        this.associatedModule = null;
    }

    public HyacinthModuleException(
        @Nullable String message,
        @Nullable Throwable cause
    ) {
        super(message, cause);
        this.associatedModule = null;
    }

    public HyacinthModuleException(
        @Nullable HyacinthModule associatedModule,
        @Nullable String message
    ) {
        super(message);
        this.associatedModule = associatedModule;
    }

    public HyacinthModuleException(
        @Nullable HyacinthModule associatedModule,
        @Nullable Throwable cause
    ) {
        super(cause);
        this.associatedModule = associatedModule;
    }

    /**
     * Obtains the module implicated in this exception.
     * @return A hyacinth module. May be null.
     */
    @Nullable
    public HyacinthModule getModule()
    {
        return this.associatedModule;
    }

    /**
     * @return Whether or not a module was provided to this exception.
     */
    public boolean isModulePresent()
    {
        return associatedModule != null;
    }
}
