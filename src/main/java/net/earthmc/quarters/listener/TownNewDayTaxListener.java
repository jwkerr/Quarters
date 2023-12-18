package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.event.actions.TownyBuildEvent;
import com.palmergames.bukkit.towny.event.time.dailytaxes.NewDayTaxAndUpkeepPreCollectionEvent;
import com.palmergames.bukkit.towny.object.Resident;
import fr.xephi.authme.events.LoginEvent;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.List;
import java.util.UUID;

/**
 * @Description: 公寓税收
 * @ClassName: TownNewDayTaxEvent
 * @Author: ice_light
 * @Date: 2023/12/18 14:59
 * @Version: 1.0
 */
public class TownNewDayTaxListener implements Listener {
    @EventHandler
    public void onLogin(LoginEvent event) {
        event.getPlayer().performCommand("mail read");// 玩家登陆后读取自己的邮件
//        event.getPlayer().performCommand("/mail read");// 玩家登陆后读取自己的邮件
//        QuartersMessaging.sendSuccessMessage(event.getPlayer(), "成功测试");
    }
    @EventHandler
    public void onTax(NewDayTaxAndUpkeepPreCollectionEvent event) {
        List<Quarter> allQuarters = QuarterUtil.getAllQuarters();
        //对每个被租用的公寓收取一定租金
        for (Quarter quarter : allQuarters) {
            if(quarter.getOwner()!=null) collectTax(quarter.getOwner(),quarter);
        }
    }

    private void collectTax(UUID uuid,Quarter quarter) {
        Player p = Bukkit.getPlayer(uuid);
        Resident resident = TownyAPI.getInstance().getResident(p);
        // 如果玩家有钱，直接扣租金，并更新下次的租金
        assert resident != null;
        assert p != null;
        if (resident.getAccount().getHoldingBalance() >= quarter.getNextTax()) {
            resident.getAccount().withdraw(quarter.getNextTax() + quarter.getOverdueTax(), "为公寓支付税收 " + quarter.getUUID());
            quarter.getTown().getAccount().deposit(quarter.getNextTax() + quarter.getOverdueTax(), "为公寓支付税收 " + quarter.getUUID());
            quarter.setOverdueday(0);
            quarter.setOverdueTax((double) 0);
            // 更新下次税收，逾期租金，逾期天数，并给玩家留言，判断玩家是否在线

            if(p.isOnline()) QuartersMessaging.sendSuccessMessage(p, "已自动为您扣除今天的公寓租金" + quarter.getNextTax());
            else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mail " + p.getName() + " 已自动为您扣除公寓租金" + quarter.getNextTax());
            double nexttax = quarter.getNextTax() + 50;
            if(nexttax<quarter.getLastPrice()*2)quarter.setNextTax(nexttax);
        }
        // 如果玩家没钱，先加记租金，更新逾期日期，并在玩家下次上线前提醒
        else{
            // 如果未逾期
            if(quarter.getOverdueday()<=15){
                // 更新逾期日期，逾期租金，并在下一次有钱支付时一并支付
                quarter.setOverdueday(quarter.getOverdueday()+1);
                quarter.setOverdueTax(quarter.getOverdueTax()+quarter.getNextTax());
                double nexttax = quarter.getNextTax() + 50;
                if(nexttax<quarter.getLastPrice()*2)quarter.setNextTax(nexttax);
                if(p.isOnline()) QuartersMessaging.sendSuccessMessage(p, "您已欠租金：" + quarter.getOverdueTax() +"，共计逾期:" + quarter.getOverdueday()+"天/15，如不能在15日内还清，您将被从公寓清退");
                else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mail " + p.getName() + " 您已欠租金：" + quarter.getOverdueTax() +"，共计逾期:" + quarter.getOverdueday()+"天/15，如不能在15日内还清，您将被从公寓清退" );
            }
            //如果已逾期,则清退玩家
            else{
                quarter.setOwner(null);
                quarter.setClaimedAt(null);
                quarter.setOverdueday(0);
                quarter.setOverdueTax((double) 0);

                quarter.setPrice(quarter.getLastPrice());
                quarter.save();
                if(p.isOnline()) QuartersMessaging.sendSuccessMessage(p, "您已欠租金：" + quarter.getOverdueTax() +"，共计逾期:" + quarter.getOverdueday()+"天/15，现已将您从公寓清退");
                else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mail " + p.getName() + " 您已欠租金：" + quarter.getOverdueTax() +"，共计逾期:" + quarter.getOverdueday()+"天/15，，现已将您从公寓清退");

            }
        }
    }
}
