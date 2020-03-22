package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.handlers.*;
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
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender, false);
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOff", sender, false);
                }
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender, false);
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOffOther", sender, false);
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
                purgeHandler.start(sender);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("clean")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
                ServerHandler.sendConsoleMessage("&6Starting to clean the expired data...");
                PurgeHandler purgeHandler = new PurgeHandler();
                purgeHandler.start(sender, args[1]);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender, false);
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOffOther", sender, false);
                return true;
            } else if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender, false);
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOff", sender, false);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length > 1 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                boolean bypass = false;
                boolean off = false;
                Player player = null;
                String nickColor = "";
                String nickName = "";
                String arg;
                for (int i = 1; i < args.length; i++) {
                    arg = args[i];
                    if (arg.equals("true") && !bypass) {
                        if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.bypass")) {
                            bypass = true;
                            continue;
                        }
                        Language.sendLangMessage("Message.noPermission", sender);
                        return true;
                    }
                    if (arg.equals("off") && !off) {
                        off = true;
                        continue;
                    }
                    if (nickColor.equals("")) {
                        nickColor = ConfigHandler.getColors().getColorCode(arg);
                        if (!nickColor.equals("")) {
                            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.color")) {
                                continue;
                            }
                            Language.sendLangMessage("Message.noPermission", sender);
                            return true;
                        }
                    }
                    if (player == null) {
                        player = PlayerHandler.getPlayerString(arg);
                        if (player != null) {
                            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.bypass")) {
                                continue;
                            }
                            Language.sendLangMessage("Message.noPermission", sender);
                            return true;
                        }
                    }
                    if (nickName.equals("")) {
                        nickName = arg;
                        continue;
                    }
                    Language.sendLangMessage("Message.targetNotOnline", sender);
                    return true;
                }
                if (!off) {
                    if (!nickName.equals("")) {
                        if (player != null) {
                            // <nick> [player]
                            Nick.setNick(sender, player, bypass, nickName, nickColor);
                        } else {
                            // <nick>
                            Nick.setNick(sender, bypass, nickName, nickColor);
                        }
                    } else {
                        // <color> [player]
                        if (player != null) {
                            if (nickColor.equals("")) {
                                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
                                return true;
                            }
                            Nick.setColor(sender, player, bypass, nickColor);
                        } else {
                            // <color>
                            if (nickColor.equals("")) {
                                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender);
                                return true;
                            }
                            Nick.setColor(sender, bypass, nickColor);
                        }
                    }
                } else {
                    if (player != null) {
                        // <off> [player]
                        Nick.setNickOff(sender, player);
                    } else {
                        // <off>
                        Nick.setNickOff(sender);
                    }
                }
                return true;
            }
            Language.sendLangMessage("Message.noPermission", sender);
            return true;
        } else {
            Language.sendLangMessage("Message.unknownCommand", sender);
            return true;
        }
    }
}