package coffee.khyonieheart.hyacinth.module.nouveau;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.crafthyacinth.module.HyacinthCoreModule;
import coffee.khyonieheart.crafthyacinth.module.nouveau.BootstrapFileCollector;
import coffee.khyonieheart.crafthyacinth.module.nouveau.BootstrapFileVerifier;
import coffee.khyonieheart.crafthyacinth.module.nouveau.CommandShader;
import coffee.khyonieheart.crafthyacinth.module.nouveau.FeatureShader;
import coffee.khyonieheart.crafthyacinth.module.nouveau.ListenerShader;
import coffee.khyonieheart.crafthyacinth.module.nouveau.ModuleShader;
import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.exception.InstantiationRuntimeException;
import coffee.khyonieheart.hyacinth.module.marker.PreventAutoLoad;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.ClassShader;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.FileCollectionShader;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.FileVerificationShader;
import coffee.khyonieheart.hyacinth.util.Collections;
import coffee.khyonieheart.hyacinth.util.Lists;
import coffee.khyonieheart.hyacinth.util.Reflect;
import coffee.khyonieheart.tidal.EnumClassShader;
import coffee.khyonieheart.tidal.TypeManager;

public class ModuleLoaderPipeline
{
	public void load()
	{
		//
		// Bootstrap
		//

		Logger.verbose("Running bootstrap file collector");
		List<File> files = new BootstrapFileCollector().collect();
		Logger.verbose("Collected files: [ " + Collections.toString(files, ", ") + " ]");

		Logger.verbose("Running bootstrap file shader");
		FileVerificationShader bootstrapShader = new BootstrapFileVerifier();

		Iterator<File> fileIterator = files.iterator();
		File file;
		while (fileIterator.hasNext())
		{
			file = fileIterator.next();
			if (bootstrapShader.verify(file))
			{
				fileIterator.remove();
			}
		}

		Logger.verbose("Files after shading: [ " + Collections.toString(files, ", ") + " ]");

		Map<ModuleFile, YamlConfiguration> loaderConfigurations = new HashMap<>();

		// Assign classloaders
		List<ModuleFile> modules = new ArrayList<>();
		for (File f : files)
		{
			try {
				ModuleFile module = new ModuleFile(f);
				Logger.verbose("Loading mod.yml for file " + f.getName());
				module.loadConfiguration("mod.yml");

				if (module.getConfiguration() == null)
				{
					Logger.verbose("§cConfiguration is null! Cannot load module.");
					continue;
				}
				Logger.verbose("Loaded non-null configuration.");

				NouveauClassloader classloader = new NouveauClassloader(module, this.getClass().getClassLoader());
				ClassCoordinator.addClassloader(classloader.getIdentifier(), classloader);
				modules.add(module);

				// Load loader config into memory
				YamlConfiguration loaderConfig = module.getLoaderConfiguration();
				if (loaderConfig != null)
				{
					Logger.verbose("Module " + classloader.getIdentifier() + " has a loader.yml, adding it to load later");
					loaderConfigurations.put(module, loaderConfig);
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}

		Logger.verbose("Classloaders created");

		Map<LoaderPriority, List<ShaderPath>> fileCollectorPaths = Collections.mapOfArray(() -> new ArrayList<>(), LoaderPriority.values());
		Map<LoaderPriority, List<ShaderPath>> fileVerifierPaths = Collections.mapOfArray(() -> new ArrayList<>(), LoaderPriority.values());
		Map<LoaderPriority, List<ShaderPath>> classShaderPaths = Collections.mapOfArray(() -> new ArrayList<>(), LoaderPriority.values());

		Logger.verbose("Processing loader.ymls");

		// Process loader.ymls
		for (ModuleFile module : loaderConfigurations.keySet())
		{
			YamlConfiguration loaderConfig = loaderConfigurations.get(module);
			
			// File collectors
			Map<LoaderPriority, List<String>> loaderPaths = this.getPriorityStringArrays(loaderConfig, "file_collectors");
			for (LoaderPriority priority : LoaderPriority.values())
			{
				fileCollectorPaths.get(priority).addAll(Lists.map(loaderPaths.get(priority), (p) -> new ShaderPath(module, p)));
			}

			// File verifiers
			loaderPaths = this.getPriorityStringArrays(loaderConfig, "file_verifiers");
			for (LoaderPriority priority : LoaderPriority.values())
			{
				fileVerifierPaths.get(priority).addAll(Lists.map(loaderPaths.get(priority), (p) -> new ShaderPath(module, p)));
			}

			// Class shaders
			loaderPaths = this.getPriorityStringArrays(loaderConfig, "class_shaders");
			for (LoaderPriority priority : LoaderPriority.values())
			{
				classShaderPaths.get(priority).addAll(Lists.map(loaderPaths.get(priority), (p) -> new ShaderPath(module, p)));
			}
		}

		//
		// Instantiation
		//

		Map<LoaderPriority, List<FileCollectionShader>> collectionShaders = Collections.mapOfArray(() -> new ArrayList<>(), LoaderPriority.values());
		Map<LoaderPriority, List<FileVerificationShader>> verificationShaders = Collections.mapOfArray(() -> new ArrayList<>(), LoaderPriority.values());
		Map<LoaderPriority, List<ClassShader<?>>> classShaders = Collections.mapOfArray(() -> new ArrayList<>(), LoaderPriority.values());

		classShaders.get(LoaderPriority.HIGHEST).add(new ModuleShader());
		classShaders.get(LoaderPriority.NORMAL).add(new CommandShader<>());
		classShaders.get(LoaderPriority.NORMAL).add(new ListenerShader());
		classShaders.get(LoaderPriority.NORMAL).add(new FeatureShader());

		// Tidal
		classShaders.get(LoaderPriority.HIGH).add(new EnumClassShader());
		classShaders.get(LoaderPriority.HIGHEST).add(new TypeManager());
		// End tidal

		for (LoaderPriority priority : LoaderPriority.values())
		{
			Class<?> clazz;
			// File collectors
			for (ShaderPath path : fileCollectorPaths.get(priority))
			{
				try {
					clazz = ClassCoordinator.getGlobalClass(path.path(), null);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}

				try {
					FileCollectionShader shader = (FileCollectionShader) Reflect.simpleInstantiate(clazz);
					collectionShaders.get(priority).add(shader);
					Logger.verbose("Added file collector " + clazz.getName() + " with priority " + priority.name());
				} catch (InstantiationRuntimeException e) {
					Logger.log("An exception was thrown when instantiating file collector shader \"" + clazz.getName() + "\"");
					e.printStackTrace();
					continue;
				}

				Logger.verbose("Added file collection shader " + clazz.getName() + " belonging to module " + path.owner().getConfiguration().getString("name"));
			}

			// File verifiers
			for (ShaderPath path : fileVerifierPaths.get(priority))
			{
				try {
					clazz = ClassCoordinator.getGlobalClass(path.path(), HyacinthCoreModule.getModuleFile());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}

				try {
					FileVerificationShader shader = (FileVerificationShader) Reflect.simpleInstantiate(clazz);
					verificationShaders.get(priority).add(shader);
					Logger.verbose("Added file shader " + clazz.getName() + " with priority " + priority.name());
				} catch (InstantiationRuntimeException e) {
					Logger.log("An exception was thrown when instantiating file verification shader \"" + clazz.getName() + "\"");
					e.printStackTrace();
					continue;
				}

				Logger.verbose("Added file verification shader " + clazz.getName() + " belonging to module " + path.owner().getConfiguration().getString("name"));
			}

			// Class shaders
			for (ShaderPath path : classShaderPaths.get(priority))
			{
				try {
					clazz = ClassCoordinator.getGlobalClass(path.path(), HyacinthCoreModule.getModuleFile()); 
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}

				try {
					ClassShader<?> shader = (ClassShader<?>) Reflect.simpleInstantiate(clazz);
					classShaders.get(priority).add(shader);
					Logger.verbose("Added class shader " + clazz.getName() + " with priority " + priority.name());
				} catch (InstantiationRuntimeException e) {
					Logger.log("An exception was thrown when instantiating class shader \"" + clazz.getName() + "\"");
					e.printStackTrace();
					continue;
				}

				Logger.verbose("Added class shader " + clazz.getName() + " belonging to module " + path.owner().getConfiguration().getString("name"));
			}
		}

		//
		// Programmable file collection
		//
		
		Logger.verbose("Running programmable file collectors");
		List<File> collectedFiles = new ArrayList<>();
		for (LoaderPriority priority : LoaderPriority.values())
		{
			for (FileCollectionShader shader : collectionShaders.get(priority))
			{
				Logger.verbose("Running collector " + shader.getClass().getName());
				try {
					collectedFiles.addAll(shader.collect());
				} catch (Exception e) {
					Logger.log("An exception occurred when running file collector " + shader.getClass().getName());
					e.printStackTrace();
					continue;
				}
			}
		}

		Logger.verbose("Collected files: [ " + Collections.toString(collectedFiles, ", ") + " ]");
		Logger.verbose("Running programmable file shaders");

		File collectedFile;
		Iterator<File> iterator = collectedFiles.iterator();
		for (LoaderPriority priority : LoaderPriority.values())
		{
			iter: while (iterator.hasNext())
			{
				collectedFile = iterator.next();

				if (collectedFile == null)
				{
					continue;
				}

				for (FileVerificationShader shader : verificationShaders.get(priority))
				{
					Logger.verbose("Running shader " + shader.getClass().getName() + " on file " + collectedFile.getName());
					try {
						if (shader.verify(collectedFile))
						{
							iterator.remove();
							Logger.verbose("Removed " + collectedFile.getName());
							continue iter;
						}
					} catch (Exception e) {
						Logger.log("An exception occurred when running file verifier " + shader.getClass().getName() + " on file " + collectedFile.getName());
					}
				}
			}
			iterator = collectedFiles.iterator(); // Reset iterator
			collectedFile = null;
		}

		//
		// Contingent classloader creation
		//
		
		Logger.verbose("Creating contingent classloaders");

		for (File f : collectedFiles)
		{
			ModuleFile moduleFile;
			try {
				moduleFile = new ModuleFile(f);
				moduleFile.loadConfiguration("mod.yml");
				NouveauClassloader classloader = new NouveauClassloader(moduleFile, this.getClass().getClassLoader());
				ClassCoordinator.addClassloader(classloader.getIdentifier(), classloader);
				modules.add(moduleFile);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}

		Logger.verbose("All classloaders created (" + ClassCoordinator.getClassloaders().size() + " total)");

		//
		// Class shading
		//

		Logger.verbose("Shading classes");
		Map<Class<?>, Object> instances = new HashMap<>(); // Cache instances as they're created
		for (LoaderPriority priority : LoaderPriority.values())
		{
			for (NouveauClassloader classloader : ClassCoordinator.getClassloaders())
			{
				processClassloader(classloader, classShaders.get(priority), instances);
			}
		}
	}

	private static void processClassloader(
		@NotNull NouveauClassloader classloader,
		@NotNull List<ClassShader<?>> classShaders,
		@NotNull Map<Class<?>, Object> instances
	) {
		for (Class<?> clazz : classloader.enumerate())
		{
			if (clazz.isAnnotationPresent(PreventAutoLoad.class))
			{
				continue;
			}

			for (ClassShader<?> classShaderUntyped : classShaders)
			{
				if (!classShaderUntyped.getType().isAssignableFrom(clazz))
				{
					continue;
				}

				try {
					processClassInContext(clazz, classShaderUntyped, instances);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void processClassInContext(
		@NotNull Class<?> classUntyped,
		@NotNull ClassShader<?> shaderUntyped,
		@NotNull Map<Class<?>, Object> instances
	) {
		Class<T> clazz = (Class<T>) classUntyped;
		ClassShader<T> shader = (ClassShader<T>) shaderUntyped;

		T obj = shader.process(clazz, (T) instances.get(clazz));

		if (obj != null && !instances.containsKey(clazz))
		{
			instances.put(clazz, obj);
		}
	}

	@NotNull
	private Map<LoaderPriority, List<String>> getPriorityStringArrays(
		@NotNull YamlConfiguration config,
		@Nullable String target
	) {
		Objects.requireNonNull(config);
		Map<LoaderPriority, List<String>> data = Collections.mapOf(() -> new ArrayList<>(), LoaderPriority.LOWEST, LoaderPriority.LOW, LoaderPriority.NORMAL, LoaderPriority.HIGH, LoaderPriority.HIGHEST);

		if (!config.contains(target))
		{
			return data;
		}

		for (LoaderPriority priority : LoaderPriority.values())
		{
			if (!config.contains(target + "." + priority.name()))
			{
				continue;
			}

			data.put(priority, config.getStringList(target + "." + priority.name()));
		}

		return data;
	}
}
