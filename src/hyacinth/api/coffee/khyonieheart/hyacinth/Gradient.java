package coffee.khyonieheart.hyacinth;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Gradient
{
	private int red1, red2, green1, green2, blue1, blue2;

	public Gradient(
		String colorA, 
		String colorB
	) {
		colorA = colorA.substring(1);
		colorB = colorB.substring(1);
		this.red1 = Integer.parseInt(colorA.substring(0, 2), 16);
		this.red2 = Integer.parseInt(colorB.substring(0, 2), 16);
		this.green1 = Integer.parseInt(colorA.substring(2, 4), 16);
		this.green2 = Integer.parseInt(colorB.substring(2, 4), 16);
		this.blue1 = Integer.parseInt(colorA.substring(4), 16);
		this.blue2 = Integer.parseInt(colorB.substring(4), 16);
	}

	public BaseComponent[] createComponents(
		String input,
		char... formatting
	) {
		int deltaRed = (red2 - red1) / input.length();
		int deltaGreen = (green2 - green1) / input.length();
		int deltaBlue = (blue2 - blue1) / input.length();

		ComponentBuilder builder = new ComponentBuilder();
		int r = red1;
		int g = green1;
		int b = blue1;
		for (int i = 0; i < input.length(); i++)
		{
			r += deltaRed;
			g += deltaGreen;
			b += deltaBlue;

			String hex = "#" + String.format("%2.2s", Integer.toHexString(r)).replace(' ', '0') + String.format("%2.2s", Integer.toHexString(g)).replace(' ', '0') + String.format("%2.2s", Integer.toHexString(b)).replace(' ', '0');

			TextComponent component = new TextComponent(Character.toString(input.charAt(i)));
			component.setColor(ChatColor.of(hex));

			builder.append(component);
		}

		return builder.create();
	}

	public static BaseComponent[] createComponents(GradientGroup... gradients)
	{
		if (gradients.length == 0)
		{
			return new BaseComponent[] {};
		}

		List<BaseComponent> components = new ArrayList<>();

		for (GradientGroup g : gradients)
		{
			Gradient gradient = new Gradient(g.colorA(), g.colorB());
			for (BaseComponent c : gradient.createComponents(g.message(), g.formatting()))
			{
				components.add(c);
			}
		}

		return components.toArray(new BaseComponent[components.size()]);
	}

	public static record GradientGroup(
		String message,
		String colorA,
		String colorB,
		char... formatting
	) { }
}
