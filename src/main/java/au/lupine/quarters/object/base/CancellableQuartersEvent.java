package au.lupine.quarters.object.base;

import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.Nullable;

public abstract class CancellableQuartersEvent extends QuartersEvent implements Cancellable {

    private boolean isCancelled;
    private String cancelMessage = null;

    public CancellableQuartersEvent() {
        super();
    }

    public CancellableQuartersEvent(boolean isAsync) {
        super(isAsync);
    }

    public @Nullable String getCancelMessage() {
        return cancelMessage;
    }

    /**
     * If a cancel message is set, this can be used by the event but depends on the event's implementation
     */
    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
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
