package coffee.khyonieheart.origami.command;

import coffee.khyonieheart.origami.module.OrigamiModule;

public interface OrigamiCommand 
{
    /**
     * Obtains the module this command is owned by.
     * @return Owning module
     */
    public OrigamiModule getModule();
}
