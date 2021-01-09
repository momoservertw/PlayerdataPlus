package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.PlayerStatus.PlayerStatusControl;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.PermissionsHandler;
import tw.momocraft.playerdataplus.handlers.PlayerHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;
import tw.momocraft.playerdataplus.utils.Clean;
import tw.momocraft.playerdataplus.utils.Language;
import tw.momocraft.playerdataplus.utils.Nick;

import java.sql.SQLException;

public class Commands implements CommandExecutor {

     /*
       I would suggest making some sort of Command System using abstraction or Annotations, Since having every single command
       On a single command executor can get very messy, For a good example feel free to take a look at this one
       https://github.com/NikV2/CombatPlus/tree/master/src/main/java/me/nik/combatplus/commands

       I could also implement a similar system to your plugin for you, Just say the word^^
     */

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        switch (args.length) {
            case 0:
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
            case 1:
                if (args[0].equalsIgnoreCase("help")) {
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
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.reload")) {
                        // working: close purge.Auto-Clean schedule
                        ConfigHandler.generateData(true);
                        Language.sendLangMessage("Message.configReload", sender);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                } else if (args[0].equalsIgnoreCase("version")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.version")) {
                        Language.dispatchMessage(sender, "&d&lPlayerdataPlus &e&lv" + PlayerdataPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                        ConfigHandler.getUpdater().checkUpdates(sender);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                } else if (args[0].equalsIgnoreCase("mycmdconvert")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.mycmdconvert")) {
                        if (ConfigHandler.getConfigPath().isMycmd()) {
                            ServerHandler.sendConsoleMessage("&6Start converting MyCommand playerdata...");
                            ConfigurationSection playerdatasConfig = ConfigHandler.getConfig("playerdata.yml").getConfigurationSection("");
                            ConfigurationSection playerConfig;
                            String value;
                            if (playerdatasConfig != null) {
                                for (String uuid : playerdatasConfig.getKeys(false)) {
                                    playerConfig = ConfigHandler.getConfig("playerdata.yml").getConfigurationSection(uuid);
                                    if (playerConfig != null) {
                                        for (String key : playerConfig.getKeys(false)) {
                                            value = ConfigHandler.getConfig("playerdata.yml").getString(uuid + "." + key);
                                            try {
                                                ConfigHandler.getMySQLAPI().addValue(uuid, key, value);
                                                ServerHandler.sendFeatureMessage("MyCommand MySQL-Convertor", uuid, "mysql", "continue", key + ": " + value,
                                                        new Throwable().getStackTrace()[0]);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        ServerHandler.sendConsoleMessage("&cCan not find \"playerdata.yml\" - " + uuid);
                                    }
                                }
                                ServerHandler.sendConsoleMessage("&6MyCommand convert process has ended.");
                            } else {
                                ServerHandler.sendConsoleMessage("&cCan not find \"playerdata.yml\".");
                            }
                        } else {
                            ServerHandler.sendConsoleMessage("&cFeatures for MyCommand is disabled. You need the enable it and restart the server");
                        }
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                } else if (args[0].equalsIgnoreCase("clean")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
                        if (ConfigHandler.getConfigPath().isTimeoutWarning() && ConfigHandler.getConfigPath().getTimeoutTime() < 180) {
                            ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                            ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                            ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                            return true;
                        }
                        Clean purgeHandler = new Clean();
                        if (purgeHandler.getRun()) {
                            ServerHandler.sendConsoleMessage("&cThe Cleanup process is still running! &8(Stop process: /pp clean stop)");
                            return true;
                        }
                        purgeHandler.start(sender);
                        return true;
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                } else if (args[0].equalsIgnoreCase("nick")) {
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
                }
                return true;
            case 2:
                if (args[0].equalsIgnoreCase("playerstatus")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.playerstatus")) {
                        if (args[1].equalsIgnoreCase("start")) {
                            if (PlayerStatusControl.isSchedule()) {
                                ServerHandler.sendConsoleMessage("&cThe process of Fly-Status is still running!");
                            } else {
                                PlayerStatusControl.startSchedule();
                            }
                        } else if (args[1].equalsIgnoreCase("stop")) {
                            if (PlayerStatusControl.isSchedule()) {
                                PlayerStatusControl.setSchedule(false);
                                ServerHandler.sendConsoleMessage("&6The Fly-Status process after finished this checking.");
                            } else {
                                ServerHandler.sendConsoleMessage("&cThe process of Fly-Status isn't running now.");
                            }
                        } else {
                            Language.sendLangMessage("Message.unknownCommand", sender);
                        }
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                } else if (args[0].equalsIgnoreCase("clean") && args[1].equalsIgnoreCase("stop")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.clean")) {
                        if (ConfigHandler.getConfigPath().isTimeoutWarning() && ConfigHandler.getConfigPath().getTimeoutTime() < 180) {
                            ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                            ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                            ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                            return true;
                        }
                        Clean purgeHandler = new Clean();
                        if (purgeHandler.getRun()) {
                            ServerHandler.sendConsoleMessage("&cThe Cleanup process is still running! &8(Stop process: /pp clean stop)");
                            return true;
                        }
                        purgeHandler.start(sender, args[1]);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                }
                return true;
            default:
                if (args[0].equalsIgnoreCase("nick")) {
                    if (PermissionsHandler.hasPermission(sender, "playerdataplus.command.nick")) {
                        if (ConfigHandler.getConfigPath().isNick()) {
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
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                } else {
                    Language.sendLangMessage("Message.unknownCommand", sender);
                }
        }
        return true;
    }
}