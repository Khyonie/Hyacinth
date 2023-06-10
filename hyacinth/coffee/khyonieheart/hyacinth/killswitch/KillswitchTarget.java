package coffee.khyonieheart.hyacinth.killswitch;

public interface KillswitchTarget
{
	public boolean kill(String target);

	public boolean reenable(String target);

	public boolean isEnabled(String target);
}
