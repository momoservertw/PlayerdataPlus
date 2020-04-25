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
        if (ConfigHandler.getPlayerdataConfig().isPsFlyEnable()) {
            if (ConfigHandler.getPlayerdataConfig().isPsFlyWorld()) {
                FlyControl flyStatus = new FlyControl();
                List<String> ignorePerms = ConfigHandler.getPlayerdataConfig().getPsFlyPerms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isFlying()) {
                    if (!ignorePerms.isEmpty()) {
                        if (flyStatus.isPerms(player, ignorePerms)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (ConfigHandler.getPlayerdataConfig().isPsFlyRes()) {
                        if (flyStatus.isFlyRes(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "Residence");
                            return;
                        }
                    }
                    if (ConfigHandler.getPlayerdataConfig().isPsFlyCMIC() || ConfigHandler.getPlayerdataConfig().isPsFlyCMIT()) {
                        if (flyStatus.isFlyCMI(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "CMI");
                            return;
                        }
                    }
                    player.setFlying(false);
                    ServerHandler.debugMessage("Player-Status.Fly", playerName, "World-Change", "cancel", "final");
                }
            }
        }
    }
}
