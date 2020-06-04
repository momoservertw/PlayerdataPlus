package tw.momocraft.playerdataplus.PlayerStatus.God;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GodControl {

    private boolean runSchedule = false;

    public void setRunSchedule(boolean runSchedule) {
        this.runSchedule = runSchedule;
    }

    public boolean isRunSchedule() {
        return runSchedule;
    }

    public void startSchedule() {
        if (ConfigHandler.getConfigPath().isPsGodEnable()) {
            if (ConfigHandler.getConfigPath().isPsGodSchedule()) {
                runSchedule = true;
                ServerHandler.sendConsoleMessage("&6Start checking God status for players...");
                List<String> ignorePerms = ConfigHandler.getConfigPath().getPsGodPerms();

                new BukkitRunnable() {
                    String playerName;

                    @Override
                    public void run() {
                        if (!runSchedule) {
                            ServerHandler.sendConsoleMessage("&6God-Status process has ended.");
                            cancel();
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String playerName = player.getName();
                            CMIUser user;
                            try {
                                user = CMI.getInstance().getPlayerManager().getUser(player);
                            } catch (Exception ex) {
                                return;
                            }
                            if (user.isGod()) {
                                if (!ignorePerms.isEmpty()) {
                                    if (isPerms(player, ignorePerms)) {
                                        ServerHandler.sendFeatureMessage("Player-Status.God", playerName, "World-Change", "bypass", "Permissions",
                        new Throwable().getStackTrace()[0]);
                                        return;
                                    }
                                }
                                if (isGodCMI(user)) {
                                    ServerHandler.sendFeatureMessage("Player-Status.God", playerName, "World-Change", "bypass", "CMI",
                        new Throwable().getStackTrace()[0]);
                                    return;
                                }
                                user.setGod(false);
                                ServerHandler.sendFeatureMessage("Player-Status.God", playerName, "World-Change", "cancel", "final",
                        new Throwable().getStackTrace()[0]);
                            }
                        }
                    }
                }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getConfigPath().getPsGodInterval());
                ServerHandler.sendFeatureMessage("Player-Status.God", "final", "Schedule", "return",
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


    public boolean isGodCMI(CMIUser user) {
        if (ConfigHandler.getConfigPath().isPsGodCMIT()) {
            return user.getTgod() > 0;
        }
        return false;
    }
}
