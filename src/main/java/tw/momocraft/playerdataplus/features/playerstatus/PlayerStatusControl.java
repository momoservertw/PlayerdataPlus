package tw.momocraft.playerdataplus.features.playerstatus;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.UtilsHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerStatusControl {
    /*

    private static boolean schedule = false;

    public static void startSchedule(boolean enable) {
        schedule = enable;
        if (!schedule)
            return;
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                ConfigHandler.getConfigPath().getMsgPSScheduleStart(), Bukkit.getConsoleSender());
        new BukkitRunnable() {
            List<Player> playerList;

            @Override
            public void run() {
                if (!schedule) {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                            ConfigHandler.getConfigPath().getMsgPSScheduleEnd(), Bukkit.getConsoleSender());
                    cancel();
                    return;
                }
                playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
                new BukkitRunnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        check(playerList.get(i));
                        i++;
                    }
                }.runTaskTimer(PlayerdataPlus.getInstance(), 0, 5);
            }
        }.runTaskTimer(PlayerdataPlus.getInstance(),
                0, ConfigHandler.getConfigPath().getPsCheckScheduleInterval());
    }

    public static void check(Player player) {
        if (player == null)
            return;
        Map<String, PlayerStatusMap> playerStatusProp = ConfigHandler.getConfigPath().getPlayerStatusProp();
        if (playerStatusProp == null)
            return;
        String playerName = player.getName();
        Location loc = player.getLocation();
        PlayerStatusMap playerStatusMap;
        back:
        for (String groupName : playerStatusProp.keySet()) {
            playerStatusMap = playerStatusProp.get(groupName);
            // Check Location.
            if (!CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(),
                    loc, playerStatusMap.getLocList(), true)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Player-Status" + groupName, "location", "continue", playerName,
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            switch (groupName) {
                case "op" -> {
                    if (!player.isOp())
                        continue back;
                    if (isAllowOP(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status", groupName, "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    player.setOp(false);
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Player-Status", groupName, "remove", playerName,
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                case "gamemode" -> {
                    String defaultGameMode = ConfigHandler.getConfigPath().getPsGMDefault();
                    if (!player.getGameMode().name().equals(defaultGameMode))
                        continue back;
                    if (isAllowGM(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status", groupName, "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    player.setGameMode(GameMode.valueOf(defaultGameMode));
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Player-Status", groupName, "remove", playerName,
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                case "fly" -> {
                    if (!player.isFlying())
                        continue back;
                    if (isAllowFly(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status", groupName, "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    player.setFlying(false);
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Player-Status", groupName, "remove", playerName,
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                case "god" -> {
                    if (!UtilsHandler.getDepend().CMIEnabled())
                        continue back;
                    CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
                    if (!user.isGod())
                        continue back;
                    if (isAllowGod(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status", groupName, "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    user.setGod(false);
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Player-Status", groupName, "remove", playerName,
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                default -> {
                    CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                            "Can not find the player status type: " + groupName);
                    return;
                }
            }
        }
    }

    public static boolean isAllowFly(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.fly", false))
            return true;
        if (UtilsHandler.getDepend().ResidenceEnabled())
            return CorePlusAPI.getCond().checkFlag(player, player.getLocation(), "fly", true);
        if (UtilsHandler.getDepend().CMIEnabled()) {
            if (CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.fly", false))
                return true;
            return CMI.getInstance().getPlayerManager().getUser(player).getTgod() > 0;
        }
        return false;
    }

    public static boolean isAllowGod(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.god", false))
            return true;
        if (UtilsHandler.getDepend().CMIEnabled()) {
            if (CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.god", false))
                return true;
            return CMI.getInstance().getPlayerManager().getUser(player).getTgod() > 0;
        }
        return false;
    }

    public static boolean isAllowOP(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.op", false))
            return true;
        return CorePlusAPI.getPlayer().hasPerm(player, "minecraft.command.op", false);
    }

    public static boolean isAllowGM(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.gamemode", false))
            return true;
        if (CorePlusAPI.getPlayer().hasPerm(player, "minecraft.command.gamemode", false))
            return true;
        if (UtilsHandler.getDepend().CMIEnabled()) {
            if (CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.gm.*", false))
                return true;
            return CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.gm." + player.getGameMode().name().toLowerCase(), false);
        }
        return false;
    }

     */
}
