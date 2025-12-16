package coffee.khyonieheart.hibiscus;

import java.util.HashMap;
import java.util.Map;

public class GuiConfiguration
{
	private String name;
	private Map<Integer, Element> data;

	public GuiConfiguration(String name, Map<Integer, Element> data)
	{
		this.name = name;
		this.data = data;
	}

	public GuiConfiguration(String name, Element[] data)
	{
		Map<Integer, Element> newData = new HashMap<>();

		for (int i = 0; i < data.length; i++)
		{
			newData.put(i, data[i]);
		}

		this.data = newData;
	}

	public String getName()
	{
		return this.name;
	}

	public Map<Integer, Element> getData()
	{
		return this.data;
	}
}
