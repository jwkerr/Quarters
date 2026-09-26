package au.lupine.quarters.command.quarters.method.trust;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class TrustAddMethod extends CommandMethod {

    public TrustAddMethod() {
        super("add", "quarters.command.quarters.trust.add");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("player", String.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("quarters.command.feedback.no_player_name");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String targetResidentName) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        Resident resident = TownyAPI.getInstance().getResident(targetResidentName);
        if (resident == null || resident.isNPC()) throw new CommandMethodException(StringConstants.SPECIFIED_PLAYER_DOES_NOT_EXIST);

        UUID uuid = resident.getUUID();
        List<UUID> trusted = quarter.getTrusted();
        if (!trusted.contains(uuid)) {
            trusted.add(resident.getUUID());

            quarter.setTrusted(trusted);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, StringConstants.SPECIFIED_PLAYER_HAS_BEEN_ADDED_TO_THIS_QUARTERS_TRUSTED_LIST);
            QuartersMessaging.sendCommandFeedbackToTown(
                    quarter.getTown(),
                    player,
                    "quarters.command.quarters.trust.add.feedback.town",
                    player.getLocation(),
                    Argument.string("player", resident.getName())
            );
            if (resident.getPlayer() != null) QuartersMessaging.sendInfoMessage(resident.getPlayer(), "quarters.command.quarters.trust.add.feedback.target", player.getLocation());
        } else {
            QuartersMessaging.sendErrorMessage(player, StringConstants.SPECIFIED_PLAYER_IS_ALREADY_TRUSTED_IN_THIS_QUARTER);
        }
    }
}
