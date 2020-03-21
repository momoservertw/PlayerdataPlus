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
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender);
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOff", sender);
                }
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOffOther", sender);
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
        } else if (args.length == 1 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOffOther", sender);
                return true;
            } else if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender);
                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOff", sender);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                // /playerdataplus nick [color]
                String nickColor = ConfigHandler.getColors().getColorCode(args[1]);
                if (!nickColor.equals("")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.color")) {
                        Nick.setColor(sender, false, nickColor);
                        return true;
                    }
                    // /playerdataplus nick off
                } else if (args[1].equalsIgnoreCase("off")) {
                    Nick.setNickOff(sender);
                    return true;
                } else {
                    // /playerdataplus nick <nick>
                    nickColor = Nick.getDefaultColor(sender);
                    Nick.setNick(sender, false, args[1], nickColor);
                    return true;
                }
            }
            Language.sendLangMessage("Message.noPermission", sender);
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                String nickColor = ConfigHandler.getColors().getColorCode(args[2]);
                // /playerdataplus nick <off> [player]
                if (args[1].equals("off")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.bypass")) {
                        Player player = PlayerHandler.getPlayerString(args[2]);
                        if (player != null) {
                            Nick.setNickOff(sender, player);
                            return true;
                        }
                        String[] placeHolders = Language.newString();
                        placeHolders[2] = args[2];
                        Language.sendLangMessage("Message.targetNotOnline", sender, placeHolders);
                        return true;
                    }
                    // /playerdataplus nick <nick> [color]
                } else if (!nickColor.equals("")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.color")) {
                        Nick.setNick(sender, false, args[1], nickColor);
                        return true;
                    }
                    // /playerdataplus nick <nick> [bypass]
                } else if (args[2].equals("true")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.bypass")) {
                        nickColor = Nick.getDefaultColor(sender);
                        Nick.setNick(sender, true, args[1], nickColor);
                        return true;
                    }
                }
                nickColor = ConfigHandler.getColors().getColorCode(args[1]);
                // /playerdataplus nick <color> [player]
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                    Player player = PlayerHandler.getPlayerString(args[2]);
                    if (player != null) {
                        nickColor = ConfigHandler.getColors().getColorCode(args[1]);
                        if (!nickColor.equals("")) {
                            Nick.setColor(sender, player, false, nickColor);
                            return true;
                        }
                        nickColor = Nick.getDefaultColor(player);
                        Nick.setNick(sender, player, false, args[1], nickColor);
                        return true;
                    }
                    String[] placeHolders = Language.newString();
                    placeHolders[2] = args[2];
                    Language.sendLangMessage("Message.targetNotOnline", sender, placeHolders);
                    return true;
                } else {
                    // Unknown command.
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                        Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
                        return true;
                    } else {
                        Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender);
                        return true;
                    }
                }

            }
            Language.sendLangMessage("Message.noPermission", sender);
            return true;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                // /playerdataplus nick <nick> [color] [?]
                String nickColor = ConfigHandler.getColors().getColorCode(args[2]);
                if (!nickColor.equals("")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.color")) {
                        // /playerdataplus nick <nick> [color] [bypass]
                        if (args[3].equals("true")) {
                            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.bypass")) {
                                Nick.setNick(sender, true, args[1], nickColor);
                                return true;
                            }
                            // /playerdataplus nick <nick> [color] [player]
                        } else if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                            Player player = PlayerHandler.getPlayerString(args[3]);
                            if (player != null) {
                                nickColor = ConfigHandler.getColors().getColorCode(args[2]);
                                Nick.setNick(sender, player, false, args[1], nickColor);
                                return true;
                            }
                            String[] placeHolders = Language.newString();
                            placeHolders[2] = args[2];
                            Language.sendLangMessage("Message.targetNotOnline", sender, placeHolders);
                            return true;
                        }
                    }
                    // /playerdataplus nick <nick> [bypass] [player]
                } else if (args[2].equals("true")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.bypass")) {
                        Player player = PlayerHandler.getPlayerString(args[2]);
                        if (player != null) {
                            nickColor = Nick.getDefaultColor(player);
                            Nick.setNick(sender, player, true, args[1], nickColor);
                            return true;
                        }
                        String[] placeHolders = Language.newString();
                        placeHolders[2] = args[2];
                        Language.sendLangMessage("Message.targetNotFound", sender, placeHolders);
                        return true;
                    }
                } else {
                    // Unknown command.
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                        Language.sendLangMessage("Message.PlayerdataPlus.Commands.nickOther", sender);
                        return true;
                    } else {
                        Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender);
                        return true;
                    }
                }
            }
            Language.sendLangMessage("Message.noPermission", sender);
            return true;
            // /playerdataplus nick <nick> [bypass] [color] [player]
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("nick")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                boolean bypass = false;
                boolean off = false;
                Player player = null;
                String nickColor = "";
                String nickName = "";
                for (String arg : args) {
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
                    nickName = arg;
                }
                if (player != null) {
                    Nick.setNick(sender, player, bypass, nickName, nickColor);
                } else {
                    Nick.setNick(sender, bypass, nickName, nickColor);
                }
            }
            Language.sendLangMessage("Message.noPermission", sender);
            return true;
        } else {
            Language.sendLangMessage("Message.unknownCommand", sender);
            return true;
        }
    }
}