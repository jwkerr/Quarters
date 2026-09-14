package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.FloodgateManager;
import au.lupine.quarters.api.manager.JSONManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.wrapper.UserGroup;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class FameMethod extends CommandMethod {

    private static final Map<UUID, String> CACHED_NAMES = new ConcurrentHashMap<>();

    public FameMethod() {
        super("fame", "quarters.command.quarters.fame");
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack) {
        List<UserGroup> userGroups = ConfigManager.getUserGroups();

        // Sending a message as response to indicate that the command is being processed. This is a user-friendly design.
        boolean hasUncachedName = userGroups.stream()
                .filter(UserGroup::shouldDisplayInFame)
                .flatMap(userGroup -> userGroup.getMembers().stream())
                .anyMatch(uuid -> !CACHED_NAMES.containsKey(uuid));

        // Only need to show if a name should be fetched from Mojang API
        if (hasUncachedName) {
            QuartersMessaging.sendMessage(stack.getSender(), Component.text("Fetching the wall of fame...").color(NamedTextColor.GRAY));
        }

        List<CompletableFuture<Component>> futureNames = new ArrayList<>();
        for (UserGroup userGroup : userGroups) {
            if (!userGroup.shouldDisplayInFame()) continue;

            for (UUID uuid : userGroup.getMembers()) {
                CompletableFuture<Component> future = getUsernameByUUIDAsync(uuid).thenApply(name -> {
                    if (name == null) return null;
                    return userGroup.formatString(name);
                });

                futureNames.add(future);
            }
        }

        CompletableFuture.allOf(futureNames.toArray(new CompletableFuture[0])).thenRun(() -> {
            List<Component> names = futureNames.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();

            TextComponent.Builder builder = Component.text();
            builder.append(QuartersMessaging.OPEN_SQUARE_BRACKET);
            builder.append(Component.text("Quarters Wall of Fame", TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB())));
            builder.append(QuartersMessaging.CLOSED_SQUARE_BRACKET).appendNewline();
            builder.append(Component.join(JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY)), names)).appendNewline();

            builder.append(Component.text("If you love Quarters and would like your own coloured name, please consider supporting development ", NamedTextColor.GREEN));
            if (FloodgateManager.getInstance().isBedrockPlayer(stack.getSender())) {
                // Bedrock does not support clickable components, so displaying the link directly is the most straight forward approach
                builder.append(Component.text("here: https://github.com/sponsors/jwkerr!!!", TextColor.color(0x2F81F7)));
            } else {
                builder.append(Component.text("here!!!", TextColor.color(0x2F81F7), TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/sponsors/jwkerr")));
            }
            builder.append(Component.text(" :3", NamedTextColor.GREEN));

            QuartersMessaging.sendComponent(stack.getSender(), builder.build());
        });
    }

    private CompletableFuture<@Nullable String> getUsernameByUUIDAsync(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String cachedName = CACHED_NAMES.get(uuid);
            if (cachedName != null) return cachedName;

            JsonObject jsonObject = JSONManager.getInstance().getUrlAsJsonElement("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid, JsonObject.class);
            if (jsonObject == null) return null;

            JsonElement nameElement = jsonObject.get("name");
            if (nameElement == null) return null;

            String name = nameElement.getAsString();
            CACHED_NAMES.put(uuid, name);

            return name;
        });
    }
}
