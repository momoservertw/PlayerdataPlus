package tw.momocraft.playerdataplus.PlayerStatus.Op;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class OpPlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (ConfigHandler.getConfigPath().isPsOpEnable()) {
            if (ConfigHandler.getConfigPath().isPsOpLogin()) {
                OpControl opControl = new OpControl();
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsGodPerms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isOp()) {
                    if (!ignorePerms.isEmpty()) {
                        if (opControl.isPerms(player, ignorePerms)) {
                            ServerHandler.debugMessage("Player-Status.Op", playerName, "World-Change", "bypass", "Permissions");
                            return;
                        }
                    }
                    player.setOp(false);
                    ServerHandler.debugMessage("Player-Status.Op", playerName, "World-Change", "cancel", "final");
                }
            }
        }
    }
}
