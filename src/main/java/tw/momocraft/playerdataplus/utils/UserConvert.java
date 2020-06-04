package tw.momocraft.playerdataplus.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.economy.CMIEconomy;
import me.dablakbandit.bank.api.BankAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PlayerHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.UUID;

public class UserConvert {

    /**
     * @param name1 the old name of player.
     * @param name2 the new name of player.
     */
    private void convertManager(String name1, String name2, boolean replace) {
        UUID uuid1 = PlayerHandler.getOfflineUUID(name1);
        UUID uuid2 = PlayerHandler.getOfflineUUID(name2);

        convertMoney(name1, name2, uuid1, uuid2, replace);
        convertPoints(uuid1, uuid2, replace);

        convertPlayerdata(name1, name2);
        convertCMI(name1, name2);
        convertPoints(uuid1, uuid2, replace);

    }


    private void convertMoney(String name1, String name2, UUID uuid1, UUID uuid2, boolean replace) {
        if (ConfigHandler.getDepends().CMIEnabled()) {
            CMIEconomy economy = new CMIEconomy();
            economy.transfer(name1, name2, economy.getBalance(name1));
        } else if (ConfigHandler.getDepends().getVault().vaultEnabled() && ConfigHandler.getDepends().getVault().getEconomy().isEnabled()) {
            /*
            Economy economy = ConfigHandler.getDepends().getVault().getEconomy();
            if (replace) {
                economy.depositPlayer()
                economy.bankWithdraw();
            } else {
                playerPointsAPI.givePoints(uuid2, playerPointsAPI.getPoints(uuid1));
            }
             */
        }
    }


    private void convertPoints(UUID uuid1, UUID uuid2, boolean replace) {
        if (ConfigHandler.getDepends().PlayerPointsEnabled()) {
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI();
            if (replace) {
                playerPointsAPI.setPoints(uuid2, playerPointsAPI.getPoints(uuid1));
            } else {
                playerPointsAPI.givePoints(uuid2, playerPointsAPI.getPoints(uuid1));
            }
        }
    }

    private void convertPlayerdata(String name1, String name2) {

    }

    private void convertCMI(String name1, String name2) {
        if (ConfigHandler.getDepends().CMIEnabled()) {
            CMIUser user1;
            CMIUser user2;
            try {
                user1 = CMI.getInstance().getPlayerManager().getUser(name1);
            } catch (Exception e) {
                ServerHandler.sendConsoleMessage("&cCan not find CMI user \"" + name1 + "\"!");
                return;
            }
            try {
                user2 = CMI.getInstance().getPlayerManager().getUser(name2);
            } catch (Exception e) {
                ServerHandler.sendConsoleMessage("&cCan not find CMI user \"" + name2 + "\"!");
                return;
            }
            boolean nick = true;
            if (nick) {
                try {
                    user2.setName(user1.getNickName());
                } catch (Exception e) {

                }
            }
        }
    }

}
