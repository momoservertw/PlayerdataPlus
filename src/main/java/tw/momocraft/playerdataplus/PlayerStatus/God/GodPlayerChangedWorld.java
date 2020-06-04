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
        if (ConfigHandler.getConfigPath().isPsGodEnable()) {
            if (ConfigHandler.getConfigPath().isPsGodWorld()) {
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
                            ServerHandler.sendFeatureMessage("Player-Status.God", playerName, "World-Change", "bypass", "Permissions",
                        new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    if (godControl.isGodCMI(user)) {
                        ServerHandler.sendFeatureMessage("Player-Status.God", playerName, "World-Change", "bypass", "CMI",
                        new Throwable().getStackTrace()[0]);
                        return;
                    }
                    user.setGod(false);
                    ServerHandler.sendFeatureMessage("Player-Status.God", playerName, "World-Change", "cancel", "final",
                        new Throwable().getStackTrace()[0]);
                }
            }
        }
    }
}
