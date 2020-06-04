package tw.momocraft.playerdataplus.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

public class UserConvert {

    private void transferManager(String name1, String name2) {
        transferCMI(name1, name2);

    }

    private void transferCMI(String name1, String name2) {
        if (ConfigHandler.getDepends().CMIEnabled()) {
            CMIUser user1 = null;
            CMIUser user2 = null;
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
    private void transferCMI(String name1, String name2) {
        if (ConfigHandler.getDepends().CMIEnabled()) {
            CMIUser user1 = null;
            CMIUser user2 = null;
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
