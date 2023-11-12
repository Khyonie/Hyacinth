package coffee.khyonieheart.tidal.validation;

public enum PlayerSelector
{
	SELF("@s"),
	NEAREST("@p"),
	EVERYONE("@a"),
	RANDOM("@r"),
	ENTITY("@e")
	;

	private String tag;

	private PlayerSelector(String tag)
	{
		this.tag = tag;
	}

	public String getTag()
	{
		return this.tag;
	}
}
