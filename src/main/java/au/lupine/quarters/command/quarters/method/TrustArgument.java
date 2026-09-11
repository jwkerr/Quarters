package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.trust.TrustAddMethod;
import au.lupine.quarters.command.quarters.method.trust.TrustClearMethod;
import au.lupine.quarters.command.quarters.method.trust.TrustRemoveMethod;
import au.lupine.quarters.object.base.CommandArgument;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class TrustArgument extends CommandArgument {

    public TrustArgument() {
        super("trust", "quarters.command.quarters.trust");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new TrustAddMethod().build())
                .then(new TrustClearMethod().build())
                .then(new TrustRemoveMethod().build());
    }

}
