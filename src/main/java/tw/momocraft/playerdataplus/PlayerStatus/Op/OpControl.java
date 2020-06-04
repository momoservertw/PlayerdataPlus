package tw.momocraft.playerdataplus.PlayerStatus.Op;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class OpControl {

    private boolean runSchedule = false;

    public void setRunSchedule(boolean runSchedule) {
        this.runSchedule = runSchedule;
    }

    public boolean isRunSchedule() {
        return runSchedule;
    }

    public void startSchedule() {
        if (ConfigHandler.getConfigPath().isPsOpEnable()) {
            if (ConfigHandler.getConfigPath().isPsOpSchedule()) {
                runSchedule = true;
                ServerHandler.sendConsoleMessage("&6Start checking Op status for players...");
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsOpPerms();

                new BukkitRunnable() {
                    String playerName;

                    @Override
                    public void run() {
                        if (!runSchedule) {
                            ServerHandler.sendConsoleMessage("&6Op-Status process has ended.");
                            cancel();
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String playerName = player.getName();
                            if (player.isOp()) {
                                if (!ignorePerms.isEmpty()) {
                                    if (isPerms(player, ignorePerms)) {
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
                }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getConfigPath().getPsOpInterval());
                ServerHandler.sendFeatureMessage("Player-Status.Op", "final", "Schedule", "return",
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
