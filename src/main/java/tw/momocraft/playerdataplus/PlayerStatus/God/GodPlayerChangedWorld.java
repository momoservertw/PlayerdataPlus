package tw.momocraft.playerdataplus.PlayerStatus.God;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GodPlayerChangedWorld implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        if (ConfigHandler.getPlayerdataConfig().isPsGodEnable()) {
            if (ConfigHandler.getPlayerdataConfig().isPsGodWorld()) {
                GodControl godControl = new GodControl();
                List<String> ignorePerms = ConfigHandler.getPlayerdataConfig().getPsGodPerms();

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
                            ServerHandler.debugMessage("Player-Status.God", playerName, "World-Change", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (godControl.isGodCMI(user)) {
                        ServerHandler.debugMessage("Player-Status.God", playerName, "World-Change", "bypass", "CMI");
                        return;
                    }
                    user.setGod(false);
                    ServerHandler.debugMessage("Player-Status.God", playerName, "World-Change", "cancel", "final");
                }
            }
        }
    }
}
