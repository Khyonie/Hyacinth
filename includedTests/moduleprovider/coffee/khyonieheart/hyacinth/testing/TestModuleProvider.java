package coffee.khyonieheart.hyacinth.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.crafthyacinth.module.HyacinthModuleManager;
import coffee.khyonieheart.hyacinth.exception.HyacinthModuleException;
import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.ModuleManager;
import coffee.khyonieheart.hyacinth.option.Option;

/**
 * Test module manager provider to be loaded at runtime. Internally contains an {@link HyacinthModuleManager} which handles all calls.
 */
public class TestModuleProvider implements ModuleManager, UnitTestable
{
    private ModuleManager wrapped = new HyacinthModuleManager();

    @Override
    public HyacinthModule loadModule(
        File moduleFile
    )
        throws IllegalArgumentException, FileNotFoundException, IOException, HyacinthModuleException 
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
	
	public void addModule(HyacinthModule module, YamlConfiguration configuration)
	{
		wrapped.addModule(module, configuration);
	}

    @Override
    @TestIdentifier("Hyacinth provider tests")
    public List<UnitTestResult> test() 
    {
        List<UnitTestResult> results = new ArrayList<>();

        results.add(new UnitTestResult(true, "Provider class instantiated", null, this));

        return results;
    }
}
