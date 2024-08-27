package au.lupine.quarters.object.base;

import org.bukkit.event.Cancellable;

public abstract class CancellableQuartersEvent extends QuartersEvent implements Cancellable {

    private boolean isCancelled;

    public CancellableQuartersEvent() {
        super();
    }

    public CancellableQuartersEvent(boolean isAsync) {
        super(isAsync);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
