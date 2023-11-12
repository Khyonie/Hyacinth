package coffee.khyonieheart.hyacinth.killswitch;

public interface Feature
{
	public boolean kill(String target);

	public boolean reenable(String target);

	public boolean isEnabled(String target);
}
