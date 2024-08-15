package net.earthmc.quarters.command.quarters.method;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.state.UserGroup;
import net.earthmc.quarters.util.RequestUtil;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
        Map<UUID, UserGroup> userGroups = ConfigManager.getUserGroups();

        List<CompletableFuture<Component>> futureNames = new ArrayList<>();
        for (Map.Entry<UUID, UserGroup> entry : userGroups.entrySet()) {
            UUID uuid = entry.getKey();
            UserGroup userGroup = entry.getValue();

            CompletableFuture<Component> future = getUsernameByUUIDAsync(uuid).thenApply(name -> {
                if (name == null) return null;

                TextComponent.Builder nameBuilder = Component.text();
                nameBuilder.append(Component.text(name, TextColor.color(userGroup.getColour().getRGB())));

                String description = userGroup.getDescription();
                if (description != null) nameBuilder.hoverEvent(Component.text(description, NamedTextColor.GRAY));

                return nameBuilder.build();
            });

            futureNames.add(future);
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

            URL url;
            try {
                url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            } catch (MalformedURLException e) {
                return null;
            }

            JsonObject jsonObject;
            try {
                jsonObject = RequestUtil.getUrlAsJsonObject(url);
            } catch (IOException e) {
                return null;
            }

            if (jsonObject == null) return null;

            JsonElement nameElement = jsonObject.get("name");
            if (nameElement == null) return null;

            String name = nameElement.getAsString();
            CACHED_NAMES.put(uuid, name);

            return name;
        });
    }
}
