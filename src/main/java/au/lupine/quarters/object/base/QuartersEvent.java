package au.lupine.quarters.object.base;

import org.bukkit.event.Event;

public abstract class QuartersEvent extends Event {

    public QuartersEvent() {
        super();
    }

    public QuartersEvent(boolean isAsync) {
        super(isAsync);
    }
}
