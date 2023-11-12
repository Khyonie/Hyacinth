package coffee.khyonieheart.crafthyacinth.module.provider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;

/**
 * Provider primer classloader, used for loading provider classes.
 *
 * @author Khyonie
 * @since 1.0.0
 */
class HyacinthProviderClassloader extends URLClassLoader
{
    private Map<String, Class<?>> interiorClasses = new HashMap<>();
    private HyacinthProviderPrimer manager;

	/**
	 * @param hostFile File that hosts this provider
	 * @param parent Parent classloader
	 * @param manager Hyacinth provider primer that is handling this provider
	 *
	 * @throws MalformedURLException When host file path is corrupt
	 */
    public HyacinthProviderClassloader(
		@NotNull File hostFile, 
		@NotNull ClassLoader parent, 
		@NotNull HyacinthProviderPrimer manager
	)
		throws MalformedURLException
    {
        super(hostFile.getName(), new URL[] { hostFile.toURI().toURL() }, parent);

		Objects.requireNonNull(parent);
		Objects.requireNonNull(manager);

        this.manager = manager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return findClass(name, true);
    }

	/**
	 * Attempts to find a class.
	 *
	 * @param name Class name
	 * @param checkGlobal Whether or not to check global class cache
	 *
	 * @return A class contained in the host file.
	 * @throws ClassNotFoundException Class does not exist in host file
	 */
	@NotNull
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
