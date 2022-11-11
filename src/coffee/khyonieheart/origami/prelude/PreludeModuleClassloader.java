package coffee.khyonieheart.origami.prelude;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PreludeModuleClassloader extends URLClassLoader 
{
    private static Map<String, Class<?>> LOADED_CLASSES = new HashMap<>();
    private static Map<String, PreludeModuleClassloader> ACTIVE_CLASSLOADERS = new HashMap<>();

    private Map<String, Class<?>> interiorClasses = new HashMap<>();
    private JarFile jar;

    public PreludeModuleClassloader(String name, File target, JarFile jar, ClassLoader parent) throws MalformedURLException
    {
        super(name, new URL[] { target.toURI().toURL() }, parent);

        this.jar = jar;

        ACTIVE_CLASSLOADERS.put(name, this);
    }

    @SuppressWarnings("unchecked")
    public <T> List<Class<? extends T>> collectSubclasses(Class<T> clazz)
    {
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

    public void purge()
    {
        LOADED_CLASSES.clear();
        ACTIVE_CLASSLOADERS.clear();
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return findClass(name, true);
    }

    public Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException
    {
        Class<?> result = this.interiorClasses.get(name);

        if (result != null)
        {
            return result;
        }

        if (checkGlobal)
        {
            result = this.findClassGlobal(name);

            if (result != null)
            {
                return result;
            }
        }

        result = super.findClass(name);

        if (result != null)
        {
            interiorClasses.put(name, result);
        }

        return result;
    }

    private Class<?> findClassGlobal(String name) throws ClassNotFoundException
    {
        Class<?> result = LOADED_CLASSES.get(name);

        if (result != null)
        {
            return result;
        }

        for (PreludeModuleClassloader loader : ACTIVE_CLASSLOADERS.values())
        {
            if (loader.equals(this))
                continue;

            try {
                result = loader.findClass(name, false);
    
                if (result != null)
                {
                    return result;
                }
            } catch (ClassNotFoundException e) {
                continue;
            }
        }

        return null;
    }
}