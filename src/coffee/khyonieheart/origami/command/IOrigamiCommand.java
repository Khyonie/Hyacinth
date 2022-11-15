package coffee.khyonieheart.origami.command;

import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.util.marker.NotNull;

interface IOrigamiCommand 
{
    /**
     * Obtains the module this command is owned by.
     * @return Owning module
     */
    @NotNull
    public OrigamiModule getModule();
}
