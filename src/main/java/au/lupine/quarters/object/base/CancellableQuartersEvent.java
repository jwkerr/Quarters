package au.lupine.quarters.object.base;

import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public abstract class CancellableQuartersEvent extends QuartersEvent implements Cancellable {

    private boolean isCancelled;
    private String cancelMessage = ChatColor.RED + "A plugin cancelled this event.";

    public CancellableQuartersEvent() {
        super();
    }

    public CancellableQuartersEvent(boolean isAsync) {
        super(isAsync);
    }

    @NotNull
    public String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(@NotNull String cancelMessage) {
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
