package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MeowMethod extends CommandMethod {

    private final List<Sound.Builder> catSounds = List.of(
            Sound.sound(Sound.sound(org.bukkit.Sound.ENTITY_CAT_AMBIENT, Sound.Source.AMBIENT, 0.5F, 0F)),
            Sound.sound(Sound.sound(org.bukkit.Sound.ENTITY_CAT_PURR, Sound.Source.AMBIENT, 0.4F, 0F)),
            Sound.sound(Sound.sound(org.bukkit.Sound.ENTITY_CAT_PURREOW, Sound.Source.AMBIENT, 0.5F, 0F))
    );

    public MeowMethod(CommandSender sender, String[] args) {
        super(sender, args, null);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Quarter quarter = getQuarterAtPlayerOrByUUIDOrThrow(player, getArgOrNull(0));

        UUID owner = quarter.getOwner();
        if (owner == null || !ConfigManager.getUserGroup(owner).hasCatMode()) return;

        Random random = new Random();

        float randomPitch = random.nextFloat(1.0F, 1.6F);
        Sound randomSound = catSounds.get(random.nextInt(catSounds.size())).pitch(randomPitch).build();

        player.playSound(randomSound, Sound.Emitter.self());
    }
}
