package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.UserGroup;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class MeowMethod extends CommandMethod {

    private final List<Sound.Builder> catSounds = List.of(
            Sound.sound(Sound.sound(org.bukkit.Sound.ENTITY_CAT_AMBIENT, Sound.Source.AMBIENT, 0.5F, 0F)),
            Sound.sound(Sound.sound(org.bukkit.Sound.ENTITY_CAT_PURR, Sound.Source.AMBIENT, 0.4F, 0F)),
            Sound.sound(Sound.sound(org.bukkit.Sound.ENTITY_CAT_PURREOW, Sound.Source.AMBIENT, 0.5F, 0F))
    );

    public MeowMethod() {
        super("meow", "quarters.command.quarters.meow");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("quarter", StringArgumentType.word())
                        .suggests((context, builder) -> suggestQuarterUUIDs(builder))
                        .executes(context -> run(context.getSource(), context.getArgument("quarter", String.class))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack) {
        execute(stack, null);
    }

    private int run(@NotNull CommandSourceStack stack, @Nullable String argument) {
        try {
            execute(stack, argument);
            return Command.SINGLE_SUCCESS;
        } catch (CommandMethodException e) {
            QuartersMessaging.sendErrorMessage(stack.getSender(), e.getMessage(), e.getArguments());
            return 0;
        }
    }

    private void execute(@NotNull CommandSourceStack stack, @Nullable String argument) {
        Player player = getSenderAsPlayerOrThrow(stack);

        // TODO: Check if it's really needed to pass a quarter argument for this
        Quarter quarter = getQuarterAtPlayerOrByUUIDOrThrow(player, argument);

        UUID owner = quarter.getOwner();
        if (owner == null) return;

        UserGroup userGroup = ConfigManager.getUserGroupOrDefault(owner, ConfigManager.DEFAULT_USER_GROUP);
        if (!userGroup.hasCatMode()) return;

        Random random = new Random();

        float randomPitch = random.nextFloat(1.0F, 1.6F);
        Sound randomSound = catSounds.get(random.nextInt(catSounds.size())).pitch(randomPitch).build();

        player.playSound(randomSound, Sound.Emitter.self());
    }
}
