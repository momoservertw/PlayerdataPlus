package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> commands = new ArrayList<>();
        if (args.length == 2 && args[0].equalsIgnoreCase("help") && CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.use")) {
            commands.add("2");
        } else if (args.length == 1) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.use"))
                commands.add("help");
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.reload"))
                commands.add("reload");
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.version"))
                commands.add("version");
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.clean"))
                commands.add("clean");
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick"))
                commands.add("nick");
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.playerstatus"))
                commands.add("playerstatus");
            /*
        } else if ((args.length == 2) && (args[0].equalsIgnoreCase("clean"))) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.clean")) {
                commands.add("stop");
                commands.addAll(ConfigHandler.getConfigPath().);
            }
             */
        } else if ((args.length == 2) && (args[0].equalsIgnoreCase("nick"))) {
            commands.add("off");
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick"))
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.color"))
                    commands.addAll(CorePlusAPI.getMsg().getColorMap().keySet());
        } else if ((args.length >= 3) && (args[0].equalsIgnoreCase("nick"))) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick")) {
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.bypass"))
                    commands.add("false");
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.color"))
                    commands.addAll(CorePlusAPI.getMsg().getColorMap().keySet());
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.other"))
                    commands.addAll(CorePlusAPI.getPlayer().getOnlinePlayerNames());
            }
        }
            /*
        } else if (args.length == 3 && args[0].equalsIgnoreCase("playerstatus")) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.playerstatus")) {
                commands.add("fly");
                commands.add("god");
                commands.add("op");
                commands.add("gamemode");
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("playerstatus")) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.playerstatus")) {
                commands.add("stop");
                commands.add("start");
            }
        }
             */
        StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}