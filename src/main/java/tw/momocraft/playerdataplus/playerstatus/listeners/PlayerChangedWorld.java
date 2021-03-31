package tw.momocraft.playerdataplus.playerstatus.listeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import tw.momocraft.playerdataplus.playerstatus.PlayerStatusControl;
import tw.momocraft.playerdataplus.playerstatus.PlayerStatusMap;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.utils.ConfigPath;

import java.util.List;
import java.util.Map;

public class PlayerChangedWorld implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        if (!ConfigHandler.getConfigPath().isPlayerStatus()) {
            return;
        }
        if (!ConfigHandler.getConfigPath().isPsWorldChange()) {
            return;
        }
        Player player = e.getPlayer();
        String playerName = player.getName();
        Location loc = player.getLocation();
        // Checking the enable worlds.
        Map<String, PlayerStatusMap> playerStatusProp = ConfigHandler.getConfigPath().getPlayerStatusProp();
        if (playerStatusProp != null) {
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
                            CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                    new Throwable().getStackTrace()[0]);
                            continue;
                        }
                        // Checking Ignore-Permissions.
                        ignorePerms = playerStatusMap.getIgnorePerms();
                        if (!ignorePerms.isEmpty()) {
                            if (PlayerStatusControl.isPerms(player, ignorePerms)) {
                                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                        new Throwable().getStackTrace()[0]);
                                continue back;
                            }
                        }
                        // Checking Residence flag "Fly".
                        if (playerStatusMap.isFlyRes()) {
                            if (PlayerStatusControl.isFlyRes(player)) {
                                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Residence",
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
                            if (PlayerStatusControl.isFlyCMI(user, playerStatusMap.isFlyCMIC(), playerStatusMap.isFlyCMIT())) {
                                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "CMIFlyC",
                                        new Throwable().getStackTrace()[0]);
                                continue back;
                            }
                        }
                        // Cancel
                        player.setFlying(false);
                        CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
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
                            CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                    new Throwable().getStackTrace()[0]);
                            continue;
                        }
                        // Checking Ignore-Permissions.
                        ignorePerms = playerStatusMap.getIgnorePerms();
                        if (!ignorePerms.isEmpty()) {
                            if (PlayerStatusControl.isPerms(player, ignorePerms)) {
                                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                        new Throwable().getStackTrace()[0]);
                                continue back;
                            }
                        }
                        // Checking cmi "tgod".
                        if (playerStatusMap.isGodCMIT()) {
                            if (PlayerStatusControl.isGodCMI(user)) {
                                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "CMIGodT",
                                        new Throwable().getStackTrace()[0]);
                                continue back;
                            }
                        }
                        // Cancel
                        user.setGod(false);
                        CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                                new Throwable().getStackTrace()[0]);
                        return;
                    case "op":
                        // Checking the player status is enabled.
                        if (!player.isOp()) {
                            continue back;
                        }
                        // Checking Location.
                        if (!ConfigPath.getLocationUtils().checkLocation(loc, playerStatusMap.getLocMaps())) {
                            CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                    new Throwable().getStackTrace()[0]);
                            continue;
                        }
                        // Checking Ignore-Permissions.
                        ignorePerms = playerStatusMap.getIgnorePerms();
                        if (!ignorePerms.isEmpty()) {
                            if (PlayerStatusControl.isPerms(player, ignorePerms)) {
                                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                        new Throwable().getStackTrace()[0]);
                                continue back;
                            }
                        }
                        // Cancel
                        player.setOp(false);
                        CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
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
                            CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, "world-change", "Location", "continue", groupName,
                                    new Throwable().getStackTrace()[0]);
                            continue;
                        }
                        // Checking Ignore-Permissions.
                        ignorePerms = playerStatusMap.getIgnorePerms();
                        if (!ignorePerms.isEmpty()) {
                            if (PlayerStatusControl.isPerms(player, ignorePerms)) {
                                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "bypass", "Permissions",
                                        new Throwable().getStackTrace()[0]);
                                continue back;
                            }
                        }
                        // Cancel
                        player.setGameMode(GameMode.valueOf(playerStatusMap.getGmDefault().toUpperCase()));
                        CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    default:
                        break;
                }
            }
        }
    }
}
