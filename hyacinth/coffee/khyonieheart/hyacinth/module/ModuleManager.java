package coffee.khyonieheart.hyacinth.module;

import java.util.Collection;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public interface ModuleManager
{
	@Nullable
	public HyacinthModule getModule(
		@NotNull String identifier
	);

	@NotNull 
	public Collection<HyacinthModule> getModules();

	public void registerModule(
		@NotNull HyacinthModule module
	);
}
