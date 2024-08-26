package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import com.google.gson.GsonBuilder;

public class PreBuildGsonEvent extends QuartersEvent {

    private final GsonBuilder builder;

    public PreBuildGsonEvent(GsonBuilder builder) {
        super(true);
        this.builder = builder;
    }

    public GsonBuilder getBuilder() {
        return builder;
    }
}
