package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.PlayerStatus.Fly.FlyControl;
import tw.momocraft.playerdataplus.PlayerStatus.God.GodControl;
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
                if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.version")) {
                    Language.sendLangMessage("Message.PlayerdataPlus.Commands.version", sender, false);
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
                if (ConfigHandler.getConfigPath().isTimeoutWarning() && ConfigHandler.getConfigPath().getTimeoutTime() < 180) {
                    ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                    ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                    ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                    return true;
                }
                PurgeHandler purgeHandler = new PurgeHandler();
                if (purgeHandler.getRun()) {
                    ServerHandler.sendConsoleMessage("&cThe Cleanup process is still running! &8(Stop process: /pp clean stop)");
                    return true;
                }
                purgeHandler.start(sender);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("clean")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
                if (ConfigHandler.getConfigPath().isTimeoutWarning() && ConfigHandler.getConfigPath().getTimeoutTime() < 180) {
                    ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                    ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                    ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                    return true;
                }
                PurgeHandler purgeHandler = new PurgeHandler();
                if (purgeHandler.getRun()) {
                    ServerHandler.sendConsoleMessage("&cThe Cleanup process is still running! &8(Stop process: /pp clean stop)");
                    return true;
                }
                purgeHandler.start(sender, args[1]);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("clean") && args[1].equalsIgnoreCase("stop")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {

                if (ConfigHandler.getConfigPath().isTimeoutWarning() && ConfigHandler.getConfigPath().getTimeoutTime() < 180) {
                    ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                    ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                    ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                    return true;
                }
                PurgeHandler purgeHandler = new PurgeHandler();
                if (!purgeHandler.getRun()) {
                    ServerHandler.sendConsoleMessage("&cThe Cleanup process isn't running now.");
                    return true;
                }
                purgeHandler.setRun(false);
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
                            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                                // <nick> [player]
                                if (nickColor.equals("")) {
                                    nickColor = Nick.getDefaultColor(player);
                                }
                                Nick.setNick(sender, player, bypass, nickName, nickColor);
                                return true;
                            }
                        } else {
                            // <nick>
                            if (nickColor.equals("")) {
                                nickColor = Nick.getDefaultColor(sender);
                            }
                            Nick.setNick(sender, bypass, nickName, nickColor);
                            return true;
                        }
                    } else {
                        // <color> [player]
                        if (player != null) {
                            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                                if (nickColor.equals("")) {
                                    nickColor = Nick.getDefaultColor(player);
                                }
                                Nick.setColor(sender, player, bypass, nickColor);
                                return true;
                            }
                        } else {
                            // <color>
                            if (nickColor.equals("")) {
                                Language.sendLangMessage("Message.PlayerdataPlus.Commands.nick", sender);
                                return true;
                            }
                            Nick.setColor(sender, bypass, nickColor);
                            return true;
                        }
                    }
                } else {
                    if (player != null) {
                        // <off> [player]
                        if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick.other")) {
                            Nick.setNickOff(sender, player);
                            return true;
                        }
                    } else {
                        // <off>
                        Nick.setNickOff(sender);
                        return true;
                    }
                }
            }
            Language.sendLangMessage("Message.noPermission", sender);
            return true;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("playerstatus")) {
            if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.playerstatus")) {
                if (args[3].equalsIgnoreCase("start")) {
                    if (args[2].equalsIgnoreCase("fly")) {
                        FlyControl flyControl = new FlyControl();
                        if (flyControl.isRunSchedule()) {
                            ServerHandler.sendConsoleMessage("&cThe process of Fly-Status is still running!");
                        } else {
                            flyControl.startSchedule();
                        }
                        return true;
                    } else
                    if (args[2].equalsIgnoreCase("god")) {
                        GodControl godControl = new GodControl();
                        if (godControl.isRunSchedule()) {
                            ServerHandler.sendConsoleMessage("&cThe process of God-Status is still running!");
                        } else {
                            godControl.startSchedule();
                        }
                        return true;
                    }
                } else if (args[3].equalsIgnoreCase("stop")) {
                    if (args[2].equalsIgnoreCase("fly")) {
                        FlyControl flyControl = new FlyControl();
                        if (flyControl.isRunSchedule()) {
                            flyControl.setRunSchedule(false);
                            ServerHandler.sendConsoleMessage("&6Stops the Fly-Status process after finished this checking.");
                        } else {
                            ServerHandler.sendConsoleMessage("&cThe process of Fly-Status isn't running now.");
                        }
                        return true;
                    } else if (args[2].equalsIgnoreCase("god")) {
                        GodControl godControl = new GodControl();
                        if (godControl.isRunSchedule()) {
                            godControl.setRunSchedule(false);
                            ServerHandler.sendConsoleMessage("&6Stops the God-Status process after finished this checking.");
                        } else {
                            ServerHandler.sendConsoleMessage("&cThe process of God-Status isn't running now.");
                        }
                        return true;
                    }
                }
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