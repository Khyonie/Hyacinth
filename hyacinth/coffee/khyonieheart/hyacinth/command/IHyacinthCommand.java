package coffee.khyonieheart.hyacinth.command;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

interface IHyacinthCommand 
{
    /**
     * Obtains the module this command is owned by.
     * @return Owning module
     */
    @NotNull
    public HyacinthModule getModule();
}
