package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.command.quartersadmin.method.trust.AdminTrustAddMethod;
import au.lupine.quarters.command.quartersadmin.method.trust.AdminTrustClearMethod;
import au.lupine.quarters.command.quartersadmin.method.trust.AdminTrustRemoveMethod;
import au.lupine.quarters.object.base.CommandArgument;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminTrustArgument extends CommandArgument {

    public AdminTrustArgument() {
        super("trust", "quarters.command.quartersadmin.trust");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new AdminTrustAddMethod().build())
                .then(new AdminTrustClearMethod().build())
                .then(new AdminTrustRemoveMethod().build());
    }
}
