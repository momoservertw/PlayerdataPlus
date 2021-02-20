package tw.momocraft.playerdataplus.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.economy.CMIEconomy;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PlayerHandler;

import java.util.UUID;

public class UserConvert {

    /**
     * The manager if converting player data.
     *
     * @param name1 the name of first player.
     * @param name2 the name of second player.
     */
    private void convertManager(String name1, String name2, boolean replace) {
        UUID uuid1 = PlayerHandler.getOfflineUUID(name1);
        UUID uuid2 = PlayerHandler.getOfflineUUID(name2);

        convertPlayerdata(uuid1, uuid2);
        //convertAdvancements(uuid1, uuid2);
        //convertStats(uuid1, uuid2);

        convertMoney(name1, name2, uuid1, uuid2, replace);
        convertPoints(uuid1, uuid2, replace);

        convertPerms(name1, name2, replace);

        convertCMI(name1, name2);
        convertPoints(uuid1, uuid2, replace);
        convertResidence(name1, name2, replace);

    }


    private void convertMoney(String name1, String name2, UUID uuid1, UUID uuid2, boolean replace) {
        if (ConfigHandler.getDepends().CMIEnabled()) {
            CMIEconomy economy = new CMIEconomy();
            economy.transfer(name1, name2, economy.getBalance(name1));
        }/* else if (ConfigHandler.getDepends().getVault().vaultEnabled() && ConfigHandler.getDepends().getVault().getEconomy().isEnabled()) {

            Economy economy = ConfigHandler.getDepends().getVault().getEconomy();
            if (replace) {
                economy.depositPlayer()
                economy.bankWithdraw();
            } else {
                playerPointsAPI.givePoints(uuid2, playerPointsAPI.getPoints(uuid1));
            }
        }
             */
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

    private void convertPlayerdata(UUID uuid1, UUID uuid2) {

    }

    private void convertPerms(String name1, String name2, boolean replace) {
        if (ConfigHandler.getDepends().LuckPermsEnabled()) {
            if (replace) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + name1 + " clone " + name2);
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + name1 + " clone " + name2);
            }
        } /*else if (ConfigHandler.getDepends().getVault().vaultEnabled() && ConfigHandler.getDepends().getVault().getPermissions().isEnabled()) {
            ConfigHandler.getDepends().getVault().getPermissions().
        }
       */
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

    /**
     * @param name1   the name of first player.
     * @param name2   the name of target player.
     * @param replace clear the target player data first.
     */
    private void convertResidence(String name1, String name2, boolean replace) {
        if (ConfigHandler.getDepends().ResidenceEnabled()) {
            ResidencePlayer resPlayer1 = Residence.getInstance().getPlayerManager().getResidencePlayer(name1);
            ResidencePlayer resPlayer2 = Residence.getInstance().getPlayerManager().getResidencePlayer(name2);
            if (replace) {
                for (ClaimedResidence residence : resPlayer2.getResList()) {
                    resPlayer2.removeResidence(residence);
                }
                for (ClaimedResidence residence : resPlayer1.getResList()) {
                    resPlayer2.addResidence(residence);
                }
            } else {
                for (ClaimedResidence residence : resPlayer1.getResList()) {
                    resPlayer2.addResidence(residence);
                }
            }
        }
    }
}
