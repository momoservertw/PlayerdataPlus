package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.utils.nick.Nick;

public class Commands implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        int length = args.length;
        if (length == 0) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.use")) {
                CorePlusAPI.getMsg().sendMsg("", sender, "");
                CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgTitle(), sender);
                CorePlusAPI.getMsg().sendMsg("", sender,
                        "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgHelp(), sender);
                CorePlusAPI.getMsg().sendMsg("", sender, "");
            } else {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
            }
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.use")) {
                    CorePlusAPI.getMsg().sendMsg("", sender, "");
                    CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgTitle(), sender);
                    CorePlusAPI.getMsg().sendMsg("", sender,
                            "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                    + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgHelp(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.reload"))
                        CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgReload(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.version"))
                        CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgVersion(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.other"))
                        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgCmdNickOther(), sender);
                    else if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick"))
                        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgCmdNick(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.clean"))
                        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgCmdClean(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.playerstatus"))
                        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgCmdPlayerstatus(), sender);
                    CorePlusAPI.getMsg().sendMsg("", sender, "");
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "reload":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.reload")) {
                    ConfigHandler.generateData(true);
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.configReload", sender);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "version":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.version")) {
                    CorePlusAPI.getMsg().sendMsg(ConfigHandler.getPrefix(), sender,
                            "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                    + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getUpdate().check(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(), sender,
                            PlayerdataPlus.getInstance().getName(), PlayerdataPlus.getInstance().getDescription().getVersion(), true);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
                /*
            case "clean":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.clean")) {
                    if (length == 2) {

                    }
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;

                 */
            case "nick":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick")) {
                    if (length == 4) {
                        // /pdp nick <nick> <color> <player>
                        if (CorePlusAPI.getUtils().equalColorCodeWithoutSymbol(args[2])) {
                            Nick.changeNick(sender, args[3], args[1], args[2], true);
                            return true;
                        }
                    } else if (length == 3) {
                        // /pdp nick <nick> <color>
                        if (CorePlusAPI.getUtils().equalColorCodeWithoutSymbol(args[2])) {
                            Nick.changeNick(sender, null, args[1], args[2], true);
                            // /pdp nick <color> <player>
                        } else if (CorePlusAPI.getUtils().equalColorCodeWithoutSymbol(args[1])) {
                            Nick.changeNick(sender, args[2], null, args[1], true);
                            // /pdp nick <off> <player>
                        } else if (args[1].equals("off")) {
                            Nick.clearNick(sender, args[2], true);
                        } else if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.other")) {
                            // /pdp nick <color> <player>
                            if (CorePlusAPI.getUtils().equalColorCodeWithoutSymbol(args[2])) {
                                Nick.changeNick(sender, args[2], null, args[1], true);
                            } else {
                                // /pdp nick <nick> <player>
                                Nick.changeNick(sender, args[2], args[1], null, true);
                            }
                        } else {
                            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                    "Message.noPermission", sender);
                        }
                        return true;
                    } else if (length == 2) {
                        // /pdp nick <color>
                        if (CorePlusAPI.getUtils().equalColorCodeWithoutSymbol(args[1])) {
                            Nick.changeNick(sender, null, null, args[1], true);
                            // /pdp nick off
                        } else if (args[1].equals("off")) {
                            Nick.clearNick(sender, null, true);
                        } else {
                            // /pdp nick <nick>
                            Nick.changeNick(sender, null, args[1], null, true);
                        }
                        return true;
                    }
                    // Send command prompt
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.other")) {
                        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgCmdNickOther(), sender);
                    } else {
                        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgCmdNick(), sender);
                    }
                    return true;
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                    return true;
                }
                /*
            case "playerstatus":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.playerstatus")) {
                    // pdp playerstatus <on/off>
                    if (length == 2) {
                        PlayerStatusControl.startSchedule(sender, args[1]);
                        return true;
                    } else if (length == 1) {
                        PlayerStatusControl.startSchedule(sender, null);
                        return true;
                    }
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgCmdPlayerstatus(), sender);
                    return true;
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "dataconvert":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.dataconvert")) {

                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;

                 */
        }
        CorePlusAPI.getMsg().

                sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.unknownCommand", sender);
        return true;
    }
}