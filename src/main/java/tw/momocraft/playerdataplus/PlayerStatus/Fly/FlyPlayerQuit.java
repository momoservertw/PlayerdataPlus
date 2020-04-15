package tw.momocraft.playerdataplus.PlayerStatus.Fly;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class FlyPlayerQuit implements Listener {

    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (ConfigHandler.getPlayerdataConfig().isPsFly()) {
            if (ConfigHandler.getPlayerdataConfig().isPsFlyLeave()) {
                FlyControl flyStatus = new FlyControl();
                List<String> ignorePerms = ConfigHandler.getPlayerdataConfig().getPsFlyPerms();
                boolean resEnable = flyStatus.getResEnable();
                boolean cmiEnable = flyStatus.getCmiEnable();
                boolean cmiTFly = flyStatus.getCmiTFlyEnable();
                boolean cmiCFly = flyStatus.getCmiCFlyEnable();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isFlying()) {
                    if (!ignorePerms.isEmpty()) {
                        if (flyStatus.isPerms(player, ignorePerms)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Leave", "bypass", "Permissions");
                            return;
                        }
                    }
                    if (resEnable) {
                        if (flyStatus.isResFly(player)) {
                            ServerHandler.debugMessage("Player-Status.Fly", playerName, "Leave", "bypass", "Residence");
                            return;
                        }
                    }
                    if (cmiEnable) {
                        if (flyStatus.isCMIFly(player, cmiTFly, cmiCFly)) {
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
