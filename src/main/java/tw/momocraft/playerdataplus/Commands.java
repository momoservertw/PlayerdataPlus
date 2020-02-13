package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.PurgeHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;
import tw.momocraft.playerdataplus.utils.Language;


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
                purgeHandler.startClean();
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else {
            Language.sendLangMessage("Message.unknownCommand", sender);
            return true;
        }
    }
}