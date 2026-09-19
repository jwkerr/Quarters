package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.toggle.ToggleConstantOutlinesMethod;
import au.lupine.quarters.command.quarters.method.toggle.ToggleEmbassyMethod;
import au.lupine.quarters.command.quarters.method.toggle.ToggleEntryBlinkingMethod;
import au.lupine.quarters.command.quarters.method.toggle.ToggleEntryNotificationsMethod;
import au.lupine.quarters.command.quarters.method.toggle.ToggleSellOnDeleteMethod;
import au.lupine.quarters.object.base.CommandArgument;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class ToggleArgument extends CommandArgument {

    public ToggleArgument() {
        super("toggle", "quarters.command.quarters.toggle");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new ToggleConstantOutlinesMethod().build())
                .then(new ToggleEmbassyMethod().build())
                .then(new ToggleEntryBlinkingMethod().build())
                .then(new ToggleEntryNotificationsMethod().build())
                .then(new ToggleSellOnDeleteMethod().build());
    }

}
