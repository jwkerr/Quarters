package au.lupine.quarters.object.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UserGroup {

    private String name = "default";
    private Color colour = new Color(NamedTextColor.GRAY.value());
    private String description = null;
    private boolean catMode = false;
    private List<TextDecoration> decorations = List.of();
    private List<UUID> members = Collections.emptyList();

    private final boolean shouldDisplayInFame;

    public UserGroup(JsonObject jsonObject) {
        name = jsonObject.get("name").getAsString();
        colour = new Color(Integer.parseInt(jsonObject.get("colour").getAsString(), 16));

        JsonElement descriptionElement = jsonObject.get("description");
        if (!descriptionElement.isJsonNull()) {
            description = descriptionElement.getAsString();
        } else {
            description = null;
        }

        catMode = jsonObject.get("cat_mode").getAsBoolean();

        List<TextDecoration> decorations = new ArrayList<>();
        for (JsonElement element : jsonObject.get("decorations").getAsJsonArray()) {
            decorations.add(TextDecoration.valueOf(element.getAsString()));
        }
        this.decorations = decorations;

        List<UUID> members = new ArrayList<>();
        for (JsonElement element : jsonObject.get("members").getAsJsonArray()) {
            members.add(UUID.fromString(element.getAsString()));
        }
        this.members = members;

        this.shouldDisplayInFame = true;
    }

    public UserGroup(String name, Color colour, @Nullable String description, boolean catMode, List<TextDecoration> decorations, List<UUID> members) {
        this.name = name;
        this.colour = colour;
        this.description = description;
        this.catMode = catMode;
        this.decorations = decorations;
        this.members = members;

        this.shouldDisplayInFame = false;
    }

    /**
     * Create a default UserGroup
     */
    public UserGroup() {
        this.shouldDisplayInFame = false;
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public String getName() {
        return name;
    }

    public Color getColour() {
        return colour;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public boolean hasCatMode() {
        return catMode;
    }

    public List<TextDecoration> getDecorations() {
        return decorations;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public boolean shouldDisplayInFame() {
        return shouldDisplayInFame;
    }
}
