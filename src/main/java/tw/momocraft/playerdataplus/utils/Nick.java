package tw.momocraft.playerdataplus.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.nametagedit.plugin.NametagEdit;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

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
        if (!bypass && !PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.*")) {
            if (nickColor.equals("")) {
                nickColor = getDefaultColor(player);
            } else if (!getColorPerm(player, nickColor)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidColor", sender);
                ServerHandler.sendFeatureMessage("Nick-On", playerName, "colorPerm", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            if (!getLength(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidLength", sender);
                ServerHandler.sendFeatureMessage("Nick-On", playerName, "length", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            if (!getPureColor(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidColorInside", sender);
                ServerHandler.sendFeatureMessage("Nick-On", playerName, "pureColor", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            if (!getBlackList(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidNick", sender);
                ServerHandler.sendFeatureMessage("Nick-On", playerName, "blackList", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        } else {
            if (nickColor.equals("")) {
                nickColor = getDefaultColor(player);
            }
        }
        formatting(true, player, playerName, nickName, nickColor);
        String[] placeHolders = Language.newString();
        placeHolders[1] = playerName;
        placeHolders[3] = nickName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOn", sender, placeHolders);
        ServerHandler.sendFeatureMessage("Nick-On", playerName, "final", "return",
                new Throwable().getStackTrace()[0]);
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
        if (!bypass && !PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.*")) {
            if (nickColor.equals("")) {
                nickColor = getDefaultColor(player);
            } else {
                if (!getColorPerm(player, nickColor)) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidColor", sender, placeHolders);
                    ServerHandler.sendFeatureMessage("Nick-On", playerName, "colorPerm", "return", "other",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
            }
            if (!getLength(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidLength", sender, placeHolders);
                ServerHandler.sendFeatureMessage("Nick-On", playerName, "length", "return", "other",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            if (!getPureColor(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidColorInside", sender, placeHolders);
                ServerHandler.sendFeatureMessage("Nick-On", playerName, "pureColor", "return", "other",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            if (!getBlackList(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidNick", sender, placeHolders);
                ServerHandler.sendFeatureMessage("Nick-On", playerName, "blackList", "return", "other",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        } else {
            if (nickColor.equals("")) {
                nickColor = getDefaultColor(player);
            }
        }
        formatting(true, player, playerName, nickName, nickColor);
        placeHolders[1] = playerName;
        placeHolders[3] = nickName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOn", player, placeHolders);
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetSuccessOn", sender, placeHolders);
        ServerHandler.sendFeatureMessage("Nick-On", playerName, "final", "return", "other",
                new Throwable().getStackTrace()[0]);
    }

    // Command: /nickplus nick off [player]
    public static void setNickOff(CommandSender sender) {
        Player player = (Player) sender;
        String playerName = player.getName();
        String nickColor = getDefaultColor(player);
        formatting(false, player, playerName, "", nickColor);
        String[] placeHolders = Language.newString();
        placeHolders[1] = playerName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOff", player, placeHolders);
        ServerHandler.sendFeatureMessage("Nick-Off", playerName, "final", "return",
                new Throwable().getStackTrace()[0]);
    }

    // Command: /nickplus nick off [player]
    public static void setNickOff(CommandSender sender, Player player) {
        String playerName = player.getName();
        String nickColor = getDefaultColor(player);
        formatting(false, player, playerName, "", nickColor);
        String[] placeHolders = Language.newString();
        placeHolders[1] = playerName;
        placeHolders[2] = playerName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successOff", player, placeHolders);
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetSuccessOff", sender, placeHolders);
        ServerHandler.sendFeatureMessage("Nick-Off", playerName, "final", "return", "other",
                new Throwable().getStackTrace()[0]);
    }

    public static void setColor(CommandSender sender, boolean bypass, String nickColor) {
        if (sender instanceof ConsoleCommandSender) {
            Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
            Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOffOther", sender);
            return;
        }
        Player player = (Player) sender;
        String playerName = player.getName();
        if (!bypass) {
            if (!getColorPerm(player, nickColor)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidColor", sender);
                ServerHandler.sendFeatureMessage("Nick-Color", playerName, "colorPerm", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        colorChanging(player, playerName, nickColor);
        String[] placeHolders = Language.newString();
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successColor", player, placeHolders);
        colorChanging(player, playerName, nickColor);
        ServerHandler.sendFeatureMessage("Nick-Color", playerName, "final", "return",
                new Throwable().getStackTrace()[0]);
    }

    public static void setColor(CommandSender sender, Player player, boolean bypass, String nickColor) {
        String playerName = player.getName();
        String[] placeHolders = Language.newString();
        placeHolders[2] = playerName;
        if (!bypass) {
            if (!getColorPerm(player, nickColor)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidColor", sender, placeHolders);
                ServerHandler.sendFeatureMessage("Nick-Color", playerName, "colorPerm", "return", "other",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        colorChanging(player, playerName, nickColor);
        placeHolders[1] = playerName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successColor", player, placeHolders);
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetSuccessColor", sender, placeHolders);
        ServerHandler.sendFeatureMessage("Nick-Color", playerName, "final", "return",
                new Throwable().getStackTrace()[0]);
    }

    private static void colorChanging(Player player, String playerName, String nickColor) {
        CMIColorChanging(player, playerName, nickColor);
        EssColorChanging(player, playerName, nickColor);
        NTEColorChanging(player, playerName, nickColor);
    }

    private static void CMIColorChanging(Player player, String playerName, String nickColor) {
        if (ConfigHandler.getDepends().CMIEnabled() && ConfigHandler.getConfigPath().isNickCMI()) {
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
            String nickName = user.getNickName();
            if (nickName == null) {
                nickName = "";
            } else {
                nickName = nickName.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
            }
            user.setNickName(nickName, true);
            if (ConfigHandler.getConfigPath().isNickCMIUpdate()) {
                CMI.getInstance().getTabListManager().updateTabList(20);
            }
            ServerHandler.sendFeatureMessage("Nick-On", playerName, "CMI", "setColor", nickName,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void EssColorChanging(Player player, String playerName, String nickColor) {
        if (ConfigHandler.getDepends().EssentialsEnabled() && ConfigHandler.getConfigPath().isNickEssentials()) {
            Essentials ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
            if (ess == null) {
                return;
            }
            User user = ess.getUser(player);
            String nickName = user.getDisplayName();
            if (nickName == null) {
                nickName = "";
            } else {
                nickName = nickName.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
            }
            user.setNickname(nickName);
            ServerHandler.sendFeatureMessage("Nick-On", playerName, "Essentials", "setColor", nickName,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void NTEColorChanging(Player player, String playerName, String nickColor) {
        if (ConfigHandler.getDepends().NameTagEditEnabled() && ConfigHandler.getConfigPath().isNickNameTagEdit()) {
            String formatPrefix = NametagEdit.getApi().getNametag(player).getPrefix();
            String formatSuffix = NametagEdit.getApi().getNametag(player).getSuffix();
            if (formatPrefix != null) {
                formatPrefix = formatPrefix.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
                if (!formatPrefix.equals("")) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utils.translateLayout("nte player " + playerName + " prefix " + formatPrefix, player));
                }
            }
            if (formatSuffix != null) {
                formatSuffix = formatSuffix.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
                if (!formatSuffix.equals("")) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utils.translateLayout("nte player " + playerName + " suffix " + formatSuffix, player));
                }
            }
            //NametagEdit.getApi().setNametag(player.getName(), nteFormatPrefix, nteFormatSuffix);
            ServerHandler.sendFeatureMessage("Nick-On", playerName, "NameEditTag", "setColor", formatPrefix + playerName + formatSuffix,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void formatting(boolean toggle, Player player, String playerName, String nickName, String nickColor) {
        CMIFormatting(toggle, player, playerName, nickName, nickColor);
        EssFormatting(toggle, player, playerName, nickName, nickColor);
        NTEFormatting(toggle, player, playerName, nickName, nickColor);
        CMDFormatting(toggle, player, playerName, nickName, nickColor);
    }

    private static void CMIFormatting(boolean toggle, Player player, String playerName, String nickName, String nickColor) {
        if (ConfigHandler.getDepends().CMIEnabled() && ConfigHandler.getConfigPath().isNickCMI()) {
            String format;
            if (toggle) {
                format = ConfigHandler.getConfigPath().getNickCMIOn();
            } else {
                format = ConfigHandler.getConfigPath().getNickCMIOff();
            }
            if (format != null) {
                format = getCmdReplace(format, playerName, nickName, nickColor, player);
            } else {
                if (toggle) {
                    format = "§" + nickColor + nickName + "(" + playerName + ")";
                } else {
                    format = "§" + nickColor + playerName;
                }
            }
            CMI.getInstance().getPlayerManager().getUser(player).setNickName(format, true);
            if (ConfigHandler.getConfigPath().isNickCMIUpdate()) {
                CMI.getInstance().getTabListManager().updateTabList(20);
                //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cmi tablistupdate");
            }
            ServerHandler.sendFeatureMessage("Nick-On", playerName, "CMI", "set", format,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void EssFormatting(boolean toggle, Player player, String playerName, String nickName, String nickColor) {
        if (ConfigHandler.getDepends().EssentialsEnabled() && ConfigHandler.getConfigPath().isNickNameTagEdit()) {
            Essentials ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
            if (ess == null) {
                return;
            }
            String format;
            if (toggle) {
                format = ConfigHandler.getConfigPath().getNickEssOn();
            } else {
                format = ConfigHandler.getConfigPath().getNickEssOff();
            }
            if (format != null) {
                format = getCmdReplace(format, playerName, nickName, nickColor, player);
            } else {
                if (toggle) {
                    format = "§" + nickColor + nickName + "(" + playerName + ")";
                } else {
                    format = "§" + nickColor + playerName;
                }
            }
            ess.getUser(player).setNickname(format);
            ServerHandler.sendFeatureMessage("Nick-On", playerName, "Essentials", "set", format,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void NTEFormatting(boolean toggle, Player player, String playerName, String nickName, String nickColor) {
        if (ConfigHandler.getDepends().NameTagEditEnabled() && ConfigHandler.getConfigPath().isNickNameTagEdit()) {
            String formatPrefix;
            String formatSuffix;
            if (toggle) {
                formatPrefix = ConfigHandler.getConfigPath().getNickNTEOnPrefix();
                formatSuffix = ConfigHandler.getConfigPath().getNickNTEOnSuffix();
            } else {
                formatPrefix = ConfigHandler.getConfigPath().getNickNTEOffPrefix();
                formatSuffix = ConfigHandler.getConfigPath().getNickNTEOffSuffix();
            }
            if (formatPrefix != null) {
                formatPrefix = getCmdReplace(formatPrefix, playerName, nickName, nickColor, player);
            } else {
                if (toggle) {
                    formatPrefix = "§" + nickColor + nickName + " &f";
                } else {
                    formatPrefix = "§" + nickColor;
                }
            }
            if (formatSuffix != null) {
                formatSuffix = getCmdReplace(formatSuffix, playerName, nickName, nickColor, player);
            } else {
                formatSuffix = "";
            }
            if (!formatPrefix.equals("")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utils.translateLayout("nte player " + playerName + " prefix " + formatPrefix, player));
            }
            if (!formatSuffix.equals("")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utils.translateLayout("nte player " + playerName + " suffix " + formatSuffix, player));
            }
            //NametagEdit.getApi().setNametag(player, nteFormatPrefix, nteFormatSuffix);
            ServerHandler.sendFeatureMessage("Nick-On", playerName, "NameEditTag", "set", formatPrefix + playerName + formatSuffix,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void CMDFormatting(boolean toggle, Player player, String playerName, String nickName, String nickColor) {
        List<String> cmdList;
        if (toggle) {
            cmdList = ConfigHandler.getConfigPath().getNickCommandOn();
        } else {
            cmdList = ConfigHandler.getConfigPath().getNickCommandOff();
        }
        for (String command : cmdList) {
            command = getCmdReplace(command, playerName, nickName, nickColor, player);
            ServerHandler.executeCommands(player, command);
            ServerHandler.sendFeatureMessage("Nick-On", playerName, "commands", "set", command,
                    new Throwable().getStackTrace()[0]);
        }
    }

    public static String getDefaultColor(CommandSender sender) {
        String defaultColor = ConfigHandler.getConfigPath().getNickGroupsDefault();
        Map<Integer, String> groupsMap = ConfigHandler.getConfigPath().getNickGroupsMap();
        List<Integer> groupList = new ArrayList<>();
        for (int group : groupsMap.keySet()) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.nick.group." + group)) {
                groupList.add(group);
            }
        }
        if (groupList.isEmpty()) {
            return defaultColor;
        }
        Collections.sort(groupList);
        String customColor;
        for (int i = 0; i <= groupList.size(); i++) {
            customColor = groupsMap.get(groupList.get(0));
            if (customColor != null) {
                return customColor;
            }
        }
        return defaultColor;
    }

    private static boolean getLength(Player player, String nickName) {
        if (!PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.length")) {
            return nickName.length() <= ConfigHandler.getConfigPath().getNickLimit();
        }
        return true;
    }

    private static boolean getPureColor(Player player, String nickName) {
        if (!PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.purecolor")) {
            if (ConfigHandler.getConfigPath().isNickPure()) {
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
        if (!PermissionsHandler.hasPermission(player, "playerdataplus.bypass.nick.blacklist")) {
            for (String key : ConfigHandler.getConfigPath().getNickBlackList()) {
                if (nickName.contains(key)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean getColorPerm(Player player, String nickColor) {
        return PermissionsHandler.hasPermission(player, "playerdataplus.nick.color." + nickColor)
                || getDefaultColor(player).equals(nickColor);
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
