package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
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
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quarters.legacy_method.set.SetEntryNotificationsMethod(context.getSource().getSender(), new String[]{context.getArgument("type", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.set.SetEntryNotificationsMethod(source.getSender(), new String[0]).execute();
    }
}
