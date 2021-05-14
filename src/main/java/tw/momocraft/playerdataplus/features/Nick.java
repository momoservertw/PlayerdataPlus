package tw.momocraft.playerdataplus.features;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Nick {

    public static void setNick(CommandSender sender, Player target, String nickName, String nickColor) {
        Player player;
        if (target != null)
            player = target;
        else if (sender instanceof ConsoleCommandSender)
            Language.sendLangMessage("Message.onlyPlayer", sender);
        else
            player = (Player) sender;
        String[] placeHolders = Language.newString();
        placeHolders[2] = playerName;
        if (!CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.nick.*")) {
            if (nickColor == null) {
                nickColor = getDefaultColor(player);
            } else {
                if (!getColorPerm(player, nickColor)) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidColor",
                            sender, placeHolders);
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                            "Nick", playerName, "Color Permission", "cancel",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
            }
            if (!getLength(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidLength", sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Nick", playerName, "Length", "cancel",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            if (!getColorCode(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidColorInside", sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Nick", playerName, "Pure Color", "cancel",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            if (!getBlackList(player, nickName)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidNick", sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Nick", playerName, "Black List", "cancel",
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
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick", playerName, "final", "return", "other",
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
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-Off", playerName, "final", "return", "other",
                new Throwable().getStackTrace()[0]);
    }

    public static void setColor(CommandSender sender, Player player, String nickColor) {
        String playerName = player.getName();
        String[] placeHolders = Language.newString();
        placeHolders[2] = playerName;
        if (!bypass) {
            if (!getColorPerm(player, nickColor)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetInvalidColor", sender, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-Color", playerName, "colorPerm", "return", "other",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        colorChanging(player, playerName, nickColor);
        placeHolders[1] = playerName;
        placeHolders[4] = nickColor;
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.successColor", player, placeHolders);
        Language.sendLangMessage("Message.PlayerdataPlus.Nick.targetSuccessColor", sender, placeHolders);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-Color", playerName, "final", "return",
                new Throwable().getStackTrace()[0]);
    }

    private static void colorChanging(Player player, String playerName, String nickColor) {
        CMIColorChanging(player, playerName, nickColor);
        NTEColorChanging(player, playerName, nickColor);
    }

    private static void CMIColorChanging(Player player, String playerName, String nickColor) {
        if (!CorePlusAPI.getDepend().CMIEnabled())
            return;
        if (ConfigHandler.getConfigPath().isNickCMI()) {
            CMIUser user = CMI.getInstance().getPlayer().getUser(player);
            String nickName = user.getNickName();
            if (nickName == null) {
                nickName = "";
            } else {
                nickName = nickName.replaceAll("[§][a-fA-F0-9]", "§" + nickColor);
            }
            user.setNickName(nickName, true);
            if (ConfigHandler.getConfigPath().isNickCMIUpdate()) {
                CMI.getInstance().getTabList().updateTabList(20);
            }
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-On", playerName, "CMI", "setColor", nickName,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void NTEColorChanging(Player player, String playerName, String nickColor) {
        if (!CorePlusAPI.getDepend().NameTagEditEnabled())
            return;
        if (ConfigHandler.getConfigPath().isNickNameTagEdit()) {
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
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-On", playerName, "NameEditTag", "setColor", formatPrefix + playerName + formatSuffix,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void formatting(boolean toggle, Player player, String playerName, String nickName, String nickColor) {
        CMIFormatting(toggle, player, playerName, nickName, nickColor);
        NTEFormatting(toggle, player, playerName, nickName, nickColor);
        CMDFormatting(toggle, player, playerName, nickName, nickColor);
    }

    private static void CMIFormatting(boolean toggle, Player player, String playerName, String nickName, String
            nickColor) {
        if (CorePlusAPI.getDepend().CMIEnabled() && ConfigHandler.getConfigPath().isNickCMI()) {
            String format;
            if (toggle) {
                format = ConfigHandler.getConfigPath().getNickCMIOn();
            } else {
                format = ConfigHandler.getConfigPath().getNickCMIOff();
            }
            if (format != null) {
                format = translate(format, playerName, nickName, nickColor);
            } else {
                if (toggle) {
                    format = "§" + nickColor + nickName + "(" + playerName + ")";
                } else {
                    format = "§" + nickColor + playerName;
                }
            }
            CMI.getInstance().getPlayer().getUser(player).setNickName(format, true);
            if (ConfigHandler.getConfigPath().isNickCMIUpdate()) {
                CMI.getInstance().getTabList().updateTabList(20);
                //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cmi tablistupdate");
            }
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-On", playerName, "CMI", "set", format,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private static void NTEFormatting(boolean toggle, Player player, String playerName, String nickName, String nickColor) {
        if (CorePlusAPI.getDepend().NameTagEditEnabled() && ConfigHandler.getConfigPath().isNickNameTagEdit()) {
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
                formatPrefix = translate(formatPrefix, playerName, nickName, nickColor);
            } else {
                if (toggle) {
                    formatPrefix = "§" + nickColor + nickName + " &f";
                } else {
                    formatPrefix = "§" + nickColor;
                }
            }
            if (formatSuffix != null) {
                formatSuffix = translate(formatSuffix, playerName, nickName, nickColor);
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
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-On", playerName, "NameEditTag", "set", formatPrefix + playerName + formatSuffix,
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
            command = translate(command, playerName, nickName, nickColor);
            ServerHandler.executeCommands(player, command);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Nick-On", playerName, "commands", "set", command,
                    new Throwable().getStackTrace()[0]);
        }
    }

    public static String getDefaultColor(CommandSender sender) {
        String defaultColor = ConfigHandler.getConfigPath().getNickGroupsDefault();
        Map<Integer, String> groupsMap = ConfigHandler.getConfigPath().getNickGroupsMap();
        List<Integer> groupList = new ArrayList<>();
        for (int group : groupsMap.keySet()) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.nick.group." + group)) {
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
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.nick.length"))
            return true;
        return nickName.length() <= ConfigHandler.getConfigPath().getNickLength();
    }

    private static boolean getColorCode(Player player, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.nick.colorcode"))
            return true;
        if (ConfigHandler.getConfigPath().isNickColorCode()) {
            return !nickName.contains("§") && !nickName.contains("&");
        } else {
            String[] split = nickName.split("[&§]");
            for (int i = 0; i <= split.length; i++) {
                if (i == 0)
                    continue;
                if (!getColorPerm(player, String.valueOf(split[i].charAt(0))))
                    return false;
            }
        }
        return true;
    }

    private static boolean getBlackList(Player player, String nickName) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.nick.blacklist"))
            return true;
        for (String key : ConfigHandler.getConfigPath().getNickBlackList())
            if (nickName.contains(key) || nickName.matches(key))
                return false;
        return true;
    }

    private static boolean getColorPerm(Player player, String nickColor) {
        return CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.nick.color." + nickColor)
                || getDefaultColor(player).equals(nickColor);
    }

    private static String translate(String command, String playerName, String nickName, String nickColor) {
        command = command.replace("%player%", playerName);
        command = command.replace("%nick%", nickName);
        command = command.replace("%color%", nickColor);
        return command;
    }
}
