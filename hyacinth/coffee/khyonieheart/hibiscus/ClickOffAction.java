package coffee.khyonieheart.hibiscus;

public enum ClickOffAction
{
	/** When clicked on a layer not the top layer, drill down to the first layer with an element in that slot. */
	DRILLDOWN,
	/** When clicked on a layer not the top layer, remove the top layer without activating the element in that slot. */
	REMOVE_ONE_LAYER,
	/** When clicked on a layer not the top layer, remove all layers down to the clicked layer without activating the element in that slot. */
	REMOVE_DOWN_TO_CLICKED,
	/** Don't do anything when the top layer isn't clicked. */
	NONE
	;
}
