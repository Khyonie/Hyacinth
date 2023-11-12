package coffee.khyonieheart.hyacinth.module.nouveau;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class NouveauClassloader extends URLClassLoader
{
	private String identifier;
	private String packageString;
	private ModuleFile module;

	private Class<?>[] enumerationCache;

	public NouveauClassloader(
		@NotNull ModuleFile module,
		@NotNull ClassLoader parent
	)
		throws MalformedURLException
	{
		super(module.getConfiguration().getString("name"), new URL[] { module.getFile().toURI().toURL() }, parent);

		this.identifier = module.getConfiguration().getString("name");
		this.packageString = module.getConfiguration().getString("package");
		this.module = module;

		this.module.attach(this);
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public String getPackage()
	{
		return this.packageString;
	}

	public Class<?>[] enumerate()
	{
		if (this.enumerationCache != null)
		{
			return enumerationCache;
		}

		List<Class<?>> classes = new ArrayList<>();

		JarEntry entry;
		Enumeration<JarEntry> entries = this.module.getJar().entries();
		while (entries.hasMoreElements())
		{
			entry = entries.nextElement();
			if (entry.isDirectory())
			{
				continue;
			}

			if (!entry.getName().endsWith(".class"))
			{
				continue;
			}

			// Check module-local cache
			String name = entry.getName().replace("/", ".").replace(".class", "");
			Class<?> clazz;
			if ((clazz = this.module.getOwnedClass(name)) != null)
			{
				classes.add(clazz);
				continue;
			}

			// Otherwise load it from jar
			try {
				clazz = Class.forName(name, false, this);
				classes.add(clazz);

				this.module.addOwnedClass(clazz.getName(), clazz);
			} catch (ClassNotFoundException e) {
				Logger.log("Failed to load class " + entry.getName().replace("/", ".") + " in module " + this.identifier);
				e.printStackTrace();
				continue;
			}
		}

		this.enumerationCache = classes.toArray(new Class<?>[classes.size()]);
		return enumerationCache;
	}

	@Override
	protected Class<?> findClass(
		String name
	) 
		throws ClassNotFoundException
	{
		return findClass(name, true);
	}

	@NotNull
	public Class<?> findClass(
		@NotNull String name,
		boolean checkGlobal
	)
		throws ClassNotFoundException
	{
		Class<?> result = this.module.getOwnedClass(name);

		if (result != null)
		{
			return result;
		}

		if (checkGlobal)
		{
			result = ClassCoordinator.getGlobalClass(name, this.module);

			if (result != null)
			{
				return result;
			}
		}

		result = super.findClass(name);

		if (result != null)
		{
			module.addOwnedClass(name, result);
		}

		return result;
	}

	public ModuleFile getModuleFile()
	{
		return this.module;
	}
}
