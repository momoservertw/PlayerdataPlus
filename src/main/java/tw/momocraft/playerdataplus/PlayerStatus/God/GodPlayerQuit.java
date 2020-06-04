package tw.momocraft.playerdataplus.PlayerStatus.God;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GodPlayerQuit implements Listener {

    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (ConfigHandler.getConfigPath().isPsGodEnable()) {
            if (ConfigHandler.getConfigPath().isPsGodLeave()) {
                GodControl godControl = new GodControl();
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsGodPerms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                CMIUser user;
                try {
                    user = CMI.getInstance().getPlayerManager().getUser(player);
                } catch (Exception ex) {
                    return;
                }
                if (user.isGod()) {
                    if (!ignorePerms.isEmpty()) {
                        if (godControl.isPerms(player, ignorePerms)) {
                            ServerHandler.debugMessage("Player-Status.God", playerName, "Leave", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (godControl.isGodCMI(user)) {
                        ServerHandler.debugMessage("Player-Status.God", playerName, "Leave", "bypass", "CMI");
                        return;
                    }
                    user.setGod(false);
                    ServerHandler.debugMessage("Player-Status.God", playerName, "Leave", "cancel", "final");
                }
            }
        }
    }
}
