package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@CommandAlias("quarters|q")
public class AutoClaimCommand extends BaseCommand {
    @Subcommand("auto")
    @Description("Auto Claim a quarter")
    @CommandPermission("quarters.command.quarters.auto")
    public void onAutoClaim(Player player) {

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;
        if(QuarterUtil.playerHasQuarters(player)!=null){
            QuartersMessaging.sendErrorMessage(player, "您已经拥有一个公寓了，输入/q home 回到公寓，输入/q unclaim退租之前的公寓，退租记得搬走个人物品噢");
            return;
        }
        if (resident.getTownOrNull()!=null) {
            QuartersMessaging.sendErrorMessage(player, "你不能租用公寓，因为你不属于任何一个城镇");
            return;
        }



        sendRandomClaimConfirmation(resident);
    }

    private void sendRandomClaimConfirmation(Resident resident) {
        List<Quarter> allClaimableQuartersInPlayerTown = QuarterUtil.getAllClaimableQuartersInPlayerTown(resident.getPlayer());
        Quarter quarter = null;
        for (Quarter q : allClaimableQuartersInPlayerTown) {
            if(q.getOwner()==null) quarter = q;
        }
        if(quarter == null){
            QuartersMessaging.sendErrorMessage(resident.getPlayer(), "你不能租用公寓，因为当前城镇公寓已住满，或没有公寓区，您可以前往其他城镇或自建城镇");
            return;
        }
        double currentPrice = quarter.getPrice();
        Player player = resident.getPlayer();
        player.teleportAsync(quarter.getCuboids().get(0).getCentorLocation());
        if (TownyEconomyHandler.isActive() && resident.getAccount().getHoldingBalance() < quarter.getPrice()) {
            QuartersMessaging.sendErrorMessage(player, "你没有足够的资金来租用这个公寓，该公寓租金为：" + quarter.getPrice());
            return;
        }
        if (currentPrice > 0) {
            Quarter finalQuarter = quarter;
            Confirmation.runOnAccept(() -> {
                if (finalQuarter.getPrice() != currentPrice) {
                    QuartersMessaging.sendErrorMessage(player, "Quarter purchase cancelled as the quarter's price has changed");
                    return;
                }

                resident.getAccount().withdraw(finalQuarter.getPrice(), "为公寓支付 " + finalQuarter.getUUID());
                finalQuarter.getTown().getAccount().deposit(finalQuarter.getPrice(), "为公寓支付 " + finalQuarter.getUUID());

                setAndSaveQuarter(finalQuarter, resident);

                QuartersMessaging.sendSuccessMessage(player, "您现在租用这个公寓了");

                Location location = player.getLocation();
                QuartersMessaging.sendInfoMessageToTown(finalQuarter.getTown(), player, player.getName() + " 租用了一个公寓 " + QuartersMessaging.getLocationString(location));
            })
            .setTitle("租用这个公寓要花" + quarter.getPrice() + ", 您确定吗？如果确定请输入/confirm")
            .sendTo(resident.getPlayer());
        } else {
            setAndSaveQuarter(quarter, resident);

            QuartersMessaging.sendSuccessMessage(player, "您现在租用这个公寓了");

            Location location = player.getLocation();
            QuartersMessaging.sendInfoMessageToTown(quarter.getTown(), player, player.getName() + " 租用了一个公寓 " + QuartersMessaging.getLocationString(location));
        }
    }

    private void setAndSaveQuarter(Quarter quarter, Resident resident) {
        quarter.setOwner(resident.getUUID());
        quarter.setClaimedAt(Instant.now().toEpochMilli());
        quarter.setOverdueday(0);
        quarter.setOverdueTax((double) 0);
        quarter.setPrice(null);
        quarter.save();
    }
}
