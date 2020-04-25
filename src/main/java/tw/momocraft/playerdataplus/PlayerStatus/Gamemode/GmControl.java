package tw.momocraft.playerdataplus.PlayerStatus.Gamemode;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GmControl implements Listener {

    private boolean runSchedule = false;

    public void setRunSchedule(boolean runSchedule) {
        this.runSchedule = runSchedule;
    }

    public boolean isRunSchedule() {
        return runSchedule;
    }

    public void startSchedule() {
        if (ConfigHandler.getPlayerdataConfig().isPsGmEnable()) {
            if (ConfigHandler.getPlayerdataConfig().isPsGmSchedule()) {
                runSchedule = true;
                ServerHandler.sendConsoleMessage("&6Start checking Gm status for players...");
                List<String> gm0IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm0Perms();
                List<String> gm1IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm1Perms();
                List<String> gm2IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm2Perms();
                List<String> gm3IgnorePerms = ConfigHandler.getPlayerdataConfig().getPsGm3Perms();

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
                                            ServerHandler.debugMessage("Player-Status.Gm1", playerName, "Schedule", "bypass", "Permissions");
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm1Default().toUpperCase()));
                                    ServerHandler.debugMessage("Player-Status.Gm1", playerName, "Schedule", "cancel", "final");
                                    break;
                                case "ADVENTURE":
                                    if (!gm2IgnorePerms.isEmpty()) {
                                        if (isPerms(player, gm2IgnorePerms)) {
                                            ServerHandler.debugMessage("Player-Status.Gm2", playerName, "Schedule", "bypass", "Permissions");
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm2Default().toUpperCase()));
                                    ServerHandler.debugMessage("Player-Status.Gm2", playerName, "Schedule", "cancel", "final");
                                    break;
                                case "SPECTATOR":
                                    if (!gm3IgnorePerms.isEmpty()) {
                                        if (isPerms(player, gm3IgnorePerms)) {
                                            ServerHandler.debugMessage("Player-Status.Gm3", playerName, "Schedule", "bypass", "Permissions");
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm3Default().toUpperCase()));
                                    ServerHandler.debugMessage("Player-Status.Gm3", playerName, "Schedule", "cancel", "final");
                                    break;
                                default:
                                    if (!gm0IgnorePerms.isEmpty()) {
                                        if (isPerms(player, gm0IgnorePerms)) {
                                            ServerHandler.debugMessage("Player-Status.Gm0", playerName, "Schedule", "bypass", "Permissions");
                                            return;
                                        }
                                    }
                                    player.setGameMode(GameMode.valueOf(ConfigHandler.getPlayerdataConfig().getPsGm0Default().toUpperCase()));
                                    ServerHandler.debugMessage("Player-Status.Gm1", playerName, "Schedule", "cancel", "final");
                                    break;
                            }
                        }
                    }
                }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getPlayerdataConfig().getPsGmInterval());
                ServerHandler.debugMessage("Player-Status.Gm", "final", "Schedule", "return");
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
