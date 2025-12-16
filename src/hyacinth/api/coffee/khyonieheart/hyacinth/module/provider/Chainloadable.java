package coffee.khyonieheart.hyacinth.module.provider;

import java.util.Map;

import coffee.khyonieheart.hyacinth.module.ModuleManager;

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
