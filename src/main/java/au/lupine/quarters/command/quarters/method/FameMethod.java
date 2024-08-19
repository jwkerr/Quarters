package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.JSONManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.wrapper.UserGroup;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class FameMethod extends CommandMethod {

    private static final Map<UUID, String> CACHED_NAMES = new ConcurrentHashMap<>();

    public FameMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.fame");
    }

    @Override
    public void execute() {
        List<UserGroup> userGroups = ConfigManager.getUserGroups();

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
            builder.append(Component.text("here!!!", TextColor.color(0x2F81F7), TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/sponsors/jwkerr")));
            builder.append(Component.text(" :3", NamedTextColor.GREEN));

            QuartersMessaging.sendComponent(sender, builder.build());
        });
    }

    public CompletableFuture<@Nullable String> getUsernameByUUIDAsync(@NotNull UUID uuid) {
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
