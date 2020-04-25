package tw.momocraft.playerdataplus.PlayerStatus.Gamemode;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GmPlayerQuit implements Listener {

    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (ConfigHandler.getPlayerdataConfig().isPsGmEnable()) {
            if (ConfigHandler.getPlayerdataConfig().isPsGmLeave()) {
                GmControl gmControl = new GmControl();
                List<String> gm0IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm0Perms();
                List<String> gm1IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm1Perms();
                List<String> gm2IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm2Perms();
                List<String> gm3IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm3Perms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                switch (player.getGameMode().name()) {
                    case "CREATIVE":
                        if (!gm1IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm1IgnorePerms)) {
                                ServerHandler.debugMessage("Player-Status.Gm1", playerName, "Leave", "bypass", "Permissions");
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm1Default().toUpperCase()));
                        ServerHandler.debugMessage("Player-Status.Gm1", playerName, "Leave", "cancel", "final");
                        break;
                    case "ADVENTURE":
                        if (!gm2IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm2IgnorePerms)) {
                                ServerHandler.debugMessage("Player-Status.Gm2", playerName, "Leave", "bypass", "Permissions");
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm2Default().toUpperCase()));
                        ServerHandler.debugMessage("Player-Status.Gm2", playerName, "Leave", "cancel", "final");
                        break;
                    case "SPECTATOR":
                        if (!gm3IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm3IgnorePerms)) {
                                ServerHandler.debugMessage("Player-Status.Gm3", playerName, "Leave", "bypass", "Permissions");
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm3Default().toUpperCase()));
                        ServerHandler.debugMessage("Player-Status.Gm3", playerName, "Leave", "cancel", "final");
                        break;
                    default:
                        if (!gm0IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm0IgnorePerms)) {
                                ServerHandler.debugMessage("Player-Status.Gm0", playerName, "Leave", "bypass", "Permissions");
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm0Default().toUpperCase()));
                        ServerHandler.debugMessage("Player-Status.Gm1", playerName, "Leave", "cancel", "final");
                        break;
                }
            }
        }
    }
}
