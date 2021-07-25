package tw.momocraft.playerdataplus.playerstatus;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.utils.ConfigPath;

import java.util.List;
import java.util.Map;

public class PlayerStatusControl {

    private static boolean schedule;

    public static void startSchedule() {
        if (ConfigHandler.getConfigPath().isPsSchdeule()) {
            schedule = true;
            ServerHandler.sendConsoleMessage("&6Start checking Player status for players...");
            new BukkitRunnable() {
                String playerName;

                @Override
                public void run() {
                    if (!schedule) {
                        cancel();
                    }
                    Map<String, PlayerStatusMap> playerStatusProp = ConfigHandler.getConfigPath().getPlayerStatusProp();
                    if (playerStatusProp == null) {
                        ServerHandler.sendConsoleMessage("&cNot player status group enabled.");
                        schedule = false;
                        cancel();
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Location loc = player.getLocation();
                        // Checking the enable worlds.
                        PlayerStatusMap playerStatusMap;
                        List<String> ignorePerms;
                        // Checking every enabled Type like "fly, god, gamemode, op"
                        back:
                        for (String groupName : playerStatusProp.keySet()) {
                            playerStatusMap = playerStatusProp.get(groupName);
                            // Checking is enable world-change checking.
                            switch (groupName.toLowerCase()) {
                                case "fly":
                                    // Checking the player status is enabled.
                                    if (player.isFlying()) {
                                        continue back;
                                    }
                                    // Checking Location.
                                    if (!ConfigPath.getLocationUtils().checkLocation(loc, playerStatusMap.getLocMaps())) {
                                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                                "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                                new Throwable().getStackTrace()[0]);
                                        continue;
                                    }
                                    // Checking Ignore-Permissions.
                                    ignorePerms = playerStatusMap.getIgnorePerms();
                                    if (!ignorePerms.isEmpty()) {
                                        if (isPerms(player, ignorePerms)) {
                                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            continue back;
                                        }
                                    }
                                    // Checking Residence flag "Fly".
                                    if (playerStatusMap.isFlyRes()) {
                                        if (isFlyRes(player)) {
                                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Residence",
                                                    new Throwable().getStackTrace()[0]);
                                            continue back;
                                        }
                                    }
                                    // Checking CMI "tfly" and "cfly".
                                    if (playerStatusMap.isFlyCMIC() || playerStatusMap.isFlyCMIT()) {
                                        CMIUser user;
                                        try {
                                            user = CMI.getInstance().getPlayer().getUser(player);
                                        } catch (Exception ex) {
                                            continue back;
                                        }
                                        if (isFlyCMI(user, playerStatusMap.isFlyCMIC(), playerStatusMap.isFlyCMIT())) {
                                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "CMIFlyC",
                                                    new Throwable().getStackTrace()[0]);
                                            continue back;
                                        }
                                    }
                                    // Cancel
                                    player.setFlying(false);
                                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    continue back;
                                case "god":
                                    // Getting the CMi user.
                                    CMIUser user;
                                    try {
                                        user = CMI.getInstance().getPlayer().getUser(player);
                                    } catch (Exception ex) {
                                        continue back;
                                    }
                                    // Checking the player status is enabled.
                                    if (!user.isGod()) {
                                        continue back;
                                    }
                                    // Checking Location.
                                    if (!ConfigPath.getLocationUtils().checkLocation(loc, playerStatusMap.getLocMaps())) {
                                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                                new Throwable().getStackTrace()[0]);
                                        continue;
                                    }
                                    // Checking Ignore-Permissions.
                                    ignorePerms = playerStatusMap.getIgnorePerms();
                                    if (!ignorePerms.isEmpty()) {
                                        if (isPerms(player, ignorePerms)) {
                                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            continue back;
                                        }
                                    }
                                    // Checking cmi "tgod".
                                    if (playerStatusMap.isGodCMIT()) {
                                        if (isGodCMI(user)) {
                                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "CMIGodT",
                                                    new Throwable().getStackTrace()[0]);
                                            continue back;
                                        }
                                    }
                                    // Cancel
                                    user.setGod(false);
                                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    return;
                                case "op":
                                    // Checking the player status is enabled.
                                    if (!player.isOp()) {
                                        continue back;
                                    }
                                    // Checking Location.
                                    if (!ConfigPath.getLocationUtils().checkLocation(loc, playerStatusMap.getLocMaps())) {
                                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                                new Throwable().getStackTrace()[0]);
                                        continue;
                                    }
                                    // Checking Ignore-Permissions.
                                    ignorePerms = playerStatusMap.getIgnorePerms();
                                    if (!ignorePerms.isEmpty()) {
                                        if (isPerms(player, ignorePerms)) {
                                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            continue back;
                                        }
                                    }
                                    // Cancel
                                    player.setOp(false);
                                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    continue back;
                                case "gamemode-survival":
                                case "gamemode-creative":
                                case "gamemode-adventure":
                                case "gamemode-spectator":
                                    // Checking the player status is enabled.
                                    if (!player.getGameMode().name().equalsIgnoreCase(groupName)) {
                                        continue back;
                                    }
                                    // Checking Location.
                                    if (!ConfigPath.getLocationUtils().checkLocation(loc, playerStatusMap.getLocMaps())) {
                                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                                new Throwable().getStackTrace()[0]);
                                        continue;
                                    }
                                    // Checking Ignore-Permissions.
                                    ignorePerms = playerStatusMap.getIgnorePerms();
                                    if (!ignorePerms.isEmpty()) {
                                        if (isPerms(player, ignorePerms)) {
                                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                                    new Throwable().getStackTrace()[0]);
                                            continue back;
                                        }
                                    }
                                    // Cancel
                                    player.setGameMode(GameMode.valueOf(playerStatusMap.getGmDefault().toUpperCase()));
                                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                                            new Throwable().getStackTrace()[0]);
                                    continue back;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }.runTaskTimer(PlayerdataPlus.getInstance(), 10, ConfigHandler.getConfigPath().getPsSchdeuleInterval());
            ServerHandler.sendConsoleMessage("&6Fly-Status process has ended.");
        } else {
            ServerHandler.sendConsoleMessage("&6Fly-Status checking is disabled");
        }
    }

    public static boolean isSchedule() {
        return schedule;
    }

    public static void setSchedule(boolean schedule) {
        PlayerStatusControl.schedule = schedule;
    }

    public static boolean isPerms(Player player, List<String> ignorePerms) {
        for (String perms : ignorePerms) {
            if (CorePlusAPI.getPlayer().hasPerm(player, perms)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFlyRes(Player player) {
        ClaimedResidence res = Residence.getInstance().getResidence().getByLoc(player);
        if (res != null) {
            return res.getPermissions().playerHas(player, Flags.fly, false);
        }
        return true;
    }

    public static boolean isFlyCMI(CMIUser user, boolean c, boolean t) {
        if (c) {
            return CorePlusAPI.getPlayer().hasPerm(user.getPlayer(), "cmi.command.flyc");
        }
        if (t) {
            return user.getTfly() > 0;
        }
        return false;
    }

    public static boolean isGodCMI(CMIUser user) {
        return user.getTgod() > 0;
    }
}
