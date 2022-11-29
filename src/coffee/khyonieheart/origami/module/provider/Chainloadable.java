package coffee.khyonieheart.origami.module.provider;

import java.util.Map;

public interface Chainloadable 
{
    /**
     * Attempts to transfer loaded data obtained from a primer module manager to a new module manager.
     * @param loadedClasses
     */
    public void transfer(Map<String, Class<?>> loadedClasses);
}
