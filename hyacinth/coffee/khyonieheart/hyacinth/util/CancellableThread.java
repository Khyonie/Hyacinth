package coffee.khyonieheart.hyacinth.util;

import java.util.Objects;
import java.util.function.Consumer;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;

public class CancellableThread extends Thread
{
	private Consumer<CancellableThread> runnable;
	private boolean isCancelled = false;

	public CancellableThread(
		@NotNull Consumer<CancellableThread> runnable
	) {
		Objects.requireNonNull(runnable);
		
		this.runnable = runnable;
	}

	@Override
	public void run()
	{
		runnable.accept(this);
	}

	public void cancel()
	{
		this.isCancelled = true;
	}

	public boolean isCancelled()
	{
		return this.isCancelled;
	}
}
