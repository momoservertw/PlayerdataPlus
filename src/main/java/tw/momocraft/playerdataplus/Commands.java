package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.PurgeHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;
import tw.momocraft.playerdataplus.utils.Language;
import tw.momocraft.playerdataplus.utils.Nick;


public class Commands implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        if (args.length == 0) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.use")) {
                Language.dispatchMessage(sender, "");
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.title", sender, false);
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.version")) {
                    Language.dispatchMessage(sender, "&d&lPlayerdataPlus &e&lv" + PlayerdataPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                }
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.help", sender, false);
                Language.dispatchMessage(sender, "");
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.use")) {
                Language.dispatchMessage(sender, "");
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.title", sender, false);
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.version")) {
                    Language.dispatchMessage(sender, "&d&lPlayerdataPlus &e&lv" + PlayerdataPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                }
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.help", sender, false);
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.reload")) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.reload", sender, false);
                }
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.clean", sender, false);
                }
                Language.dispatchMessage(sender, "");
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.reload")) {
                // working: close purge.Auto-Clean schedule
                ConfigHandler.generateData(true);
                Language.sendLangMessage("Message.configReload", sender);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.version")) {
                Language.dispatchMessage(sender, "&d&lPlayerdataPlus &e&lv" + PlayerdataPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                ConfigHandler.getUpdater().checkUpdates(sender);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("clean")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
                ServerHandler.sendConsoleMessage("&6Starting to clean the expired data...");
                PurgeHandler purgeHandler = new PurgeHandler();
                purgeHandler.startClean(sender);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
            //nickplus nick 暱稱 顏色
        } else if (args.length == 1 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "nickplus.command.nick")) {
                Language.sendLangMessage("Message.NickPlus.usage", sender);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "nickplus.command.nick")) {
                Language.dispatchMessage(sender, "&a/nickplus nick 暱稱");
                Player player = (Player) sender;
                Nick.onNick(player, args);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
            /*

        } else if (args.length == 3 && args[0].equalsIgnoreCase("nick") && args[1].equalsIgnoreCase("off")) {
            if (PermissionsHandler.hasPermission(sender, "nickplus.command.nick")) {
                Language.dispatchMessage(sender, "&a/nickplus off");
                Nick.onNickOff(sender, args);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("nick") && Utils.isColorCode(args[3]) ) {
            if (PermissionsHandler.hasPermission(sender, "nickplus.command.nick")) {
                Language.dispatchMessage(sender, "&a/nickplus nick 暱稱 顏色");
                Nick.onNick(sender, args);
                OfflinePlayer player = PlayerHandler.getOfflinePlayer(args[2]);
                if (player == null) {
                    Language.sendLangMessage("Message.playerNotFound", sender);
                } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "nickplus.command.nick.other")) {
                Language.dispatchMessage(sender, "&a/nickplus nick 暱稱 顏色 玩家");
                Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
                if (argsPlayer == null) {
                    Language.sendLangMessage("Message.playerNotFound", sender);  return true; }
                Nick.onNickOther(args);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;*/
        } else {
            Language.sendLangMessage("Message.unknownCommand", sender);
            return true;
        }
    }
}