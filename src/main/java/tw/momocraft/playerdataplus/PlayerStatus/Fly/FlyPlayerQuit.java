package tw.momocraft.playerdataplus.PlayerStatus.Fly;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class FlyPlayerQuit implements Listener {

    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (ConfigHandler.getConfigPath().isPsFlyEnable()) {
            if (ConfigHandler.getConfigPath().isPsFlyLeave()) {
                FlyControl flyStatus = new FlyControl();
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsFlyPerms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isFlying()) {
                    if (!ignorePerms.isEmpty()) {
                        if (flyStatus.isPerms(player, ignorePerms)) {
                            ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "Leave", "bypass", "Permissions",
                                    new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyRes()) {
                        if (flyStatus.isFlyRes(player)) {
                            ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "Leave", "bypass", "Residence",
                                    new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyCMIC() || ConfigHandler.getConfigPath().isPsFlyCMIT()) {
                        if (flyStatus.isFlyCMI(player)) {
                            ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "Leave", "bypass", "CMI",
                                    new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    player.setFlying(false);
                    ServerHandler.sendFeatureMessage("Player-Status.Fly", playerName, "Leave", "cancel", "final",
                            new Throwable().getStackTrace()[0]);
                }
            }
        }
    }
}
