package coffee.khyonieheart.crafthyacinth.module;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import coffee.khyonieheart.hyacinth.Hyacinth;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class HyacinthModuleClassloader extends URLClassLoader
{
    private Map<String, Class<?>> interiorClasses = new HashMap<>();

    private JarFile jar;

    public HyacinthModuleClassloader(
		@NotNull File hostFile, 
		@NotNull JarFile jar, 
		@NotNull ClassLoader parent
	) 
		throws MalformedURLException 
    {
        super(hostFile.getName(), new URL[] { hostFile.toURI().toURL() }, parent);

		Objects.requireNonNull(jar);
		Objects.requireNonNull(parent);

        this.jar = jar;
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
        Class<?> result = interiorClasses.get(name);

        if (result != null)
        {
            return result;
        }

        if (checkGlobal)
        {
            result = Hyacinth.getModuleManager().getGlobalClass(name, this);

            if (result != null)
                return result;
        }

        result = super.findClass(name);

        if (result != null)
            interiorClasses.put(name, result);

        return result;
    }

	@NotNull
    @SuppressWarnings("unchecked")
    public <T> List<Class<? extends T>> collectSubclasses(
		@NotNull Class<T> clazz
	) {
		Objects.requireNonNull(clazz);

        List<Class<? extends T>> collectedClasses = new ArrayList<>();

        Enumeration<JarEntry> entries = jar.entries();
        JarEntry entry;
        
        while (entries.hasMoreElements())
        {
            entry = entries.nextElement();

            if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                continue;

            String className = entry.getName().replace("/", ".").replace(".class", "");

            try {
                Class<?> buffer = Class.forName(className, false, this);

                if (clazz.isAssignableFrom(buffer))
                    collectedClasses.add((Class<? extends T>) buffer);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }

        return collectedClasses;
    } 

	@NotNull
    public Map<Class<?>, List<Class<?>>> collectMultiSubclasses(
		Class<?>... classes
	) {
        Map<Class<?>, List<Class<?>>> collected = new HashMap<>();

        for (Class<?> clazz : classes)
            collected.put(clazz, new ArrayList<>());

        Enumeration<JarEntry> entries = jar.entries();
        JarEntry entry;

        while (entries.hasMoreElements())
        {
            entry = entries.nextElement();

            if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                continue;

            String className = entry.getName().replace("/", ".").replace(".class", "");

            try {
                Class<?> buffer = Class.forName(className, false, this);

                for (Class<?> clazz : classes)
                {
                    if (!clazz.isAssignableFrom(buffer))
                        continue;
                    
                    collected.get(clazz).add(buffer);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }

        return collected;
    }

	@NotNull
    public static HyacinthModuleClassloader create(
		@NotNull File file, 
		@NotNull JarFile jar
	) 
		throws MalformedURLException
    {
		Objects.requireNonNull(file);
		Objects.requireNonNull(jar);

        return new HyacinthModuleClassloader(file, jar, Hyacinth.getClassloader());
    }
}
