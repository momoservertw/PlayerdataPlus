package tw.momocraft.playerdataplus.utils;

public class UserConvert {

    /*

    private void convertManager(String name1, String name2, boolean replace) {
        UUID uuid1 = CorePlusAPI.getPlayer().getPlayerUUID(name1);
        UUID uuid2 = CorePlusAPI.getPlayer().getPlayerUUID(name2);

        convertPlayerdata(uuid1, uuid2);
        convertAdvancements(uuid1, uuid2);
        convertStats(uuid1, uuid2);

        convertMoney(name1, name2, uuid1, uuid2, replace);
        convertPoints(uuid1, uuid2, replace);

        convertPerms(name1, name2, replace);

        convertCMI(name1, name2);
        convertPoints(uuid1, uuid2, replace);
        convertResidence(name1, name2, replace);

    }


    private void convertMoney(String name1, String name2, UUID uuid1, UUID uuid2, boolean replace) {
        if (CorePlusAPI.getDepend().CMIEnabled()) {
            CMIEconomy economy = new CMIEconomy();
            economy.transfer(name1, name2, economy.getBalance(name1));
        } else if (CorePlusAPI.getDepend().getVault().vaultEnabled() && CorePlusAPI.getDepend().getVault().getEconomy().isEnabled()) {

            Economy economy = CorePlusAPI.getDepend().getVault().getEconomy();
            if (replace) {
                economy.depositPlayer()
                economy.bankWithdraw();
            } else {
                playerPointsAPI.givePoints(uuid2, playerPointsAPI.getPoints(uuid1));
            }
        }
    }


    private void convertPoints(UUID uuid1, UUID uuid2, boolean replace) {
        if (CorePlusAPI.getDepend().PlayerPointsEnabled()) {
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
        if (CorePlusAPI.getDepend().LuckPermsEnabled()) {
            if (replace) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + name1 + " clone " + name2);
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + name1 + " clone " + name2);
            }
        } else if (CorePlusAPI.getDepend().getVault().vaultEnabled() && CorePlusAPI.getDepend().getVault().getPermissions().isEnabled()) {
            CorePlusAPI.getDepend().getVault().getPermissions().
        }
    }

    private void convertCMI(String name1, String name2) {
        if (CorePlusAPI.getDepend().CMIEnabled()) {
            CMIUser user1;
            CMIUser user2;
            try {
                user1 = CMI.getInstance().getPlayer().getUser(name1);
            } catch (Exception e) {
                ServerHandler.sendConsoleMessage("&cCan not find CMI user \"" + name1 + "\"!");
                return;
            }
            try {
                user2 = CMI.getInstance().getPlayer().getUser(name2);
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

     * @param name1   the name of first player.
     * @param name2   the name of target player.
     * @param replace clear the target player data first.

    private void convertResidence(String name1, String name2, boolean replace) {
        if (CorePlusAPI.getDepend().ResidenceEnabled()) {
            ResidencePlayer resPlayer1 = Residence.getInstance().getPlayer().getResidencePlayer(name1);
            ResidencePlayer resPlayer2 = Residence.getInstance().getPlayer().getResidencePlayer(name2);
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
     */
}
