package tw.momocraft.playerdataplus.features.nick;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.UtilsHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Nick implements Listener {

    /*
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!ConfigHandler.getConfigPath().isNick())
            return;
        updateNickName(e.getPlayer());
    }
     */

    public static void clear(CommandSender sender, String targetName, boolean sendMessage) {
        NickMap nickMap = new NickMap();

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
            // Setup the attributes.
            nickMap.setPlayer(player);
            nickMap.setPlayerName(playerName);
            nickMap.setPlayerUUID(uuid);
        } else {
            // Setup the attributes.
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
        // Change nick.
        clearCMI(nickMap);
        clearDiscord(nickMap);
        setCmd(nickMap, false);
        saveNick(uuid, null);

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

    public static void set(CommandSender sender, String targetName, String nickName, String nickColor, boolean sendMessage) {
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "Nick", "targetName: " + targetName + ", nickName: " + nickName + ", nickColor: " + nickColor,
                "set", "start",
                new Throwable().getStackTrace()[0]);

        Player player;
        OfflinePlayer offlinePlayer;
        String playerName;
        String displayName;
        UUID uuid;

        NickMap nickMap = new NickMap();
        nickMap.setSentBySelf(sender.getName().equals(targetName));
        player = CorePlusAPI.getPlayer().getPlayer(targetName);
        offlinePlayer = CorePlusAPI.getPlayer().getOfflinePlayer(targetName);
        if (player != null) {
            playerName = player.getName();
            uuid = player.getUniqueId();
            displayName = CorePlusAPI.getPlayer().getPlayerDisplayName(uuid);

            nickMap.setPlayer(player);
            nickMap.setPlayerUUID(uuid);
            nickMap.setPlayerName(playerName);
            nickMap.setDisplayName(displayName);
        } else if (offlinePlayer != null) {
            playerName = offlinePlayer.getName();
            uuid = offlinePlayer.getUniqueId();
            displayName = CorePlusAPI.getPlayer().getPlayerDisplayName(uuid);

            nickMap.setOfflinePlayer(offlinePlayer);
            nickMap.setPlayerUUID(uuid);
            nickMap.setPlayerName(playerName);
            nickMap.setDisplayName(displayName);
        } else {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                    "Message.targetNotFound", sender);
            return;
        }
        if (nickColor == null)
            nickColor = getDefaultColor(uuid);
        nickMap.setNickColor(nickColor);

        String[] placeHolders = CorePlusAPI.getMsg().newString();
        placeHolders[0] = playerName; // %player%
        placeHolders[1] = playerName; // %targetplayer%
        placeHolders[16] = nickColor; // %color%
        placeHolders[21] = displayName; // %nick%
        placeHolders[23] = String.valueOf(ConfigHandler.getConfigPath().getNickLength()); // %limit%

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
                    "Nick", playerName, "color", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Check nick length.
        if (!checkLength(uuid, nickName)) {
            if (sendMessage) {
                if (nickMap.isSentBySelf())
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidLength(), player, placeHolders);
                else
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidLengthTarget(), sender, placeHolders);
            }
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Nick", playerName, "length", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }

        // Check contains color code.
        nickName = checkColorCode(uuid, nickName);

        // Check contains placeholder.
        nickName = checkPlaceholder(sender, nickName);

        nickMap.setNickName(nickName);

        // Check black list.
        if (!checkBlackList(uuid, nickName)) {
            if (sendMessage) {
                if (nickMap.isSentBySelf())
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidNick(), player, placeHolders);
                else
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgNickInvalidNickTarget(), sender, placeHolders);
            }
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Nick", playerName, "block list", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Change nick name.
        setCMI(nickMap);
        setDiscord(nickMap);
        setCmd(nickMap, true);

        saveNick(uuid, nickMap);

        // Send messages.
        if (sendMessage) {
            displayName = ConfigHandler.getConfigPath().getNickMsg();
            displayName = translate(nickMap, displayName);
            placeHolders[21] = displayName; // %nick%
            if (nickMap.isSentBySelf()) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickChange(), player, placeHolders);
            } else {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickChange(), player, placeHolders);
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgNickChangeTarget(), sender, placeHolders);
            }
        }
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "Nick", playerName + ": " + displayName, "change", "return",
                new Throwable().getStackTrace()[0]);
    }

    private static void setCMI(NickMap nickMap) {
        if (!ConfigHandler.getConfigPath().isNickCMI())
            return;
        if (!CorePlusAPI.getDepend().CMIEnabled())
            return;
        if (!UtilsHandler.getDepend().CMIEnabled())
            return;
        String nickName = nickMap.getNickName();
        String nickColor = nickMap.getNickColor();

        CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(nickMap.getPlayerName());
        if (nickName == null && nickColor != null) {
            cmiUser.setNickName(CorePlusAPI.getUtils().removeColorCode(cmiUser.getDisplayName()), true);
            cmiUser.setNamePlatePrefix(CorePlusAPI.getUtils().removeColorCode(cmiUser.getNamePlatePrefix()));
            cmiUser.setNamePlateSuffix(CorePlusAPI.getUtils().removeColorCode(cmiUser.getNamePlateSuffix()));
            cmiUser.setNamePlateNameColor(CorePlusAPI.getUtils().getChatColor(nickMap.getNickColor()));
            return;
        }

        String nickFormat = ConfigHandler.getConfigPath().getNickCMINickSet();
        String platePrefix = ConfigHandler.getConfigPath().getNickCMIPlatePrefix();
        String plateSuffix = ConfigHandler.getConfigPath().getNickCMIPlateSuffix();
        String plateColor = ConfigHandler.getConfigPath().getNickCMIPlateColor();
        // Translate placeholder of format.
        Player player = nickMap.getPlayer();
        String playerName;
        if (player != null) {
            playerName = player.getName();
            nickFormat = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", nickFormat);
            platePrefix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", platePrefix);
            plateSuffix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", plateSuffix);
            plateColor = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, "player", plateColor);
        } else {
            OfflinePlayer offlinePlayer = nickMap.getOfflinePlayer();
            if (offlinePlayer != null) {
                playerName = offlinePlayer.getName();
                nickFormat = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), offlinePlayer, "player", nickFormat);
                platePrefix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), offlinePlayer, "player", platePrefix);
                plateSuffix = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), offlinePlayer, "player", plateSuffix);
                plateColor = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), offlinePlayer, "player", plateColor);
            } else {
                return;
            }
        }
        // Translate placeholder of nick.
        nickFormat = translate(nickMap, nickFormat);
        platePrefix = translate(nickMap, platePrefix);
        plateSuffix = translate(nickMap, plateSuffix);

        // Change CMI nick.
        try {
            cmiUser.setNickName(nickFormat, true);
            cmiUser.setNamePlatePrefix(platePrefix);
            cmiUser.setNamePlateSuffix(plateSuffix);
            cmiUser.setNamePlateNameColor(ChatColor.valueOf(plateColor));
        } catch (Exception ex) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not set the name plate for player \"" + playerName + "\".");
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Available color: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html");
        }
        // Update TabList.
        if (ConfigHandler.getConfigPath().isNickCMIUpdateTabList())
            CMI.getInstance().getTabListManager().updateTabList();
    }

    private static void setDiscord(NickMap nickMap) {
        if (!ConfigHandler.getConfigPath().isNickDiscordSRV())
            return;
        if (!CorePlusAPI.getDepend().DiscordSRVEnabled())
            return;
        if (!UtilsHandler.getDepend().CMIEnabled())
            return;
        String nickName = nickMap.getNickName();
        if (nickName == null)
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
        if (cmdList == null || cmdList.isEmpty())
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
        if (ConfigHandler.getConfigPath().isNickCMIUpdateTabList())
            CMI.getInstance().getTabListManager().updateTabList(20);
    }

    private static void clearDiscord(NickMap nickMap) {
        if (!ConfigHandler.getConfigPath().isNickDiscordSRV())
            return;
        if (!UtilsHandler.getDepend().DiscordSRVEnabled())
            return;
        UUID uuid = nickMap.getPlayerUUID();
        CorePlusAPI.getPlayer().setDiscordNick(uuid,
                CorePlusAPI.getPlayer().getDiscordName(uuid));
    }

    private static boolean checkLength(UUID uuid, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.length"))
            return true;
        return nickName.length() <= ConfigHandler.getConfigPath().getNickLength();
    }

    private static boolean checkBlackList(UUID uuid, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.blacklist"))
            return true;
        for (String key : ConfigHandler.getConfigPath().getNickBlackList())
            if (nickName.matches(key))
                return false;
        return true;
    }

    private static String checkPlaceholder(CommandSender sender, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.bypass.nick.placeholder"))
            return nickName;
        String[] split = nickName.split("%");
        if (split.length == 1)
            return nickName;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= split.length; i++) {
            if (i % 2 == 0)
                sb.append(split[i]);
        }
        return sb.toString();
    }

    private static String checkColorCode(UUID uuid, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.bypass.nick.colorcode"))
            return nickName;
        return CorePlusAPI.getUtils().removeColorCode(nickName);
    }

    private static boolean checkColorPerm(UUID uuid, String nickColor) {
        return CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.nick.color." + nickColor)
                || getDefaultColor(uuid).equals(nickColor);
    }

    public static String getDefaultColor(UUID uuid) {
        Map<String, String> groupsMap = ConfigHandler.getConfigPath().getNickGroupsProp();
        for (String group : groupsMap.keySet())
            if (CorePlusAPI.getPlayer().hasPerm(uuid, "playerdataplus.nick.group." + group))
                return groupsMap.get(group);
        return groupsMap.keySet().iterator().next();
    }

    private static String translate(NickMap nickMap, String input) {
        if (input == null)
            return null;
        String nickName = nickMap.getNickName();
        String nickColor = nickMap.getNickColor();
        String playerName = nickMap.getPlayerName();
        input = input.replace("%player%", playerName != null ? playerName : "");
        input = input.replace("%nick%", nickName != null ? nickName : "");
        input = input.replace("%color%", nickColor != null ? nickColor : "");
        return input;
    }

    private static void saveNick(UUID uuid, NickMap nickMap) {
        if (!CorePlusAPI.getConfig().isDataMySQL()) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not connect to MySQL.");
            return;
        }
        if (!CorePlusAPI.getFile().getMySQL().isConnect(ConfigHandler.getPluginName(),
                "playerdataplus")) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Can not connect to MySQL.");
            return;
        }

        String displayName = nickMap.getDisplayName();
        String nickName = nickMap.getNickName();
        String nickColor = nickMap.getNickColor();

        CorePlusAPI.getFile().getMySQL().setValueWhere(ConfigHandler.getPluginName(),
                "playerdataplus", "player",
                "uuid", uuid.toString(), "display_name", displayName);
        CorePlusAPI.getFile().getMySQL().setValueWhere(ConfigHandler.getPluginName(),
                "playerdataplus", "player",
                "uuid", uuid.toString(), "nick_name", nickName);
        CorePlusAPI.getFile().getMySQL().setValueWhere(ConfigHandler.getPluginName(),
                "playerdataplus", "player",
                "uuid", uuid.toString(), "nick_color", nickColor);
    }


    private void updateNickName(Player player) {
       /*
        if (!ConfigHandler.getConfigPath().isPlayerDataGroupNick())
            return;

        */
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
            clear(player, null, false);
        else
            set(player, playerName, nickname, null, false);
    }
}
