package au.lupine.quarters.command.quartersadmin.method.set;

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
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String playerName) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Resident resident = TownyAPI.getInstance().getResident(playerName);
        if (resident == null || resident.isNPC()) throw new CommandMethodException(StringConstants.SPECIFIED_PLAYER_DOES_NOT_EXIST);

        if (!quarter.isEmbassy() && !quarter.getTown().hasResident(resident)) throw new CommandMethodException(StringConstants.SPECIFIED_PLAYER_COULD_NOT_BE_SET_AS_OWNER);

        quarter.setOwner(resident.getUUID());
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.owner.feedback.success", Argument.string("player", resident.getName()));
    }
}
