package tw.momocraft.playerdataplus.PlayerStatus.God;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GodPlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (ConfigHandler.getConfigPath().isPsGodEnable()) {
            if (ConfigHandler.getConfigPath().isPsGodLogin()) {
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
                            ServerHandler.debugMessage("Player-Status.God", playerName, "Join", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (godControl.isGodCMI(user)) {
                        ServerHandler.debugMessage("Player-Status.God", playerName, "Join", "bypass", "CMI");
                        return;
                    }
                    user.setGod(false);
                    ServerHandler.debugMessage("Player-Status.God", playerName, "Join", "cancel", "final");
                }
            }
        }
    }
}
