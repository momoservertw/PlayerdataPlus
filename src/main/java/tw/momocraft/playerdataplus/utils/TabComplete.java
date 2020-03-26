package tw.momocraft.playerdataplus.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> commands = new ArrayList<>();
        Collection<?> playersOnlineNew = null;
        Player[] playersOnlineOld;
        if (args.length == 2 && args[0].equalsIgnoreCase("help") && PermissionsHandler.hasPermission(sender, "playerdataplus.use")) {
            commands.add("2");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("clean") && PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
            ConfigurationSection cleanConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Clean.Control");
            if (cleanConfig != null) {
                commands.addAll(cleanConfig.getKeys(false));
            }
        } else if ((args.length >= 3) && (args[0].equalsIgnoreCase("nick"))) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.bypass")) {
                    commands.add("false");
                }
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.color")) {
                    commands.addAll(ConfigHandler.getColors().getColorList());
                }
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                }
            }
        } else if ((args.length == 2) && (args[0].equalsIgnoreCase("nick"))) {
            commands.add("off");
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.color")) {
                    commands.addAll(ConfigHandler.getColors().getColorList());
                }
            }
        } else if (args.length == 1) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.use")) {
                commands.add("help");
            }
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.reload")) {
                commands.add("reload");
            }
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.version")) {
                commands.add("version");
            }
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
                commands.add("clean");
            }
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                commands.add("nick");
            }
        }
        StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}