package tw.momocraft.playerdataplus.PlayerStatus.Fly;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class FlyPlayerChangedWorld implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        if (ConfigHandler.getConfigPath().isPsFlyEnable()) {
            if (ConfigHandler.getConfigPath().isPsFlyWorld()) {
                FlyControl flyStatus = new FlyControl();
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsFlyPerms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isFlying()) {
                    if (!ignorePerms.isEmpty()) {
                        if (flyStatus.isPerms(player, ignorePerms)) {
                            ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "Permissions",
                                    new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyRes()) {
                        if (flyStatus.isFlyRes(player)) {
                            ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "Residence",
                                    new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyCMIC() || ConfigHandler.getConfigPath().isPsFlyCMIT()) {
                        if (flyStatus.isFlyCMI(player)) {
                            ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "CMI",
                                    new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    player.setFlying(false);
                    ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "World-Change", "cancel", "final",
                            new Throwable().getStackTrace()[0]);
                }
            }
        }
    }
}
