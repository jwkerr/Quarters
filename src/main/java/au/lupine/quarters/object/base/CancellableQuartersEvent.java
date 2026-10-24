package au.lupine.quarters.object.base;

import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.Nullable;

public abstract class CancellableQuartersEvent extends QuartersEvent implements Cancellable {

    private String cancelMessage = null;
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

    public @Nullable String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(@Nullable String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }
}
