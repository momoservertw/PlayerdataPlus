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
        if (ConfigHandler.getPlayerdataConfig().isPsFly()) {
            if (ConfigHandler.getPlayerdataConfig().isPsFlyWorld()) {
                FlyControl flyStatus = new FlyControl();
                List<String> ignorePerms = ConfigHandler.getPlayerdataConfig().getPsFlyPerms();
                boolean resEnable = flyStatus.getResEnable();
                boolean cmiEnable = flyStatus.getCmiEnable();
                boolean cmiTFly = flyStatus.getCmiTFlyEnable();
                boolean cFly = flyStatus.getCmiCFlyEnable();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isFlying()) {
                    if (!ignorePerms.isEmpty()) {
                        if (flyStatus.isPerms(player, ignorePerms)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (resEnable) {
                        if (flyStatus.isResFly(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "World-Change", "bypass", "Residence");
                            return;
                        }
                    }
                    if (cmiEnable) {
                        if (flyStatus.isCMIFly(player, cmiTFly, cFly)) {
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
