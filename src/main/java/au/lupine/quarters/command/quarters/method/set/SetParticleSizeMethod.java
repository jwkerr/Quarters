package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SetParticleSizeMethod extends CommandMethod {

    public SetParticleSizeMethod() {
        super("particlesize", "quarters.command.quarters.set.particlesize");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("size", FloatArgumentType.floatArg(0.0F, 4.0F))
                        .suggests((context, builder) -> suggestStrings(builder, "1.0"))
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("size", Float.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }

    private void execute(@NotNull CommandSourceStack source, float value) {
        Player player = getSenderAsPlayerOrThrow(source);

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        if (value < 0.0F || value > 4.0F) throw new CommandMethodException("quarters.command.quarters.set.particlesize.feedback.invalid_value");

        Quarter quarter = getQuarterAtPlayerOrNull(player);

        if (quarter != null && quarter.hasBasicCommandPermissions(player)) {
            quarter.setParticleSize(value);

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.particlesize.feedback.quarter_success", Argument.string("value", Float.toString(value)));
            QuartersMessaging.sendCommandFeedbackToTown(
                    quarter.getTown(),
                    player,
                    "quarters.command.quarters.set.particlesize.feedback.town",
                    player.getLocation(),
                    Argument.string("value", Float.toString(value))
            );
            return;
        }

        ResidentMetadataManager.getInstance().setParticleSize(resident, value);
        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.particlesize.feedback.default_success", Argument.string("value", Float.toString(value)));
    }
}
