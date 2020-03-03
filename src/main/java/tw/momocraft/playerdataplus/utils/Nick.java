package tw.momocraft.playerdataplus.utils;

import com.Zrips.CMI.CMI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nick {
    // Command: /nickplus nick <nickName> [color] [player]
    public static void onNick(Player player, String[] args) {
        String nickName = args[1];
        // Check black list.
        for (String key : ConfigHandler.getConfig("config.yml").getStringList("Nick.Black-List")) {
            if (nickName.contains(key)) {
                Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidCharacter", player);
                return;
            }
        }
        // Check nick length.
        int nickLength = ConfigHandler.getConfig("config.yml").getInt("Nick.Length");
        if (nickName.length() > nickLength) {
            Language.sendLangMessage("Message.NickPlus.invalidLong", player);
            return;
        }
        // No custom color.
        if (args[2] == null) {
            setNick(player, nickName, getDefaultColor(player));
            return;
        }
        // Check permission of color.
        if (PermissionsHandler.hasPermissionOffline(player, "playerdataplus.nick.color." + args[2]) || getDefaultColor(player).equals(args[2])) {
            setNick(player, nickName, args[2]);
        } else {
            Language.sendLangMessage("Message.PlayerdataPlus.Nick.invalidColor", player);
        }
    }

    //nickplus nick off
    public static void onNickOff(Player player) {
        String nickColor = getDefaultColor(player);
        if (ConfigHandler.getDepends().CMIEnabled() && ConfigHandler.getConfig("config.yml").getBoolean("Nick.Format.CMI.Enable")) {
            String cmiOff = ConfigHandler.getConfig("config.yml").getString("Nick.Format.CMI.Off");
            if (cmiOff != null) {
                cmiOff = cmiOff.replace("%player%", player.getName());
                cmiOff = cmiOff.replace("%color%", nickColor);
            } else {
                cmiOff = "§" + nickColor + player.getName();
            }
            CMI.getInstance().getPlayerManager().getUser(player).setNickName(cmiOff, true);
        }
        if (ConfigHandler.getDepends().NameTagEditEnabled() && ConfigHandler.getConfig("config.yml").getBoolean("Nick.Format.NameTagEdit.Enable")) {
            String nteOffPrefix = ConfigHandler.getConfig("config.yml").getString("Nick.Format.NameTagEdit.Off.Prefix");
            String nteOffSuffix = ConfigHandler.getConfig("config.yml").getString("Nick.Format.NameTagEdit.Off.Suffix");
            if (nteOffPrefix != null) {
                nteOffPrefix = nteOffPrefix.replace("%player%", player.getName());
                nteOffPrefix = nteOffPrefix.replace("%color%", nickColor);
            } else {
                nteOff = "§" + nickColor;
            }
            CMI.getInstance().getPlayerManager().getUser(player).setNickName(cmiOff, true);
        }

        for (String commands : ConfigHandler.getConfig("config.yml").getStringList("Nick.Format.Command-Cancel")) {
            commands = commands.replace("%player%", player.getName());
            commands = commands.replace("%color%", nickColor);
            commands = PlaceholderAPI.setPlaceholders(player, commands);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands);
            // Debug
            ServerHandler.sendConsoleMessage(commands);
        }
    }

    /**
     * Set player nick.
     *
     * @param player    the player name.
     * @param nickName  the nick name player set.
     * @param nickColor the color player set or primary group's color.
     */
    private static void setNick(Player player, String nickName, String nickColor) {
        String playerName = player.getName();
        for (String commands : ConfigHandler.getConfig("config.yml").getStringList("Nick.Commands")) {
            commands = PlaceholderAPI.setPlaceholders(player, commands);
            if (ConfigHandler.getConfig("config.yml").getBoolean("Remove-Placeholder-Color")) {
                commands = commands.replaceAll("([&§])[a-fA-F0-8]", "");
            }
            commands = commands.replace("%player%", playerName);
            commands = commands.replace("%nick%", nickName);
            commands = commands.replace("%color%", "§" + nickColor);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands);
            String[] placeHolders = Language.newString();
            placeHolders[1] = playerName;
            placeHolders[4] = nickName;
            placeHolders[5] = "§" + nickColor;
            Language.sendLangMessage("Message.NickPlus.success", player, placeHolders);
            ServerHandler.sendConsoleMessage(commands);
        }
    }

    private static String getDefaultColor(Player player) {
        List<Long> groupList = new ArrayList<>();
        String permission;
        for (PermissionAttachmentInfo pa : player.getEffectivePermissions()) {
            permission = pa.getPermission();
            if (pa.getPermission().startsWith("playerdataplus.nick.color.")) {
                groupList.add(Long.valueOf(permission.replaceFirst("playerdataplus.nick.color.", "")));
            }
        }
        Long maxGroup = Collections.max(groupList);
        String defaultColor;
        while (true) {
            if (maxGroup == null) {
                defaultColor = ConfigHandler.getConfig("config.yml").getString("Nick.Groups.0");
                return defaultColor;
            }
            defaultColor = ConfigHandler.getConfig("config.yml").getString("Nick.Groups." + maxGroup);
            if (defaultColor != null) {
                return defaultColor;
            }
            groupList.remove(Collections.max(groupList));
            maxGroup = Collections.max(groupList);
        }
    }
}
