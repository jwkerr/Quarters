package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.object.state.EntryNotificationType;
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
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

    @Section("particles")
    public @NotNull ParticlesSection particles = new ParticlesSection();

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

        @Section("arena_quarter")
        public @NotNull ArenaQuarter arenaQuarter = new ArenaQuarter();

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

    public static class ArenaQuarter {
        @Key("enabled")
        @Comment({
                "Enable to make arena quarters functional",
                "When false, arena quarters work like regular quarters",
                "WARNING: Players may exploit this by making invisible traps",
                "Enable on own risk!"
        })
        public boolean enabled = false;

        @Key("visible_boundary")
        @Comment("If set to true, arena quarters will have a forced visible boundary that cannot be disabled")
        public boolean visibleBoundary = true;

        @Key("entry_grace_period_seconds")
        @Comment({
                "Number of seconds after entering an arena before players can take PvP damage",
                "Useful in case a player entered an arena plot by accident"
        })
        public int entryGracePeriodSeconds = 3;

        @Key("item_drops_on_death")
        @Comment("If set to true, items will drop on death")
        public boolean itemsDropsOnDeath = true;

        @Key("exp_drops_on_death")
        @Comment("If set to true, exp will drop on death")
        public boolean expDropsOnDeath = true;

        @Key("friendly_fire_town")
        @Comment("If set to true, players from the same town can damage each other")
        public boolean friendlyFireTown = false;

        @Key("friendly_fire_nation")
        @Comment("If set to true, players from the same nation can damage each other")
        public boolean friendlyFireNation = true;

        // TODO: Possibly hook into McMMO to also check for party friendly fire?
    }

    public static class ParticlesSection {
        @Key("enabled")
        @Comment("Set to false to completely disable particle outlines around cuboids")
        public boolean enabled = true;

        @Key("current_selection_particle")
        @Comment("Particle outline of the currently selected area")
        public @NotNull Particle currentSelectionParticle = Particle.SCRAPE;

        @Key("current_cuboids_particle")
        @Comment("Particle outline of current cuboids added to the selection")
        public @NotNull Particle currentCuboidsParticle = Particle.WAX_OFF;

        @Key("ticks_between_particle_outlines")
        @Comment("The number of ticks between particle outline updates")
        public int ticksBetweenParticleOutlines = 5;

        @Key("max_distance_for_cuboid_particles")
        @Comment("The maximum distance a player can be from a cuboid before outline particles stop being sent to their client")
        public int maxDistanceForCuboidParticles = 48;

        @Key("default_particle_size")
        @Comment("Sets the default size for particles of quarters that have been made")
        public float defaultParticleSize = 1F;

        @Key("allow_constant_particle_outlines")
        @Comment("If set to true, players will be able to toggle quarter outlines to display constantly")
        public boolean allowConstantParticleOutlines = true;

        @Key("constant_particle_outlines_on_by_default")
        @Comment("If set to false, players will have to opt in to constant particle outlines")
        public boolean constantParticleOutlinesOnByDefault = true;

        @Key("allow_entry_particle_blinking")
        @Comment("If set to true, players will be able to toggle quarter outlines to blink when entered")
        public boolean allowEntryParticleBlinking = true;

        @Key("entry_particle_blinking_on_by_default")
        @Comment({
                "If set to true, quarters will blink their particles for one tick upon entry by a player",
                "This can be a good alternative to constant particle outlines if they are causing lag"
        })
        public boolean entryParticleBlinkingOnByDefault = false;
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
