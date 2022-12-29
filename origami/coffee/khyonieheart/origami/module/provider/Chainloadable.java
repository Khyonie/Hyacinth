package coffee.khyonieheart.origami.module.provider;

import coffee.khyonieheart.origami.module.ModuleManager;

import java.util.Map;

/**
 * Extension to {@link ModuleManager}s that allows transfer of loaded classes.
 */
public interface Chainloadable 
{
    /**
     * Attempts to transfer loaded data obtained from a primer module manager to a new module manager.
     * @param loadedClasses All loaded classes.
     */
    public void transfer(Map<String, Class<?>> loadedClasses);
}
