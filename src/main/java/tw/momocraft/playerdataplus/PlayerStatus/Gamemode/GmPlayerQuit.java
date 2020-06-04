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
        if (ConfigHandler.getConfigPath().isPsGmEnable()) {
            if (ConfigHandler.getConfigPath().isPsGmLeave()) {
                GmControl gmControl = new GmControl();
                List<String> gm0IgnorePerms = ConfigHandler.getConfigPath().getPsGm0Perms();
                List<String> gm1IgnorePerms = ConfigHandler.getConfigPath().getPsGm1Perms();
                List<String> gm2IgnorePerms = ConfigHandler.getConfigPath().getPsGm2Perms();
                List<String> gm3IgnorePerms = ConfigHandler.getConfigPath().getPsGm3Perms();

                Player player = e.getPlayer();
                String playerName = player.getName();
                switch (player.getGameMode().name()) {
                    case "CREATIVE":
                        if (!gm1IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm1IgnorePerms)) {
                                ServerHandler.sendFeatureMessage("Player-Status.Gm1", playerName, "Leave", "bypass", "Permissions",
                        new Throwable().getStackTrace()[0]);
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm1Default().toUpperCase()));
                        ServerHandler.sendFeatureMessage("Player-Status.Gm1", playerName, "Leave", "cancel", "final",
                        new Throwable().getStackTrace()[0]);
                        break;
                    case "ADVENTURE":
                        if (!gm2IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm2IgnorePerms)) {
                                ServerHandler.sendFeatureMessage("Player-Status.Gm2", playerName, "Leave", "bypass", "Permissions",
                        new Throwable().getStackTrace()[0]);
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm2Default().toUpperCase()));
                        ServerHandler.sendFeatureMessage("Player-Status.Gm2", playerName, "Leave", "cancel", "final",
                        new Throwable().getStackTrace()[0]);
                        break;
                    case "SPECTATOR":
                        if (!gm3IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm3IgnorePerms)) {
                                ServerHandler.sendFeatureMessage("Player-Status.Gm3", playerName, "Leave", "bypass", "Permissions",
                        new Throwable().getStackTrace()[0]);
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm3Default().toUpperCase()));
                        ServerHandler.sendFeatureMessage("Player-Status.Gm3", playerName, "Leave", "cancel", "final",
                        new Throwable().getStackTrace()[0]);
                        break;
                    default:
                        if (!gm0IgnorePerms.isEmpty()) {
                            if (gmControl.isPerms(player, gm0IgnorePerms)) {
                                ServerHandler.sendFeatureMessage("Player-Status.Gm0", playerName, "Leave", "bypass", "Permissions",
                        new Throwable().getStackTrace()[0]);
                                return;
                            }
                        }
                        player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm0Default().toUpperCase()));
                        ServerHandler.sendFeatureMessage("Player-Status.Gm1", playerName, "Leave", "cancel", "final",
                        new Throwable().getStackTrace()[0]);
                        break;
                }
            }
        }
    }
}
