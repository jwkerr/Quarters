package net.earthmc.quarters.object.state;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public enum UserGroup {
    AUTHOR(new Color(0xF6003C), "This player is a developer of Quarters"),
    DEFAULT(new Color(NamedTextColor.GRAY.value()), null),
    EARLY_SUPPORTER(new Color(NamedTextColor.GOLD.value()), "This player helped fund the early days of Quarters, thanks!"),
    OXBIT(new Color(0x003CF6), null),
    TUZZZIE(new Color(0xFF69B4), "This player offered great emotional support during Quarters' development");

    private final Color colour;
    private final String description;

    UserGroup(Color colour, String description) {
        this.colour = colour;
        this.description = description;
    }

    public Color getColour() {
        return colour;
    }

    public @Nullable String getDescription() {
        return description;
    }
}
