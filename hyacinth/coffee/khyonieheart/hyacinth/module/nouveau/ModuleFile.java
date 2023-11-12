package coffee.khyonieheart.hyacinth.module.nouveau;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;
import coffee.khyonieheart.hyacinth.module.ModuleOwned;
import coffee.khyonieheart.hyacinth.util.JarUtils;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public class ModuleFile implements ModuleOwned
{
	private YamlConfiguration config;
	private JarFile file;
	private File originalFile;
	private Map<String, Class<?>> ownedClasses = new HashMap<>();
	private NouveauClassloader classloader;
	private HyacinthModule owner;

	public ModuleFile(
		@NotNull File file
	) 
		throws FileNotFoundException,
			IOException
	{
		Objects.requireNonNull(file);
		
		if (!file.exists())
		{
			throw new FileNotFoundException();
		}

		this.file = new JarFile(file);
		this.originalFile = file;
	}

	public File getFile()
	{
		return this.originalFile;
	}

	public JarFile getJar()
	{
		return this.file;
	}

	void attach(
		@NotNull NouveauClassloader classloader
	) {
		Objects.requireNonNull(classloader);

		this.classloader = classloader;
	}

	void attachModule(
		@NotNull HyacinthModule module
	) {
		Objects.requireNonNull(module);

		this.owner = module;
	}

	public void enumerate()
	{
		this.classloader.enumerate();
	}

	@Nullable
	public Class<?> getOwnedClass(
		@NotNull String name
	) {
		Objects.requireNonNull(name);

		return this.ownedClasses.get(name);
	}

	public void addOwnedClass(
		@NotNull String name,
		@NotNull Class<?> clazz
	) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(clazz);

		this.ownedClasses.put(name, clazz);
	}

	public void loadConfiguration(
		@NotNull String path
	) {
		Objects.requireNonNull(path);	

		this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(JarUtils.toInputStream(this.file, path)));
	}

	@Nullable
	public YamlConfiguration getLoaderConfiguration()
	{
		if (this.file.getEntry("loader.yml") == null)
		{
			return null;
		}

		return YamlConfiguration.loadConfiguration(new InputStreamReader(JarUtils.toInputStream(this.file, "loader.yml")));
	}

	public YamlConfiguration getConfiguration()
	{
		return this.config;
	}

	@Override
	public HyacinthModule getModule() 
	{
		return this.owner;
	}
}
