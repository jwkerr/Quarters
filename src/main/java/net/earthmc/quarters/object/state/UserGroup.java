package net.earthmc.quarters.object.state;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public enum UserGroup {
    AUTHOR(new Color(0xF6003C), "This player is a developer of Quarters", true),
    CORRUPTEDGREED(new Color(0x8A50C4), null, true),
    DEFAULT(new Color(NamedTextColor.GRAY.value()), null, false),
    EARLY_SUPPORTER(new Color(NamedTextColor.GOLD.value()), "This player helped fund the early days of Quarters, thanks!", false),
    OXBIT(new Color(0x003CF6), null, true),
    TUZZZIE(new Color(0xFF69B4), "This player offered great emotional support during Quarters' development", true);

    private final Color colour;
    private final String description;
    private final boolean catMode;

    UserGroup(Color colour, String description, boolean catMode) {
        this.colour = colour;
        this.description = description;
        this.catMode = catMode;
    }

    public @NotNull Color getColour() {
        return colour;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public boolean hasCatMode() {
        return catMode;
    }
}
