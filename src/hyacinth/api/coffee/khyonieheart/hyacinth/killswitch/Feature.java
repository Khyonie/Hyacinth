package coffee.khyonieheart.hyacinth.killswitch;

import java.lang.reflect.Field;
import java.util.Objects;

import coffee.khyonieheart.hyacinth.Logger;

public interface Feature
{
	public default boolean kill(
		String target
	) {
		Objects.requireNonNull(target);

		try {
			Field targetField = this.getClass().getDeclaredField(target);
			if (!((boolean) targetField.get(this)))
			{
				return false;
			}

			targetField.setAccessible(true);
			targetField.set(this, false);

			return true;
		} catch (NoSuchFieldException e) {
			Logger.log("§eNo such feature-flag field " + target + " in class " + this.getClass().getName());
			return false;
		} catch (ClassCastException e) {
			Logger.log("§eIllegal feature-flag field " + target + " in class " + this.getClass().getName() + "; field must be a boolean to be a valid feature-flag.");
			return false;
		} catch (IllegalAccessException e) {
			return false;
		}
	}

	public default boolean reenable(
		String target
	) {
		Objects.requireNonNull(target);

		try {
			Field targetField = this.getClass().getDeclaredField(target);
			if ((boolean) targetField.get(this))
			{
				return false;
			}

			targetField.setAccessible(true);
			targetField.set(this, true);

			return true;
		} catch (NoSuchFieldException e) {
			Logger.log("§eNo such feature-flag field " + target + " in class " + this.getClass().getName());
			return false;
		} catch (ClassCastException e) {
			Logger.log("§eIllegal feature-flag field " + target + " in class " + this.getClass().getName() + "; field must be a boolean to be a valid feature-flag.");
			return false;
		} catch (IllegalAccessException e) {
			return false;
		}
	}

	public default boolean isEnabled(
		String target
	) {
		Objects.requireNonNull(target);

		try {
			Field targetField = this.getClass().getDeclaredField(target);

			targetField.setAccessible(true);

			return (boolean) targetField.get(this);
		} catch (NoSuchFieldException e) {
			Logger.log("§eNo such feature-flag field " + target + " in class " + this.getClass().getName());
			return false;
		} catch (ClassCastException e) {
			Logger.log("§eIllegal feature-flag field " + target + " in class " + this.getClass().getName() + "; field must be a boolean to be a valid feature-flag.");
			return false;
		} catch (IllegalAccessException e) {
			return false;
		}
	}
}
