package coffee.khyonieheart.origami.exception;

import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.util.marker.Nullable;

/**
 * Parent class for all Origami module-loading exceptions.
 * 
 * @author Khyonie
 * @since 1.0.0
 */
public class OrigamiModuleException extends Exception
{
    private final OrigamiModule associatedModule;

    public OrigamiModuleException(
        @Nullable OrigamiModule associatedModule, 
        @Nullable String message, 
        @Nullable Throwable cause
    ) {
        super(message, cause);
        this.associatedModule = associatedModule;
    }

    public OrigamiModuleException()
    {
        this.associatedModule = null;
    }

    public OrigamiModuleException(
        @Nullable String message
    ) {
        super(message);
        this.associatedModule = null;
    }

    public OrigamiModuleException(
        @Nullable Throwable cause
    ) {
        super(cause);
        this.associatedModule = null;
    }

    public OrigamiModuleException(
        @Nullable String message,
        @Nullable Throwable cause
    ) {
        super(message, cause);
        this.associatedModule = null;
    }

    public OrigamiModuleException(
        @Nullable OrigamiModule associatedModule,
        @Nullable String message
    ) {
        super(message);
        this.associatedModule = associatedModule;
    }

    public OrigamiModuleException(
        @Nullable OrigamiModule associatedModule,
        @Nullable Throwable cause
    ) {
        super(cause);
        this.associatedModule = associatedModule;
    }

    /**
     * Obtains the module implicated in this exception.
     * @return An origami module. May be null.
     */
    @Nullable
    public OrigamiModule getModule()
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
