package coffee.khyonieheart.crafthyacinth.module.nouveau;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import coffee.khyonieheart.hyacinth.module.nouveau.pipeline.FileCollectionShader;
import coffee.khyonieheart.hyacinth.util.Folders;

public class BootstrapFileCollector implements FileCollectionShader
{
	@Override
	public List<File> collect() 
	{
		File base = Folders.ensureFolder("./Hyacinth/modules/");

		List<File> collected = new ArrayList<>(base.listFiles().length);
		for (File f : base.listFiles())
		{
			if (!f.getName().endsWith(".jar"))
			{
				continue;
			}

			if (f.isDirectory())
			{
				continue;
			}

			collected.add(f);
		}

		return collected;
	}
}
