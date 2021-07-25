package tw.momocraft.playerdataplus.features;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.utils.message.TranslateMap;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Nick implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!ConfigHandler.getConfigPath().isNick())
            return;
        if (!ConfigHandler.getConfigPath().isNickAutoUpdate())
            return;
        String nickname = CorePlusAPI.getFile().getMySQL().getValue(ConfigHandler.getPlugin(),
                "coreplus", "player", "display_name");
    }

    public static void setNick(CommandSender sender, String target, String nickName, String nickColor, boolean status) {
        Player player;
        String playerName;
        if (target == null) {
            if (sender instanceof ConsoleCommandSender) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(), "Message.onlyPlayer", sender);
                return;
            }
            player = (Player) sender;
            playerName = player.getName();
        } else {
            player = CorePlusAPI.getPlayer().getPlayer(target);
            playerName = target;
        }
        UUID uuid = CorePlusAPI.getPlayer().getPlayerUUID(playerName);
        if (nickColor == null)
            nickColor = getDefaultColor(uuid);
        if (nickName == null)
            nickName = playerName;
        String[] placeHolders = CorePlusAPI.getMsg().newString();
        placeHolders[2] = playerName;
        if (!CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.*")) {
            // Check color permission.
            if (!getColorPerm(uuid, nickColor)) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidColor(), player, placeHolders);
                if (target != null)
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidColorTarget(), sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Nick", playerName, "color permission", "cancel",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Check nick length.
            if (!getLength(uuid, nickName)) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidLength(), player, placeHolders);
                if (target != null)
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidLengthTarget(), sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Nick", playerName, "length", "cancel",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Check contains color code
            if (!getColorCode(uuid, nickName)) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidColorInside(), player, placeHolders);
                if (target != null)
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidColorInsideTarget(), sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Nick", playerName, "color color", "cancel",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Check black list
            if (!getBlackList(uuid, nickName)) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidNick(), player, placeHolders);
                if (target != null)
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidNickTarget(), sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Nick", playerName, "block list", "cancel",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        formatting(playerName, uuid, nickName, nickColor, status);
        placeHolders[1] = playerName;
        placeHolders[3] = nickName;
        placeHolders[4] = nickColor;
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                ConfigHandler.getConfigPath().getMsgNickChange(), player, placeHolders);
        if (target != null)
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(),
                    ConfigHandler.getConfigPath().getMsgNickChangeTarget(), sender, placeHolders);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick", playerName, "final", "return", "other",
                new Throwable().getStackTrace()[0]);
    }

    private static void formatting(String playerName, UUID uuid, String nickName, String nickColor, boolean status) {
        CMIFormatting(playerName, uuid, nickName, nickColor, status);
        CMDFormatting(playerName, nickName, nickColor, status);
    }

    private static void CMIFormatting(String playerName, UUID uuid, String nickName, String nickColor, boolean status) {
        if (!CorePlusAPI.getDepend().CMIEnabled() || ConfigHandler.getConfigPath().isNickCMI())
            return;
        String nickFormat;
        String platePrefix;
        String plateSuffix;
        String plateColor;
        if (status && nickName != null) {
            nickFormat = ConfigHandler.getConfigPath().getNickCMINickSet();
            platePrefix = ConfigHandler.getConfigPath().getNickCMIPlatePrefix();
            plateSuffix = ConfigHandler.getConfigPath().getNickCMIPlateSuffix();
            plateColor = ConfigHandler.getConfigPath().getNickCMIPlateColor();
        } else {
            nickFormat = playerName;
            platePrefix = "ConfigHandler.getConfigPath().getNickCMIPlatePrefix()";
            plateSuffix = "";
            plateColor = "WHITE";
        }
        Player player = CorePlusAPI.getPlayer().getPlayer(playerName);
        if (player == null) {
            OfflinePlayer offlinePlayer = CorePlusAPI.getPlayer().getOfflinePlayer(uuid);
            TranslateMap translateMap = CorePlusAPI.getMsg().getTranslateMap(null, offlinePlayer, "player");
            nickFormat = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), null, translateMap, nickFormat);
            platePrefix = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), null, translateMap, platePrefix);
            plateSuffix = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), null, translateMap, plateSuffix);
            plateColor = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), null, translateMap, plateColor);
        } else {
            TranslateMap translateMap = CorePlusAPI.getMsg().getTranslateMap(null, player, "player");
            nickFormat = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), player, translateMap, nickFormat);
            platePrefix = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), player, translateMap, platePrefix);
            plateSuffix = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), player, translateMap, plateSuffix);
            plateColor = CorePlusAPI.getMsg().transTranslateMap(ConfigHandler.getPlugin(), player, translateMap, plateColor);
        }
        CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(uuid);
        cmiUser.setNickName(nickFormat, true);
        cmiUser.setNamePlatePrefix(platePrefix);
        cmiUser.setNamePlateSuffix(plateSuffix);
        cmiUser.setNamePlateNameColor(ChatColor.valueOf(plateColor));
        if (ConfigHandler.getConfigPath().isNickCMIUpdateTabList())
            CMI.getInstance().getTabListManager().updateTabList(20);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                "Nick", playerName, "CMI", "change", nickFormat,
                new Throwable().getStackTrace()[0]);
    }

    private static void CMDFormatting(String playerName, String nickName, String nickColor, boolean status) {
        List<String> cmdList;
        if (status)
            cmdList = ConfigHandler.getConfigPath().getNickCommandSet();
        else
            cmdList = ConfigHandler.getConfigPath().getNickCommandClear();
        Player player = CorePlusAPI.getPlayer().getPlayer(playerName);
        for (String command : cmdList) {
            command = translate(command, playerName, nickName, nickColor);
            TranslateMap translateMap = CorePlusAPI.getMsg().getTranslateMap(null, player, "player");
            translateMap = CorePlusAPI.getMsg().getTranslateMap(translateMap, player, "player");
            CorePlusAPI.getCmd().sendCmd(ConfigHandler.getPrefix(),
                    null, translateMap, command, true);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                    "Nick", playerName, "commands", "change", command,
                    new Throwable().getStackTrace()[0]);
        }
    }

    public static String getDefaultColor(UUID uuid) {
        Map<String, String> groupsMap = ConfigHandler.getConfigPath().getNickGroupsProp();
        for (String group : groupsMap.keySet())
            if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.nick.group." + group))
                return groupsMap.get(group);
        return groupsMap.keySet().iterator().next();
    }

    private static boolean getLength(UUID uuid, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.length"))
            return true;
        return nickName.length() <= ConfigHandler.getConfigPath().getNickLength();
    }

    private static boolean getColorCode(UUID uuid, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.colorcode"))
            return true;
        if (ConfigHandler.getConfigPath().isNickColorCode()) {
            return !CorePlusAPI.getUtils().containsColorCode(nickName);
        } else {
            String[] split = nickName.split("[&§]");
            for (int i = 0; i <= split.length; i++) {
                if (i == 0)
                    continue;
                if (!getColorPerm(uuid, String.valueOf(split[i].charAt(0))))
                    return false;
            }
        }
        return true;
    }

    private static boolean getBlackList(UUID uuid, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.blacklist"))
            return true;
        for (String key : ConfigHandler.getConfigPath().getNickBlackList())
            if (nickName.matches(key))
                return false;
        return true;
    }

    private static boolean getColorPerm(UUID uuid, String nickColor) {
        return CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.nick.color." + nickColor)
                || getDefaultColor(uuid).equals(nickColor);
    }

    private static String translate(String command, String playerName, String nickName, String nickColor) {
        command = command.replace("%player%", playerName);
        command = command.replace("%nick%", nickName);
        command = command.replace("%color%", nickColor);
        return command;
    }
}
