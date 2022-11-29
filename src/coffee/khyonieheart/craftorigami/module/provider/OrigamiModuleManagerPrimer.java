package coffee.khyonieheart.craftorigami.module.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import coffee.khyonieheart.origami.exception.OrigamiModuleException;
import coffee.khyonieheart.origami.module.ModuleManager;
import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.module.provider.Chainloadable;
import coffee.khyonieheart.origami.option.Option;

public class OrigamiModuleManagerPrimer implements ModuleManager, Chainloadable
{
    @Override
    public OrigamiModule loadModule(File moduleFile) throws IllegalArgumentException, FileNotFoundException, IOException, OrigamiModuleException 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Option getModule(String moduleName) 
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<?> getGlobalClass(String name, ClassLoader accessor) 
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void transfer(Map<String, Class<?>> loadedClasses, ClassLoader parentLoader) 
    {
        
    }
}
