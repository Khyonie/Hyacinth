package coffee.khyonieheart.hyacinth.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;
import coffee.khyonieheart.hyacinth.option.Option;

/**
 * Various utilities for reading and writing .json files.
 *
 * @author Khyonie
 * @since 1.0.0
 */
public class JsonUtils
{
	private static Gson gson = new GsonBuilder()
		.disableHtmlEscaping()
		.excludeFieldsWithoutExposeAnnotation()
		.create();

	/**
	 * Obtains a Gson instance.
	 *
	 * @return Gson object
	 */
	@NotNull
	public static Gson getGson()
	{
		return gson;
	}

	/**
	 * Deserializes an object from a .json file.
	 *
	 * @param <T> Type of object
	 * @param path Filepath to .json file
	 * @param type Class of object
	 *
	 * @return An object read from the given file. May return null if the file cannot be read.
	 * @throws FileNotFoundException When a file does not exist at the given path.
	 *
	 * @since 1.0.0
	 */
	@Nullable
	public static <T> T fromJson(
		@NotNull String path, 
		@NotNull Class<T> type
	)
		throws FileNotFoundException
	{
		File file = new File(path);
		if (!file.exists())
		{
			throw new FileNotFoundException("File \"" + file.getName() + "\" does not exist");
		}

		try (FileReader reader = new FileReader(file))
		{
			return gson.fromJson(reader, type);	
		} catch (IOException | IllegalStateException | JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deserializes an object from a .json file, with support for interface types in java.lang.reflect.Type.
	 *
	 * @param <T> Type of object
	 * @param path Filepath to .json file
	 * @param type Type of object, see com.google.gson.reflect.TypeToken
	 *
	 * @return An object read from the given file. May return null if the file cannot be read.
	 * @throws FileNotFoundException When a file does not exist at the given path.
	 *
	 * @since 1.0.0
	 */
	@Nullable
	public static <T> T fromJson(
		@NotNull String path,
		@NotNull Type type
	)
		throws FileNotFoundException
	{
		File file = new File(path);
		if (!file.exists())
		{
			throw new FileNotFoundException("File \"" + file.getName() + "\" does not exist");
		}

		try (FileReader reader = new FileReader(file))
		{
			return gson.fromJson(reader, type);
		} catch (IOException | IllegalStateException | JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deserializes an object from a .json file.
	 *
	 * @param <T> Type of object
	 * @param path Filepath to .json file
	 * @param type Class of object
	 *
	 * @throws FileNotFoundException When a file does not exist at the given path.
	 * @return See optional
	 *
	 * @hyacinth.some <T>
	 * @hyacinth.none File could not be read or is corrupt.
	 *
	 * @since 1.0.0
	 */
	@NotNull
	public static <T> Option fromJsonOption(
		@NotNull String path,
		@NotNull Class<T> type
	)
		throws FileNotFoundException
	{
		T obj = (T) fromJson(path, type);

		return obj != null ? Option.some(obj) : Option.none();
	}

	/**
	 * Serializes an object and saves it to a .json file.
	 *
	 * @param path Filepath to write to
	 * @param object Object to serialize
	 *
	 * @return The file that was written to
	 *
	 * @since 1.0.0
	 */
	@NotNull
	public static File toFile(
		@NotNull String path, 
		@NotNull Object object
	) {
		File file = new File(path);

		BufferedWriter writer;
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			writer = new BufferedWriter(fileWriter);

			writer.write(gson.toJson(object));
			writer.flush();

			writer.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return file;
	}
}
