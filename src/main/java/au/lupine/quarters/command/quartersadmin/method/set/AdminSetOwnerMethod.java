package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
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

public final class AdminSetOwnerMethod extends CommandMethod {

    public AdminSetOwnerMethod() {
        super("owner", "quarters.command.quartersadmin.set.owner");
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
        throw new CommandMethodException("quarters.command.feedback.required_argument_missing");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String playerName) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Resident resident = TownyAPI.getInstance().getResident(playerName);
        if (resident == null || resident.isNPC()) throw new CommandMethodException("quarters.command.quarters.trust.feedback.player_not_found");

        if (!quarter.isEmbassy() && !quarter.getTown().hasResident(resident)) throw new CommandMethodException("quarters.command.quarters.set.owner.feedback.invalid_owner");

        quarter.setOwner(resident.getUUID());
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.owner.feedback.success", Argument.string("player", resident.getName()));
    }
}
