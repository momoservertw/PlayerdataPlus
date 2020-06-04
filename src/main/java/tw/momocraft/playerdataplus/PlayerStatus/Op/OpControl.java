package tw.momocraft.playerdataplus.PlayerStatus.Op;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class OpControl implements Listener {

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
                                        ServerHandler.debugMessage("Player-Status.Op", playerName, "World-Change", "bypass", "Permissions");
                                        return;
                                    }
                                }
                                player.setOp(false);
                                ServerHandler.debugMessage("Player-Status.Op", playerName, "World-Change", "cancel", "final");
                            }
                        }
                    }
                }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getConfigPath().getPsOpInterval());
                ServerHandler.debugMessage("Player-Status.Op", "final", "Schedule", "return");
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
