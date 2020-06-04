package tw.momocraft.playerdataplus.PlayerStatus.Fly;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class FlyPlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (ConfigHandler.getConfigPath().isPsFlyEnable()) {
            if (ConfigHandler.getConfigPath().isPsFlyLogin()) {
                FlyControl flyStatus = new FlyControl();
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsFlyPerms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isFlying()) {
                    if (!ignorePerms.isEmpty()) {
                        if (flyStatus.isPerms(player, ignorePerms)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Join", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyRes()) {
                        if (flyStatus.isFlyRes(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Join", "bypass", "Residence");
                            return;
                        }
                    }
                    if (ConfigHandler.getConfigPath().isPsFlyCMIC() || ConfigHandler.getConfigPath().isPsFlyCMIT()) {
                        if (flyStatus.isFlyCMI(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Join", "bypass", "CMI");
                            return;
                        }
                    }
                    player.setFlying(false);
                    ServerHandler.debugMessage("Player-Status.Fly", playerName, "Join", "cancel", "final");
                }
            }
        }
    }
}
