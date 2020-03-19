package tw.momocraft.playerdataplus.utils;

import com.Zrips.CMI.CMI;
import com.nametagedit.plugin.NametagEdit;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Nick {

    /**
     * Changing player's nick.
     * Using command: /nickplus nick <nickName> [color]
     *
     * @param sender    the sender of this method.
     * @param bypass    bypass the nick limits.
     * @param nickName  the new nick name.
     * @param nickColor the new nick color.
     */
    public static void setNick(CommandSender sender, boolean bypass, String nickName, String nickColor) {
        if (sender instanceof ConsoleCommandSender) {
            Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
            Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOffOther", sender);
            return;
        }
        Player player = (Player) sender;
        String playerName = player.getName();
        if (bypass && !PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.*", false)) {
            if (nickColor == null) {
                nickColor = getDefaultColor(player);
            } else if (!getColorPerm(player, nickColor)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidColor", sender);
                return;
            }
            if (!getLength(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidLength", sender);
                return;
            }
            if (!getPureColor(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidColorInside", sender);
                return;
            }
            if (!getBlackList(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidNick", sender);
                return;
            }
        } else {
            if (nickColor == null) {
                nickColor = getDefaultColor(player);
            }
        }
        getFormatting("On", player, playerName, nickName, nickColor);
        String[] placeHolders = Language.newString();
        placeHolders[1] = playerName;
        placeHolders[3] = nickName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOn", sender, placeHolders);
    }

    /**
     * Changing player's nick.
     * Using command: /nickplus nick <nickName> [color] [player]
     *
     * @param sender    the sender of this method.
     * @param player    the changing nick target.
     * @param bypass    bypass the nick limits.
     * @param nickName  the new nick name.
     * @param nickColor the new nick color.
     */
    public static void setNick(CommandSender sender, Player player, boolean bypass, String nickName, String nickColor) {
        String playerName = player.getName();
        String[] placeHolders = Language.newString();
        placeHolders[2] = playerName;
        if (bypass && !PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.*", false)) {
            if (nickColor == null) {
                nickColor = getDefaultColor(player);
            } else {
                if (!getColorPerm(player, nickColor)) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidColor", sender);
                    return;
                }
            }
            if (!getLength(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidLength", sender, placeHolders);
                return;
            }
            if (!getPureColor(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidColorInside", sender, placeHolders);
                return;
            }
            if (!getBlackList(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidNick", sender, placeHolders);
                return;
            }
        } else {
            if (nickColor == null) {
                nickColor = getDefaultColor(player);
            }
        }
        getFormatting("On", player, playerName, nickName, nickColor);
        placeHolders[1] = playerName;
        placeHolders[2] = playerName;
        placeHolders[3] = nickName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOn", player, placeHolders);
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetSuccessOn", sender, placeHolders);
    }

    // Command: /nickplus nick off [player]
    public static void setNickOff(CommandSender sender) {
        Player player = (Player) sender;
        String playerName = player.getName();
        String nickColor = getDefaultColor(player);
        getFormatting("Off", player, playerName, null, nickColor);
        String[] placeHolders = Language.newString();
        placeHolders[1] = playerName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOff", player, placeHolders);
    }

    // Command: /nickplus nick off [player]
    public static void setNickOff(CommandSender sender, Player player) {
        String playerName = player.getName();
        String nickColor = getDefaultColor(player);
        getFormatting("Off", player, playerName, null, nickColor);
        String[] placeHolders = Language.newString();
        placeHolders[1] = playerName;
        placeHolders[2] = playerName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOff", player, placeHolders);
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetSuccessOff", sender, placeHolders);
    }

    public static void setColor(CommandSender sender, String nickColor) {
        if (sender instanceof ConsoleCommandSender) {
            Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
            Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOffOther", sender);
            return;
        }
        Player player = (Player) sender;
        String playerName = player.getName();
        if (ConfigHandler.getDepends().CMIEnabled() && ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Enable")) {
            String cmiFormat = CMI.getInstance().getPlayerManager().getUser(player).getNickName();
            cmiFormat = cmiFormat.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
            CMI.getInstance().getPlayerManager().getUser(player).setNickName(cmiFormat, true);
            if (ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Tablist-Update")) {
                CMI.getInstance().getTabListManager().updateTabList();
            }
            ServerHandler.debugMessage("Nick", playerName, "CMI", "setColor", cmiFormat);
        }
        if (ConfigHandler.getDepends().NameTagEditEnabled() && ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.NameTagEdit.Enable")) {
            String nteFormatPrefix = NametagEdit.getApi().getNametag(player).getPrefix();
            String nteFormatSuffix = NametagEdit.getApi().getNametag(player).getSuffix();
            nteFormatPrefix = nteFormatPrefix.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
            nteFormatSuffix = nteFormatSuffix.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
            NametagEdit.getApi().setNametag(player.getName(), nteFormatPrefix, nteFormatSuffix);
            ServerHandler.debugMessage("Nick", playerName, "NameEditTag", "setColor", nteFormatPrefix + playerName + nteFormatSuffix);
        }
    }

    public static String getDefaultColor(CommandSender sender) {
        String defaultColor = ConfigHandler.getConfig("config.yml").getString("Nick.Groups.Default");
        ConfigurationSection groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Nick.Groups.Custom");
        Set<String> groupSet;
        if (groupConfig != null) {
            groupSet = groupConfig.getKeys(false);
        } else {
            return defaultColor;
        }
        List<Long> groupList = new ArrayList<>();
        for (String group : groupSet) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.nick.color." + group)) {
                groupList.add(Long.valueOf(group));
            }
        }
        if (groupList.isEmpty()) {
            return defaultColor;
        }
        Long maxGroup = Collections.max(groupList);
        String customColor;
        while (true) {
            customColor = ConfigHandler.getConfig("config.yml").getString("Nick.Groups.Custom." + maxGroup);
            if (customColor != null) {
                return customColor;
            }
            groupList.remove(maxGroup);
            if (groupList.isEmpty()) {
                return defaultColor;
            }
            maxGroup = Collections.max(groupList);
        }
    }

    private static boolean getLength(Player player, String nickName) {
        if (!PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.length", false)) {
            int nickLength = ConfigHandler.getConfig("config.yml").getInt("Nick.Limits.Length");
            return nickName.length() <= nickLength;
        }
        return true;
    }

    private static boolean getPureColor(Player player, String nickName) {
        if (!PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.purecolor", false)) {
            if (ConfigHandler.getConfig("config.yml").getBoolean("Nick.Limits.Pure-Color")) {
                return !nickName.contains("§") && !nickName.contains("&");
            } else {
                String[] nickNameSplit = nickName.split("[&§]");
                if (nickNameSplit.length != 1) {
                    for (String split : nickNameSplit) {
                        if (!PermissionsHandler.hasPermission(player, "playerdataplus.nick.color." + split.charAt(0))
                                && !getDefaultColor(player).equals(String.valueOf(split.charAt(0)))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean getBlackList(Player player, String nickName) {
        if (!PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.blacklist", false)) {
            for (String key : ConfigHandler.getConfig("config.yml").getStringList("Nick.Limits.Black-List")) {
                if (nickName.contains(key)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean getColorPerm(Player player, String nickColor) {
        if (nickColor != null) {
            return PermissionsHandler.hasPermission(player, "playerdataplus.nick.color." + nickColor, false)
                    || getDefaultColor(player).equals(nickColor);
        }
        return true;
    }

    private static void getFormatting(String toggle, Player player, String playerName, String nickName, String nickColor) {
        if (ConfigHandler.getDepends().CMIEnabled() && ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Enable")) {
            String cmiFormat = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI." + toggle);
            if (cmiFormat != null) {
                cmiFormat = getCmdReplace(cmiFormat, playerName, nickName, nickColor, player);
            } else {
                if (toggle.equals("On")) {
                    cmiFormat = "§" + nickColor + nickName + "(" + playerName + ")";
                } else {
                    cmiFormat = "§" + nickColor + playerName;
                }
            }
            CMI.getInstance().getPlayerManager().getUser(player).setNickName(cmiFormat, true);
            if (ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Tablist-Update")) {
                CMI.getInstance().getTabListManager().updateTabList();
            }
            ServerHandler.debugMessage("Nick", playerName, "CMI", "set", cmiFormat);
        }
        if (ConfigHandler.getDepends().NameTagEditEnabled() && ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.NameTagEdit.Enable")) {
            String nteFormatPrefix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit." + toggle + ".Prefix");
            String nteFormatSuffix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit." + toggle + ".Suffix");
            if (nteFormatPrefix != null) {
                nteFormatPrefix = getCmdReplace(nteFormatPrefix, playerName, nickName, nickColor, player);
            } else {
                if (toggle.equals("On")) {
                    nteFormatPrefix = "§" + nickColor + nickName + " ";
                } else {
                    nteFormatPrefix = "§" + nickColor;
                }
            }
            if (nteFormatSuffix != null) {
                nteFormatSuffix = getCmdReplace(nteFormatSuffix, playerName, nickName, nickColor, player);
            } else {
                if (toggle.equals("On")) {
                    nteFormatSuffix = "";
                } else {
                    nteFormatSuffix = "";
                }
            }
            NametagEdit.getApi().setNametag(player, nteFormatPrefix, nteFormatSuffix);
            ServerHandler.debugMessage("Nick", playerName, "NameEditTag", "set", nteFormatPrefix + playerName + nteFormatSuffix);
        }
        List<String> cmdList;
        if (toggle.equals("On")) {
            cmdList = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands");
        } else {
            cmdList = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands-Off");
        }
        for (String command : cmdList) {
            command = getCmdReplace(command, playerName, nickName, nickColor, player);
            ServerHandler.executeCommands(player, command);
            ServerHandler.debugMessage("Nick", playerName, "commands", "set", command);
        }
    }

    private static String getCmdReplace(String command, String playerName, String nickName, String nickColor, Player player) {
        command = command.replace("%player%", playerName);
        command = command.replace("%nick%", nickName);
        command = command.replace("%color%", nickColor);
        command = command.replace("&", "§");
        command = PlaceholderAPI.setPlaceholders(player, command);
        return command;
    }
}
