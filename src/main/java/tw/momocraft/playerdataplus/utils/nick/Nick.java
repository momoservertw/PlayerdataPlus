package tw.momocraft.playerdataplus.utils.nick;

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
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Nick implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!ConfigHandler.getConfigPath().isNick())
            return;
        updateNickName(e.getPlayer());
    }

    public static void clearNick(CommandSender sender, String targetName, boolean sendMessage) {
        NickMap nickMap = new NickMap();
        nickMap.setSentBySelf(true);

        Player player;
        String playerName;
        UUID uuid;
        if (targetName == null) {
            if (sender instanceof ConsoleCommandSender) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.onlyPlayer", sender);
                return;
            }
            nickMap.setSentBySelf(true);
            player = (Player) sender;
            playerName = player.getName();
            uuid = player.getUniqueId();
            nickMap.setPlayer(player);
            nickMap.setPlayerName(playerName);
            nickMap.setPlayerUUID(uuid);
        } else {
            nickMap.setSentBySelf(false);
            nickMap.setPlayerName(targetName);
            player = CorePlusAPI.getPlayer().getPlayer(targetName);
            if (player != null) {
                playerName = player.getName();
                uuid = player.getUniqueId();
                nickMap.setPlayer(player);
                nickMap.setPlayerName(playerName);
                nickMap.setPlayerUUID(uuid);
            } else {
                OfflinePlayer offlinePlayer = CorePlusAPI.getPlayer().getOfflinePlayer(targetName);
                if (offlinePlayer != null) {
                    playerName = offlinePlayer.getName();
                    uuid = offlinePlayer.getUniqueId();
                    nickMap.setOfflinePlayer(offlinePlayer);
                    nickMap.setPlayerName(playerName);
                    nickMap.setPlayerUUID(uuid);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                            "Message.targetNotFound", player);
                    return;
                }
            }
        }
        // Setup the placeholders.
        String[] placeHolders = CorePlusAPI.getMsg().newString();
        placeHolders[0] = playerName; // %player%
        placeHolders[1] = playerName; // %targetplayer%
        placeHolders[21] = playerName; // %nick%
        placeHolders[16] = "f"; // %color%
        // Check bypass permission.
        // Change nick.
        clearCMI(nickMap);
        clearDiscord(nickMap);
        setCmd(nickMap, false);

        // Send messages.
        if (!sendMessage)
            return;
        if (nickMap.isSentBySelf()) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgNickClear(), player, placeHolders);
        } else {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgNickClear(), player, placeHolders);
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgNickClearTarget(), sender, placeHolders);
        }
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "Nick", playerName, "clear", "return",
                new Throwable().getStackTrace()[0]);
    }

    public static void changeNick(CommandSender sender, String targetName, String nickName, String nickColor, boolean sendMessage) {
        NickMap nickMap = new NickMap();
        nickMap.setNickName(nickName);
        nickMap.setSentBySelf(true);

        Player player;
        String playerName;
        UUID uuid;
        if (targetName == null) {
            if (sender instanceof ConsoleCommandSender) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.onlyPlayer", sender);
                return;
            }
            nickMap.setSentBySelf(true);
            player = (Player) sender;
            playerName = player.getName();
            uuid = player.getUniqueId();
            nickMap.setPlayer(player);
            nickMap.setPlayerName(playerName);
            nickMap.setPlayerUUID(uuid);
        } else {
            nickMap.setSentBySelf(false);
            nickMap.setPlayerName(targetName);
            player = CorePlusAPI.getPlayer().getPlayer(targetName);
            if (player != null) {
                playerName = player.getName();
                uuid = player.getUniqueId();
                nickMap.setPlayer(player);
                nickMap.setPlayerName(playerName);
                nickMap.setPlayerUUID(uuid);
            } else {
                OfflinePlayer offlinePlayer = CorePlusAPI.getPlayer().getOfflinePlayer(targetName);
                if (offlinePlayer != null) {
                    playerName = offlinePlayer.getName();
                    uuid = offlinePlayer.getUniqueId();
                    nickMap.setOfflinePlayer(offlinePlayer);
                    nickMap.setPlayerName(playerName);
                    nickMap.setPlayerUUID(uuid);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                            "Message.targetNotFound", player);
                    return;
                }
            }
        }
        if (nickColor == null)
            nickColor = getDefaultColor(uuid);
        nickMap.setNickColor(nickColor);
        // Setup the placeholders.
        String[] placeHolders = CorePlusAPI.getMsg().newString();
        placeHolders[0] = playerName; // %player%
        placeHolders[1] = playerName; // %targetplayer%
        placeHolders[21] = nickName != null ? nickName : ""; // %nick%
        placeHolders[16] = nickColor; // %color%
        // Check bypass permission.
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.*")) {
            // Change nick.
            setCMI(nickMap);
            setDiscord(nickMap);
            setCmd(nickMap, true);
            saveNickName(uuid, nickName);
            // Send messages.
            if (!sendMessage)
                return;
            if (nickMap.isSentBySelf()) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickChange(), player, placeHolders);
            } else {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickChange(), player, placeHolders);
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickChangeTarget(), sender, placeHolders);
            }
            return;
        }
        // Check color permission.
        if (!checkColorPerm(uuid, nickColor)) {
            if (!sendMessage)
                return;
            if (nickMap.isSentBySelf())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidColor(), player, placeHolders);
            else
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidColorTarget(), sender, placeHolders);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Nick", playerName, "color permission", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Check nick length.
        if (!checkLimitLength(uuid, nickName)) {
            if (!sendMessage)
                return;
            if (nickMap.isSentBySelf())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidLength(), player, placeHolders);
            else
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidLengthTarget(), sender, placeHolders);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Nick", playerName, "length", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Check contains color code.
        if (!checkColorCode(uuid, nickName)) {
            if (!sendMessage)
                return;
            if (nickMap.isSentBySelf())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidColorInside(), player, placeHolders);
            else
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidColorInsideTarget(), sender, placeHolders);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Nick", playerName, "color code", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Check black list.
        if (!checkLimitBlackList(uuid, nickName)) {
            if (!sendMessage)
                return;
            if (nickMap.isSentBySelf())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidNick(), player, placeHolders);
            else
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickInvalidNickTarget(), sender, placeHolders);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Nick", playerName, "block list", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Change nick name.
        setCMI(nickMap);
        setDiscord(nickMap);
        setCmd(nickMap, true);
        saveNickName(uuid, nickName);
        // Send messages.
        if (!sendMessage)
            return;
        if (nickMap.isSentBySelf()) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgNickChange(), player, placeHolders);
        } else {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgNickChange(), player, placeHolders);
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgNickChangeTarget(), sender, placeHolders);
        }
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "Nick", playerName, "change", "return",
                new Throwable().getStackTrace()[0]);
    }

    private static void setCMI(NickMap nickMap) {
        if (!CorePlusAPI.getDepend().CMIEnabled())
            return;
        if (!ConfigHandler.getConfigPath().isNickCMI())
            return;
        String nickFormat = ConfigHandler.getConfigPath().getNickCMINickSet();
        String platePrefix = ConfigHandler.getConfigPath().getNickCMIPlatePrefix();
        String plateSuffix = ConfigHandler.getConfigPath().getNickCMIPlateSuffix();
        String plateColor = ConfigHandler.getConfigPath().getNickCMIPlateColor();
        // Translate placeholder of format.
        Player player = nickMap.getPlayer();
        if (player != null) {
            nickFormat = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", nickFormat);
            platePrefix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", platePrefix);
            plateSuffix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", plateSuffix);
            plateColor = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", plateColor);
        } else {
            OfflinePlayer offlinePlayer = nickMap.getOfflinePlayer();
            if (offlinePlayer != null) {
                nickFormat = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", nickFormat);
                platePrefix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", platePrefix);
                plateSuffix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", plateSuffix);
                plateColor = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", plateColor);
            }
        }
        // Translate placeholder of nick.
        nickFormat = translate(nickMap, nickFormat);
        platePrefix = translate(nickMap, platePrefix);
        plateSuffix = translate(nickMap, plateSuffix);
        // Change CMI nick.
        try {
            CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(nickMap.getPlayerName());
            cmiUser.setNickName(nickFormat, true);
            cmiUser.setNamePlatePrefix(platePrefix);
            cmiUser.setNamePlateSuffix(plateSuffix);
            cmiUser.setNamePlateNameColor(ChatColor.valueOf(plateColor));
        } catch (Exception ex) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not set the color of player name plate.");
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Available color: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html");
        }
        // Update TabList.
        if (ConfigHandler.getConfigPath().isNickCMIUpdateTabList())
            CMI.getInstance().getTabListManager().updateTabList();
    }

    private static void setDiscord(NickMap nickMap) {
        if (!CorePlusAPI.getDepend().DiscordSRVEnabled())
            return;
        if (!ConfigHandler.getConfigPath().isNickDiscordSRV())
            return;
        String format = ConfigHandler.getConfigPath().getNickDiscordSRVSet();
        Player player = nickMap.getPlayer();
        if (player != null) {
            format = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", format);
        } else {
            OfflinePlayer offlinePlayer = nickMap.getOfflinePlayer();
            format = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), offlinePlayer, "player", format);
        }
        format = translate(nickMap, format);
        CorePlusAPI.getPlayer().setDiscordNick(nickMap.getPlayerUUID(), format);
    }

    private static void setCmd(NickMap nickMap, boolean set) {
        List<String> cmdList;
        if (set)
            cmdList = ConfigHandler.getConfigPath().getNickCommandSet();
        else
            cmdList = ConfigHandler.getConfigPath().getNickCommandClear();
        if (cmdList == null)
            return;
        Player player = nickMap.getPlayer();
        OfflinePlayer offlinePlayer = nickMap.getOfflinePlayer();
        for (String command : cmdList) {
            command = translate(nickMap, command);
            if (player != null) {
                command = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", command);
                CorePlusAPI.getCmd().sendCmd(ConfigHandler.getPrefix(), player, command);
            } else {
                command = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), offlinePlayer, "player", command);
                CorePlusAPI.getCmd().sendCmd(ConfigHandler.getPrefix(), command);
            }
        }
    }

    private static void clearCMI(NickMap nickMap) {
        if (!CorePlusAPI.getDepend().CMIEnabled())
            return;
        if (!ConfigHandler.getConfigPath().isNickCMI())
            return;
        CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(nickMap.getPlayerUUID());
        cmiUser.setNickName(null, true);
        cmiUser.setNamePlatePrefix(null);
        cmiUser.setNamePlateSuffix(null);
        cmiUser.setNamePlateNameColor(ChatColor.WHITE);
        // Update TabList.
        if (ConfigHandler.getConfigPath().isNickCMIUpdateTabList())
            CMI.getInstance().getTabListManager().updateTabList(20);
    }

    private static void clearDiscord(NickMap nickMap) {
        if (!CorePlusAPI.getDepend().DiscordSRVEnabled())
            return;
        if (!ConfigHandler.getConfigPath().isNickDiscordSRV())
            return;
        UUID uuid = nickMap.getPlayerUUID();
        CorePlusAPI.getPlayer().setDiscordNick(uuid,
                CorePlusAPI.getPlayer().getDiscordName(uuid));
    }

    private static boolean checkLimitLength(UUID uuid, String nickName) {
        if (nickName == null)
            return true;
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.length"))
            return true;
        return nickName.length() <= ConfigHandler.getConfigPath().getNickLength();
    }

    private static boolean checkLimitBlackList(UUID uuid, String nickName) {
        if (nickName == null)
            return true;
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.blacklist"))
            return true;
        for (String key : ConfigHandler.getConfigPath().getNickBlackList())
            if (nickName.matches(key))
                return false;
        return true;
    }

    public static String getDefaultColor(UUID uuid) {
        Map<String, String> groupsMap = ConfigHandler.getConfigPath().getNickGroupsProp();
        for (String group : groupsMap.keySet())
            if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.nick.group." + group))
                return groupsMap.get(group);
        return groupsMap.keySet().iterator().next();
    }

    private static boolean checkColorCode(UUID uuid, String nickName) {
        if (nickName == null)
            return true;
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.colorcode"))
            return true;
        if (ConfigHandler.getConfigPath().isNickColorCode())
            return !CorePlusAPI.getUtils().containsColorCode(nickName);
        String[] split = nickName.split("[&§]");
        for (int i = 0; i <= split.length; i++) {
            if (i == 0)
                continue;
            if (!checkColorPerm(uuid, String.valueOf(split[i].charAt(0))))
                return false;
        }
        return true;
    }

    private static boolean checkColorPerm(UUID uuid, String nickColor) {
        if (nickColor == null)
            return true;
        return CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.nick.color." + nickColor)
                || getDefaultColor(uuid).equals(nickColor);
    }

    private static String translate(NickMap nickMap, String input) {
        String nickName = nickMap.getNickName();
        String nickColor = nickMap.getNickColor();
        input = input.replace("%player%", nickMap.getNickName());
        input = input.replace("%nick%", nickName != null ? nickName : "");
        input = input.replace("%color%", nickColor != null ? nickColor : "");
        return input;
    }

    private static void saveNickName(UUID uuid, String nickName) {
        if (!ConfigHandler.getConfigPath().isNickAutoUpdate())
            return;
        if (!CorePlusAPI.getConfig().isDataMySQL()) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not connect to MySQL.");
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "You should setup the settings in CorePlus/data.yml");
            return;
        }
        if (!CorePlusAPI.getFile().getMySQL().isConnect(ConfigHandler.getPluginName(), "coreplus")) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not connect to MySQL.");
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "You should setup the settings in CorePlus/data.yml");
            return;
        }
        CorePlusAPI.getFile().getMySQL().setValueWhere(ConfigHandler.getPluginName(),
                "coreplus", "player",
                "uuid", uuid.toString(), "display_name", nickName);
    }

    private void updateNickName(Player player) {
        if (!ConfigHandler.getConfigPath().isNickAutoUpdate())
            return;
        if (!CorePlusAPI.getConfig().isDataMySQL()) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not update the nick name from MySQL: CorePlus");
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "You should setup the settings in CorePlus/data.yml");
            return;
        }
        if (!CorePlusAPI.getFile().getMySQL().isConnect(ConfigHandler.getPluginName(), "coreplus")) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not update the nick name from MySQL: CorePlus");
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "You should setup the settings in CorePlus/data.yml");
            return;
        }
        String playerName = player.getName();
        String nickname = CorePlusAPI.getFile().getMySQL().getValueWhere(ConfigHandler.getPluginName(),
                "coreplus", "player",
                "uuid", player.getUniqueId().toString(), "display_name");
        if (nickname == null)
            clearNick(player, null, false);
        else
            changeNick(player, playerName, nickname, null, false);
    }
}
