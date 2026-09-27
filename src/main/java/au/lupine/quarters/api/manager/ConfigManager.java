package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.object.state.EntryNotificationType;
import au.lupine.quarters.object.state.FlagType;
import au.lupine.quarters.object.wrapper.UserGroup;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import de.bsommerfeld.jshepherd.annotation.Comment;
import de.bsommerfeld.jshepherd.annotation.Key;
import de.bsommerfeld.jshepherd.annotation.Section;
import de.bsommerfeld.jshepherd.core.ConfigurablePojo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Comment("If comments are not present, please restart your server")
public class ConfigManager extends ConfigurablePojo<ConfigManager> {

    private static final String USER_GROUPS_URL = "https://raw.githubusercontent.com/jwkerr/Quarters/master/src/main/resources/user_groups.json";

    public static final UserGroup DEFAULT_USER_GROUP = new UserGroup();
    private static final List<UserGroup> USER_GROUPS = new ArrayList<>();

    @Section("technical")
    public @NotNull TechnicalSection technical = new TechnicalSection();

    @Section("wand")
    public @NotNull WandSection wand = new WandSection();

    @Section("quarters")
    public @NotNull QuartersSection quarters = new QuartersSection();

    @Section("renderer")
    public @NotNull RendererSection renderer = new RendererSection();

    private static @NotNull Map<FlagType, Boolean> createAllowedFlags() {
        Map<FlagType, Boolean> flags = new LinkedHashMap<>();
        flags.put(FlagType.MOBS, true);
        flags.put(FlagType.EXPLOSIONS, true);
        flags.put(FlagType.PVP, false);
        flags.put(FlagType.FIRE, true);
        flags.put(FlagType.SNOW, true);
        flags.put(FlagType.ICE, true);
        return flags;
    }

    private static @NotNull Map<FlagType, Boolean> createDefaultFlags() {
        Map<FlagType, Boolean> flags = new LinkedHashMap<>();
        flags.put(FlagType.MOBS, false);
        flags.put(FlagType.EXPLOSIONS, false);
        flags.put(FlagType.PVP, false);
        flags.put(FlagType.FIRE, false);
        flags.put(FlagType.SNOW, false);
        flags.put(FlagType.ICE, false);
        return flags;
    }

    public static class TechnicalSection {
        @Key("can_plugin_request_user_groups")
        @Comment("If set to true, the plugin will be allowed to query GitHub for the latest sponsor data to correctly format names")
        public boolean canPluginRequestUserGroups = true;

        @Key("mayor_bypasses_certain_elevated_perms")
        @Comment({
                "If this is set to true, mayors will bypass perms for certain command such as /q create, /q evict etc",
                "This is intended to make configuration easier as most servers will want this behaviour"
        })
        public boolean doMayorsBypassCertainElevatedPerms = true;
    }

    public static class WandSection {
        @Key("material")
        @Comment("Material of the wand item")
        public @NotNull Material material = Material.FLINT;

        @Key("free_amount")
        @Comment({
                "Amount of free wands a player can receive in total when using /q wand",
                "- -1 for no limit",
                "- 0 for no free wands"
        })
        public int freeAmount = 1;

        @Key("cooldown_seconds")
        @Comment({
                "Cooldown in seconds between uses of /q wand",
                "- 0 for no cooldown"
        })
        public int cooldownSeconds = 300;
    }

    public static class QuartersSection {
        @Key("max_quarter_volume")
        @Comment({
                "Maximum block volume of all cuboids in a quarter combined",
                "- -1 for no limit"
        })
        public int maxQuarterVolume = -1;

        @Key("max_quarters_per_town")
        @Comment({
                "Maximum amount of quarters that can be in a single town",
                "- -1 for no limit"
        })
        public int maxQuartersPerTown = -1;

        @Key("max_cuboid_volume")
        @Comment({
                "Maximum block volume of individual cuboids",
                "- -1 for no limit"
        })
        public int maxCuboidVolume = -1;

        @Key("max_cuboids_per_quarter")
        @Comment({
                "Maximum amount of cuboids that can be in each quarter",
                "- -1 for no limit"
        })
        public int maxCuboidsPerQuarter = -1;

        @Section("default_quarter_colour")
        public @NotNull QuarterColour defaultQuarterColour = new QuarterColour();

        @Section("pvp_settings")
        public @NotNull PvpSettings pvpSettings = new PvpSettings();

        @Key("allow_quarter_entry_notifications")
        @Comment("If set to true, players will be allowed to toggle notifications when entering a quarter")
        public boolean allowQuarterEntryNotifications = true;

        @Key("quarter_entry_notifications_on_by_default")
        @Comment("If set to false, players will have to opt in to quarter entry notifications")
        public boolean quarterEntryNotificationsOnByDefault = true;

        @Key("default_quarter_entry_notification_type")
        @Comment("Configure this to change the default quarter entry notification type")
        public @NotNull EntryNotificationType defaultQuarterEntryNotificationType = EntryNotificationType.ACTION_BAR;

        @Key("name_adjectives")
        @Comment("Adjectives used when generating a random quarter name")
        public @NotNull List<String> nameAdjectives = List.of(
                "Lovely", "Cheerful", "Upbeat", "Stylish", "Luxurious", "Elegant", "Inviting", "Welcoming",
                "Annoying", "Perturbing", "Enraging", "Dingy", "Inconvenient", "Dull", "Bland", "Gloomy"
        );

        @Key("name_nouns")
        @Comment("Nouns used when generating a random quarter name")
        public @NotNull List<String> nameNouns = List.of("Quarter", "Apartment", "Flat", "Dwelling", "Residence", "Suite", "Property", "Tenement");

        @Key("allowed_flags")
        @Comment({
                "Which flags are allowed to be used and functional",
                "If a flag is set to true, it will be functional",
                "When false, that flag cannot be enabled",
                "Quarters that already have the flag will show false in the '/q here' menu and it will not work"
        })
        public @NotNull Map<FlagType, Boolean> allowedFlags = createAllowedFlags();

        @Key("default_flags")
        @Comment({
                "The flags a quarter gets when it's created",
                "This will not affect already existing quarters"
        })
        public @NotNull Map<FlagType, Boolean> defaultFlags = createDefaultFlags();
    }

    public static class QuarterColour {
        @Key("enabled")
        @Comment({
                "Enable to give quarters a colour by default",
                "Configure the colour values below"
        })
        public boolean enabled = false;

        @Key("red")
        public int red = 63;

        @Key("green")
        public int green = 180;

        @Key("blue")
        public int blue = 255;
    }

    public static class PvpSettings {
        @Key("show_exempt_message")
        @Comment("If set to true, the attacker will receive a translated message when the victim is exempt from PvP damage")
        public boolean showExemptMessage = true;

        @Key("visible_boundary")
        @Comment("If set to true, PvP quarters will have a forced visible boundary that cannot be disabled or hidden")
        public boolean visibleBoundary = true;

        @Key("visible_glow")
        @Comment({
                "If set to true, PvP quarters will have a forced visible glow that cannot be disabled",
                "A glow allows players to see quarters through walls",
                "This can help with quickly identifying where traps are"
        })
        public boolean visibleGlow = true;

        @Section("pvp_quarter_colour")
        @Comment("The colour all PvP quarters will use for boundaries and menus when enabled")
        public @NotNull PvpQuarterColour pvpQuarterColour = new PvpQuarterColour();

        @Key("entry_grace_period_seconds")
        @Comment({
                "Number of seconds after entering a PvP quarter before players can take PvP damage",
                "Useful in case a player entered a PvP quarter by accident",
                "Set to 0 to disable the grace period",
                "If you want to make players fully exempt from PvP quarters, give the 'quarters.exempt_from_pvp' permission"
        })
        public int entryGracePeriodSeconds = 0;

        @Key("show_entry_grace_period_enter_message")
        @Comment("If set to true, players will receive a translated message when their PvP grace period starts")
        public boolean showEntryGracePeriodEnterMessage = false;

        @Key("show_entry_grace_period_complete_message")
        @Comment("If set to true, players will receive a translated message when their PvP grace period expires")
        public boolean showEntryGracePeriodCompleteMessage = false;

        @Key("item_drops_on_death")
        @Comment({
                "If set to true, items should drop on death",
                "Set to false for keep inventory"
        })
        public boolean itemsDropsOnDeath = true;

        @Key("exp_drops_on_death")
        @Comment("If set to true, exp should drop on death")
        public boolean expDropsOnDeath = true;

        @Key("friendly_fire_town")
        @Comment("If set to true, players from the same town can damage each other")
        public boolean friendlyFireTown = false;

        @Key("friendly_fire_nation")
        @Comment("If set to true, players from the same nation can damage each other")
        public boolean friendlyFireNation = true;

        @Key("friendly_fire_mcmmo_party")
        @Comment({
                "If set to true, players from the same McMMO party can damage each other",
                "The McMMO plugin is required on the server to make this config work"
        })
        public boolean friendlyFireMcmmoParty = true;
    }

    public static class PvpQuarterColour {
        @Key("enabled")
        @Comment({
                "Enable to give quarters a colour by default",
                "Configure the colour values below"
        })
        public boolean enabled = true;

        @Key("red")
        public int red = 255;

        @Key("green")
        public int green = 0;

        @Key("blue")
        public int blue = 0;
    }

    public static class RendererSection {
        @Key("enabled")
        @Comment("Set to false to completely disable visual outlines around cuboids")
        public boolean enabled = true;

        @Key("ticks_between_outline_updates")
        @Comment("The number of ticks between visual outline updates")
        public int ticksBetweenOutlineUpdates = 5;

        @Key("max_distance_for_outlines")
        @Comment("The maximum distance a player can be from a cuboid before outline entities stop being sent to their client")
        public int maxDistanceForOutlines = 48;
    }

    public void loadRuntimeData() {
        loadUserGroups();
    }

    private void loadUserGroups() {
        if (technical.canPluginRequestUserGroups) {
            Quarters.logInfo("Requesting user_groups.json from " + USER_GROUPS_URL + " thank you for keeping this setting enabled!");

            loadUserGroupsFromWeb().thenAccept(jsonArray -> {
                if (jsonArray == null) {
                    Quarters.logWarning("An error occurred while requesting user_groups.json, defaulting to jar resources");
                    jsonArray = loadUserGroupsFromResources();
                }

                parseUserGroups(jsonArray);
            });
        } else {
            parseUserGroups(loadUserGroupsFromResources());
        }
    }

    private void parseUserGroups(@Nullable JsonArray jsonArray) {
        if (jsonArray == null) return;

        USER_GROUPS.clear();

        for (JsonElement element : jsonArray) {
            USER_GROUPS.add(new UserGroup(element.getAsJsonObject()));
        }

        Collections.shuffle(USER_GROUPS);
    }

    private CompletableFuture<@Nullable JsonArray> loadUserGroupsFromWeb() {
        return CompletableFuture.supplyAsync(() -> JSONManager.getInstance().getUrlAsJsonElement(USER_GROUPS_URL, JsonArray.class));
    }

    private @Nullable JsonArray loadUserGroupsFromResources() {
        InputStream inputStream = Quarters.getInstance().getResource("user_groups.json");
        if (inputStream == null) return null;

        InputStreamReader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(reader, JsonArray.class);
    }

    public static UserGroup getUserGroupOrDefault(UUID uuid, UserGroup def) {
        for (UserGroup userGroup : USER_GROUPS) {
            if (userGroup.getMembers().contains(uuid)) return userGroup;
        }

        return def;
    }

    public static List<UserGroup> getUserGroups() {
        return USER_GROUPS;
    }

    /**
     * @param uuid UUID of the player you would like to get a formatted name of
     * @param def A default if the UUID doesn't resolve to a player, can be null if you know for a fact the player exists
     * @return A formatted name that can be clicked for /res and has a colour and hover if applicable
     */
    public static Component getFormattedName(@Nullable UUID uuid, @Nullable Component def) {
        if (uuid == null) return def;

        String name;
        final Resident resident = TownyAPI.getInstance().getResident(uuid);
        if (resident != null) {
            name = resident.getName();
        } else {
            name = Bukkit.getOfflinePlayer(uuid).getName();
        }

        if (name == null) return def;

        UserGroup userGroup = getUserGroupOrDefault(uuid, DEFAULT_USER_GROUP);

        return userGroup.formatString(name)
                .clickEvent(ClickEvent.runCommand("/towny:resident " + name));
    }
}
