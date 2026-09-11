package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AdminPortMethod extends CommandMethod {

    private boolean consoleOutput = false;
    private int numFailures = 0;
    private int successfulPorts = 0;

    public AdminPortMethod() {
        super("port", "quarters.command.quartersadmin.port");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("consoleOutput", BoolArgumentType.bool())
                        .suggests((context, builder) -> suggestStrings(builder, "true", "false"))
                        .executes(context -> run(context.getSource(), () -> execute(context.getArgument("consoleOutput", Boolean.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        execute(false);
    }

    private void execute(boolean consoleOutput) {
        this.consoleOutput = consoleOutput;
        numFailures = 0;
        successfulPorts = 0;

        List<Town> towns = TownyAPI.getInstance().getTowns();
        if (this.consoleOutput) Quarters.logInfo("Porting legacy quarters of " + towns.size() + " towns");

        for (Town town : towns) {
            portLegacyQuarters(town);
        }

        if (this.consoleOutput) Quarters.logInfo("Porting has completed, " + successfulPorts + "/" + towns.size() + " towns had quarters ported, " + numFailures + " towns had quarters that were unsuccessfully ported, view log for errors if any failed");
    }

    private void portLegacyQuarters(Town town) {
        String townName = town.getName();
        if (consoleOutput) Quarters.logInfo("Porting " + townName);

        CustomDataField<?> cdf = town.getMetadata("quarters_qldf");
        if (cdf == null) {
            if (consoleOutput) Quarters.logInfo(townName + " had no quarters to port");
            return;
        }

        String value = (String) cdf.getValue();
        if (value == null) {
            if (consoleOutput) Quarters.logWarning("Value of " + townName + "'s legacy quarters was null, continuing to the next town");
            return;
        }

        String[] split = value.split("\\|");

        List<Quarter> quarters = new CopyOnWriteArrayList<>();
        for (String quarterString : split) {
            try {
                Quarter quarter = getPortedQuarter(quarterString);
                quarters.add(quarter);
            } catch (Exception e) {
                numFailures += 1;
                if (consoleOutput) Quarters.logWarning("There was an error while porting the quarters of " + townName + " continuing to the next town\n" + e);
            }
        }

        if (quarters.isEmpty()) return;

        QuarterManager.getInstance().setQuarters(town, quarters);

        if (consoleOutput) Quarters.logInfo("Successfully ported the quarters of " + townName);
        successfulPorts += 1;

        town.removeMetaData("quarters_qldf");
    }

    private Quarter getPortedQuarter(String value) {
        String[] split = value.split(",");

        Town town = TownyAPI.getInstance().getTown(UUID.fromString(split[2]));
        List<Cuboid> cuboids = deserialiseCuboids(split[0]);
        UUID uuid = UUID.fromString(split[1]);
        long registered = Long.parseLong(split[8]);

        UUID owner;
        try {
            owner = UUID.fromString(split[3]);
        } catch (IllegalArgumentException e) {
            owner = null;
        }

        String trustedValue = split[4];
        List<UUID> trusted = trustedValue.equals("null") ? new ArrayList<>() : deserialiseTrusted(trustedValue);

        String priceValue = split[5];
        Double price = priceValue.equals("null") ? null : Double.parseDouble(priceValue);

        au.lupine.quarters.object.state.QuarterType type;
        try {
            type = au.lupine.quarters.object.state.QuarterType.valueOf(split[6].toUpperCase());
        } catch (IllegalArgumentException e) {
            type = au.lupine.quarters.object.state.QuarterType.APARTMENT;
        }

        boolean isEmbassy = Boolean.parseBoolean(split[7]);

        String claimedAtValue = split[9];
        Long claimedAt = claimedAtValue.equals("null") ? null : Long.parseLong(claimedAtValue);

        String[] colourSplit = split[10].split("\\+");
        int r = Integer.parseInt(colourSplit[0]);
        int g = Integer.parseInt(colourSplit[1]);
        int b = Integer.parseInt(colourSplit[2]);
        Color colour = new Color(r, g, b);

        return new Quarter(town, cuboids, uuid, registered, owner, trusted, price, type, isEmbassy, claimedAt, colour);
    }

    private List<Cuboid> deserialiseCuboids(String value) {
        List<Cuboid> cuboids = new ArrayList<>();

        String[] split = value.split(":");
        for (String cuboidString : split) {
            String[] locationSplit = cuboidString.split(";");

            Location cornerOne = deserialiseLocation(locationSplit[0]);
            Location cornerTwo = deserialiseLocation(locationSplit[1]);
            cuboids.add(new Cuboid(cornerOne, cornerTwo));
        }

        return cuboids;
    }

    private Location deserialiseLocation(String value) {
        String[] split = value.split("\\+");
        return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    private List<UUID> deserialiseTrusted(String value) {
        String[] split = value.split("\\+");

        List<UUID> trusted = new ArrayList<>();
        for (String uuid : split) {
            trusted.add(UUID.fromString(uuid));
        }

        return trusted;
    }
}
