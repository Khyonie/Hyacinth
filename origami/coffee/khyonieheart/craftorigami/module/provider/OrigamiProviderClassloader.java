package coffee.khyonieheart.craftorigami.module.provider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

class OrigamiProviderClassloader extends URLClassLoader
{
    private Map<String, Class<?>> interiorClasses = new HashMap<>();
    private OrigamiProviderPrimer manager;

    public OrigamiProviderClassloader(File hostFile, ClassLoader parent, OrigamiProviderPrimer manager) throws MalformedURLException
    {
        super(hostFile.getName(), new URL[] { hostFile.toURI().toURL() }, parent);
        this.manager = manager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return findClass(name, true);
    }

    public Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException
    {
        Class<?> result = interiorClasses.get(name);

        if (result != null)
        {
            return result;
        }

        if (checkGlobal)
        {
            result = manager.getGlobalClass(name, this);

            if (result != null)
                return result;
        }

        result = super.findClass(name);

        if (result != null)
        {
            interiorClasses.put(name, result);
            return result;
        }

        throw new ClassNotFoundException();
    }
}
