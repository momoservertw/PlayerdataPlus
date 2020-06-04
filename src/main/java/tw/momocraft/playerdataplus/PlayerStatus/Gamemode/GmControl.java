package tw.momocraft.playerdataplus.PlayerStatus.Gamemode;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GmControl {

    private boolean runSchedule = false;

    public void setRunSchedule(boolean runSchedule) {
        this.runSchedule = runSchedule;
    }

    public boolean isRunSchedule() {
        return runSchedule;
    }

    public void startSchedule() {
        if (ConfigHandler.getConfigPath().isPsGmEnable()) {
            if (ConfigHandler.getConfigPath().isPsGmSchedule()) {
                runSchedule = true;
                ServerHandler.sendConsoleMessage("&6Start checking Gm status for players...");
                List<String> gm0IgnorePerms = ConfigHandler.getConfigPath().getPsGm0Perms();
                List<String> gm1IgnorePerms = ConfigHandler.getConfigPath().getPsGm1Perms();
                List<String> gm2IgnorePerms = ConfigHandler.getConfigPath().getPsGm2Perms();
                List<String> gm3IgnorePerms = ConfigHandler.getConfigPath().getPsGm3Perms();

                new BukkitRunnable() {
                    String playerName;
                    String gamemode;

                    @Override
                    public void run() {
                        if (!runSchedule) {
                            ServerHandler.sendConsoleMessage("&6Gm-Status process has ended.");
                            cancel();
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            playerName = player.getName();
                            gamemode = player.getGameMode().name();
                            switch (gamemode) {
                                case "CREATIVE":
                                    if (!gm1IgnorePerms.isEmpty()) {
                                        if (isPerms(player, gm1IgnorePerms)) {
                                            ServerHandler.sendFeatureMessage("Player-Status.Gm1", playerName, "Schedule", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm1Default().toUpperCase()));
                                    ServerHandler.sendFeatureMessage("Player-Status.Gm1", playerName, "Schedule", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    break;
                                case "ADVENTURE":
                                    if (!gm2IgnorePerms.isEmpty()) {
                                        if (isPerms(player, gm2IgnorePerms)) {
                                            ServerHandler.sendFeatureMessage("Player-Status.Gm2", playerName, "Schedule", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm2Default().toUpperCase()));
                                    ServerHandler.sendFeatureMessage("Player-Status.Gm2", playerName, "Schedule", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    break;
                                case "SPECTATOR":
                                    if (!gm3IgnorePerms.isEmpty()) {
                                        if (isPerms(player, gm3IgnorePerms)) {
                                            ServerHandler.sendFeatureMessage("Player-Status.Gm3", playerName, "Schedule", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm3Default().toUpperCase()));
                                    ServerHandler.sendFeatureMessage("Player-Status.Gm3", playerName, "Schedule", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    break;
                                default:
                                    if (!gm0IgnorePerms.isEmpty()) {
                                        if (isPerms(player, gm0IgnorePerms)) {
                                            ServerHandler.sendFeatureMessage("Player-Status.Gm0", playerName, "Schedule", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGm0Default().toUpperCase()));
                                    ServerHandler.sendFeatureMessage("Player-Status.Gm1", playerName, "Schedule", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    break;
                            }
                        }
                    }
                }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getConfigPath().getPsGmInterval());
                ServerHandler.sendFeatureMessage("Player-Status.Gm", "final", "Schedule", "return",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    public boolean isPerms(Player player, List<String> ignorePerms) {
        for (String perms : ignorePerms) {
            if (PermissionsHandler.hasPermission(player, perms)) {
                return true;
            }
        }
        return false;
    }
}
