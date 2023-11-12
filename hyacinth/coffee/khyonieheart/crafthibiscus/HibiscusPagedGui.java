package coffee.khyonieheart.crafthibiscus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import coffee.khyonieheart.hibiscus.Element;
import coffee.khyonieheart.hibiscus.GuiConfiguration;
import coffee.khyonieheart.hibiscus.element.ButtonElement;
import coffee.khyonieheart.hibiscus.element.ItemStackElement;
import coffee.khyonieheart.hibiscus.inventory.InventoryBuilder;
import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

/**
 * Base implementation of a paged GUI that automatically generates configurations for each "page" of given elements.
 *
 * @since 1.0.0
 * @author Khyonie
 */
public class HibiscusPagedGui extends HibiscusGui
{
	private static final String leftArrow = "http://textures.minecraft.net/texture/5f133e91919db0acefdc272d67fd87b4be88dc44a958958824474e21e06d53e6";
	private static final String rightArrow = "http://textures.minecraft.net/texture/e3fc52264d8ad9e654f415bef01a23947edbccccf649373289bea4d149541f70";

	public HibiscusPagedGui(
		@Nullable String inventoryLabel, 
		@NotNull List<Element> data
	) {
		super(
			inventoryLabel, 
			6, 
			InventoryBuilder.builder(6, inventoryLabel)
				.create()
		);

		// Create configurations

		int numberOfPages = (data.size() / 36) + 1;
		for (int i = 0; i < numberOfPages; i++)
		{
			Element[] configuration = new Element[54];
			String name = "page" + i;

			for (int o = 0; o < 36; o++)
			{
				if ((i * 36) + o >= data.size())
				{
					break;
				}

				configuration[o + 9] = data.get((i * 36) + o);

				// Embed page buttons
				configuration[49] = new ItemStackElement(Material.BOOK, "§fPage " + (i + 1), (i + 1));

				// Draw back page
				configuration[48] = new ItemStackElement(new ItemStack(Material.AIR));
				
				if (i > 0)
				{
					Element button = new ButtonElement(getHead("§rBack", leftArrow))
						.setAction((event, player, clickedSlot, action, view, itemOnCursor) -> {
							removeLayer(player);
						});
					configuration[48] = button;
				}

				// Draw next page
				configuration[50] = new ItemStackElement(new ItemStack(Material.AIR));

				if (i < (numberOfPages - 1))
				{
					String nextPage = "page" + (i + 1);
					Element button = new ButtonElement(getHead("§rNext", rightArrow))
						.setAction((event, player, clickedSlot, action, view, itemOnCursor) -> {
							addLayer(player, 0, nextPage);
						});
					configuration[50] = button;
				}
			}

			// Register configuration
			this.addConfiguration(name, new GuiConfiguration(name, configuration));
		}
	}

	private static ItemStack getHead(
		@NotNull String name, 
		@NotNull String data
	) {
		PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
		try {
			profile.getTextures().setSkin(new URL(data));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setDisplayName(name);
		meta.setOwnerProfile(profile);
		skull.setItemMeta(meta);

		return skull;	
	}
}
