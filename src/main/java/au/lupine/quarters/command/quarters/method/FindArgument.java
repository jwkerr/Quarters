package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.find.ForsaleMethod;
import au.lupine.quarters.object.base.CommandArgument;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public class FindArgument extends CommandArgument {

    public FindArgument() {
        super("find", "quarters.command.quarters.find");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new ForsaleMethod().build());
    }
}
