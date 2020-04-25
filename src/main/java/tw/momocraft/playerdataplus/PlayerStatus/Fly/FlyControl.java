package tw.momocraft.playerdataplus.PlayerStatus.Fly;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.List;

public class FlyControl implements Listener {

    private boolean runSchedule = false;

    public void setRunSchedule(boolean runSchedule) {
        this.runSchedule = runSchedule;
    }

    public boolean isRunSchedule() {
        return runSchedule;
    }

    public void startSchedule() {
        if (ConfigHandler.getPlayerdataConfig().isPsFlyEnable()) {
            if (ConfigHandler.getPlayerdataConfig().isPsFlySchedule()) {
                runSchedule = true;
                ServerHandler.sendConsoleMessage("&6Start checking Fly status for players...");
                List<String> ignorePerms = ConfigHandler.getPlayerdataConfig().getPsFlyPerms();
                boolean resEnable = ConfigHandler.getPlayerdataConfig().isPsFlyRes();
                boolean cmiEnable = ConfigHandler.getPlayerdataConfig().isPsFlyCMIC() || ConfigHandler.getPlayerdataConfig().isPsFlyCMIT();

                new BukkitRunnable() {
                    String playerName;

                    @Override
                    public void run() {
                        if (!runSchedule) {
                            ServerHandler.sendConsoleMessage("&6Fly-Status process has ended.");
                            cancel();
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isFlying()) {
                                playerName = player.getName();
                                if (!ignorePerms.isEmpty()) {
                                    if (isPerms(player, ignorePerms)) {
                                        ServerHandler.debugMessage("Player-Status.Fly", playerName, "Schedule", "bypass", "Permissions");
                                        continue;
                                    }
                                }
                                if (resEnable) {
                                    if (isFlyRes(player)) {
                                        ServerHandler.debugMessage("Player-Status.Fly", playerName, "Schedule", "bypass", "Residence");
                                        continue;
                                    }
                                }
                                if (cmiEnable) {
                                    if (isFlyCMI(player)) {
                                        ServerHandler.debugMessage("Player-Status.Fly", playerName, "Schedule", "bypass", "CMI");
                                        continue;
                                    }
                                }
                                player.setFlying(false);
                                ServerHandler.debugMessage("Player-Status.Fly", playerName, "Schedule", "cancel", "final");
                            }
                        }
                    }
                }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getPlayerdataConfig().getPsFlyInterval());
                ServerHandler.debugMessage("Player-Status.Fly", "final", "Schedule", "return");
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


    public boolean isFlyRes(Player player) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(player);
        if (res != null) {
            return res.getPermissions().playerHas(player, Flags.fly, false);
        }
        return true;
    }

    public boolean isFlyCMI(Player player) {
        CMIUser user;
        try {
            user = CMI.getInstance().getPlayerManager().getUser(player);
        } catch (Exception e) {
            return true;
        }
        if (ConfigHandler.getPlayerdataConfig().isPsFlyCMIT()) {
            if (user.getTfly() > 0) {
                return true;
            }
        }
        if (ConfigHandler.getPlayerdataConfig().isPsFlyCMIC()) {
            if (PermissionsHandler.hasPermission(player, "cmi.command.flyc")) {
                return true;
            }
        }
        return false;
    }
}
