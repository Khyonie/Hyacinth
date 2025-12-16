package coffee.khyonieheart.hyacinth.module;

import java.util.Collection;

import coffee.khyonieheart.anenome.NotNull;
import coffee.khyonieheart.anenome.Nullable;

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
