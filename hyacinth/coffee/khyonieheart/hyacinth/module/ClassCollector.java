package coffee.khyonieheart.hyacinth.module;

import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public interface ClassCollector<T> extends Iterable<T>
{
	@NotNull
	public Map<String, Class<? extends T>> getClasses();

	@NotNull
	public Class<? extends T> getClass(
		@NotNull String name
	) throws 
		ClassNotFoundException;

	public Class<T> getType();

	@SuppressWarnings("unchecked")
	public default void collect(
		@NotNull Class<T> tClass,
		@NotNull Map<String, Class<? extends T>> classes,
		@NotNull Enumeration<JarEntry> entries,
		@NotNull URLClassLoader classloader
	) {
		Objects.requireNonNull(entries);
		Objects.requireNonNull(classloader);

		JarEntry entry;
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

			try {
				Class<?> prospect = Class.forName(entry.getName().replace("/", ".").replace(".class", ""), false, classloader);

				if (this.getType().isAssignableFrom(prospect))
				{
					classes.put(prospect.getName(), (Class<? extends T>) prospect);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}
