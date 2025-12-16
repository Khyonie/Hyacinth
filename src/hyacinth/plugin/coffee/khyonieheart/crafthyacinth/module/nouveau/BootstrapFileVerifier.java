package coffee.khyonieheart.crafthyacinth.module.nouveau;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import coffee.khyonieheart.hyacinth.Logger;
import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.FileVerificationShader;
import coffee.khyonieheart.hyacinth.util.Collections;
import coffee.khyonieheart.hyacinth.util.JarUtils;

public class BootstrapFileVerifier implements FileVerificationShader
{
	private static final String[] REQUIRED_CONFIGURATION_ELEMENTS = {
		"name",
		"package",
		"entry",
		"author",
		"description"
	};

	@Override
	public boolean verify(File file) 
	{
		try (JarFile jar = new JarFile(file))
		{
			if (jar.getEntry("mod.yml") == null)
			{
				Logger.log("Jarfile \"" + file.getName() + " does not contain a mod.yml");
				return true;
			}

			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(new InputStreamReader(JarUtils.toInputStream(jar, "mod.yml")));
			} catch (InvalidConfigurationException e) {
				Logger.log("Jarfile \"" + file.getName() + "\"'s mod.yml is invalid or corrupt");
				e.printStackTrace();
				return true;
			}

			List<String> missingKeys = new ArrayList<>();
			for (String s : REQUIRED_CONFIGURATION_ELEMENTS)
			{
				if (!config.contains(s))
				{
					missingKeys.add(s);
				}
			}

			if (!missingKeys.isEmpty())
			{
				Logger.log("Jarfile \"" + file.getName() + "\"'s mod.yml is incomplete. Missing keys: [ " + Collections.toString(missingKeys, ", ") + " ]");
				return true;
			}
			
			Logger.verbose("Validated mod.yml config for " + file.getName());
		} catch (IOException e) {
			Logger.log("Invalid/corrupt module jarfile \"" + file.getName() + "\"");
			e.printStackTrace();
			return true;
		} 
		return false;
	}
}
