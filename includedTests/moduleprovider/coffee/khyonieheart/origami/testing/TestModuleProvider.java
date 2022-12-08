package coffee.khyonieheart.origami.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import coffee.khyonieheart.craftorigami.module.OrigamiModuleManager;
import coffee.khyonieheart.origami.exception.OrigamiModuleException;
import coffee.khyonieheart.origami.module.ModuleManager;
import coffee.khyonieheart.origami.module.OrigamiModule;
import coffee.khyonieheart.origami.option.Option;

/**
 * Test module manager provider to be loaded at runtime. Internally contains an {@link OrigamiModuleManager} which handles all calls.
 */
public class TestModuleProvider implements ModuleManager, UnitTestable
{
    private ModuleManager wrapped = new OrigamiModuleManager();

    @Override
    public OrigamiModule loadModule(
        File moduleFile
    )
        throws IllegalArgumentException, FileNotFoundException, IOException, OrigamiModuleException 
    {
        return wrapped.loadModule(moduleFile);
    }

    @Override
    public Option getModule(String moduleName) 
    {
        return wrapped.getModule(moduleName);
    }

    @Override
    public Class<?> getGlobalClass(String name, ClassLoader accessor) 
    {
        return wrapped.getGlobalClass(name, accessor);
    }

    @Override
    @TestIdentifier("Origami provider tests")
    public List<UnitTestResult> test() 
    {
        List<UnitTestResult> results = new ArrayList<>();

        results.add(new UnitTestResult(true, "Provider class instantiated", null, this));

        return results;
    }
}
