package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.set.SetAnchorMethod;
import au.lupine.quarters.command.quarters.method.set.SetColourMethod;
import au.lupine.quarters.command.quarters.method.set.SetDefaultSellPriceMethod;
import au.lupine.quarters.command.quarters.method.set.SetEntryNotificationsMethod;
import au.lupine.quarters.command.quarters.method.set.SetNameMethod;
import au.lupine.quarters.command.quarters.method.set.SetOwnerMethod;
import au.lupine.quarters.command.quarters.method.set.SetParticleSizeMethod;
import au.lupine.quarters.command.quarters.method.set.SetPermMethod;
import au.lupine.quarters.command.quarters.method.set.SetTypeMethod;
import au.lupine.quarters.object.base.CommandArgument;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class SetArgument extends CommandArgument {

    public SetArgument() {
        super("set", "quarters.command.quarters.set");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new SetAnchorMethod().build())
                .then(new SetColourMethod().build())
                .then(new SetDefaultSellPriceMethod().build())
                .then(new SetEntryNotificationsMethod().build())
                .then(new SetNameMethod().build())
                .then(new SetOwnerMethod().build())
                .then(new SetParticleSizeMethod().build())
                .then(new SetPermMethod().build())
                .then(new SetTypeMethod().build());
    }
}
