package tw.momocraft.playerdataplus.PlayerStatus.God;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class GodControl implements Listener {

    private boolean runSchedule = false;

    public void setRunSchedule(boolean runSchedule) {
        this.runSchedule = runSchedule;
    }

    public boolean isRunSchedule() {
        return runSchedule;
    }

    public void startSchedule() {
        if (ConfigHandler.getPlayerdataConfig().isPsGodEnable()) {
            if (ConfigHandler.getPlayerdataConfig().isPsGodSchedule()) {
                runSchedule = true;
                ServerHandler.sendConsoleMessage("&6Start checking God status for players...");
                List<String> ignorePerms = ConfigHandler.getPlayerdataConfig().getPsGodPerms();

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
                                        ServerHandler.debugMessage("Player-Status.God", playerName, "World-Change", "bypass", "Permissions");
                                        return;
                                    }
                                }
                                if (isGodCMI(user)) {
                                    ServerHandler.debugMessage("Player-Status.God", playerName, "World-Change", "bypass", "CMI");
                                    return;
                                }
                                user.setGod(false);
                                ServerHandler.debugMessage("Player-Status.God", playerName, "World-Change", "cancel", "final");
                            }
                        }
                    }
                }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getPlayerdataConfig().getPsGodInterval());
                ServerHandler.debugMessage("Player-Status.God", "final", "Schedule", "return");
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
        if (ConfigHandler.getPlayerdataConfig().isPsGodCMIT()) {
            return user.getTgod() > 0;
        }
        return false;
    }
}
