package tw.momocraft.playerdataplus.PlayerStatus.Op;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class OpPlayerQuit implements Listener {

    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (ConfigHandler.getConfigPath().isPsOpEnable()) {
            if (ConfigHandler.getConfigPath().isPsOpLeave()) {
                OpControl opControl = new OpControl();
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsOpPerms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                if (player.isOp()) {
                    if (!ignorePerms.isEmpty()) {
                        if (opControl.isPerms(player, ignorePerms)) {
                            ServerHandler.sendFeatureMessage("Player-Status.Op", playerName, "World-Change", "bypass", "Permissions",
                        new Throwable().getStackTrace()[0]);
                            return;
                        }
                    }
                    player.setOp(false);
                    ServerHandler.sendFeatureMessage("Player-Status.Op", playerName, "World-Change", "cancel", "final",
                        new Throwable().getStackTrace()[0]);
                }
            }
        }
    }
}
