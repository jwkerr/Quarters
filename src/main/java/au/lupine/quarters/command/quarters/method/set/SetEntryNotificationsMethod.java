package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class SetEntryNotificationsMethod extends CommandMethod {

    public SetEntryNotificationsMethod() {
        super("entrynotifications", "quarters.command.quarters.set.entrynotifications");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(EntryNotificationType.values()).map(EntryNotificationType::getLowerCase).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("type", String.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("No entry notification type provided");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String typeName) {
        Player player = getSenderAsPlayerOrThrow(source);

        EntryNotificationType type;
        try {
            type = EntryNotificationType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid entry notification type provided");
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager.getInstance().setEntryNotificationType(resident, type);

        QuartersMessaging.sendSuccessMessage(player, "Your entry notification type has been set to: " + type.getCommonName());
    }
}
