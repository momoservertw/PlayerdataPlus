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
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Leave", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyRes()) {
                        if (flyStatus.isFlyRes(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Leave", "bypass", "Residence");
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyCMIC() || ConfigHandler.getConfigPath().isPsFlyCMIT()) {
                        if (flyStatus.isFlyCMI(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Leave", "bypass", "CMI");
                            return;
                        }
                    }
                    player.setFlying(false);
                    ServerHandler.debugMessage("Player-Status.Fly", playerName, "Leave", "cancel", "final");
                }
            }
        }
    }
}
