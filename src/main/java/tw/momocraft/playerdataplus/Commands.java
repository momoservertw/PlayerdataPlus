package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.features.Nick;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

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
                    CorePlusAPI.getUpdate().check(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(), sender,
                            PlayerdataPlus.getInstance().getName(), PlayerdataPlus.getInstance().getDescription().getVersion(), true);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "clean":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.clean")) {
                    if (length == 2) {

                    }
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "nick":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick")) {
                    // /pdp nick <nick> <color> [player]
                    if (length == 4) {
                        Nick.setNick(sender, args[3], args[1], args[2], true);
                    } else if (length == 3) {
                        // /pdp nick <nick> <color>
                        if (CorePlusAPI.getUtils().containsColorCode(args[2])) {
                            Nick.setNick(sender, null, args[1], args[2], true);
                            // /pdp nick <color> [player]
                        } else if (CorePlusAPI.getUtils().containsColorCode(args[1])) {
                            Nick.setNick(sender, args[2], null, args[1], true);
                            // /pdp nick <off> [player]
                        } else if (args[1].equals("off")) {
                            Nick.setNick(sender, args[2], null, null, false);
                            // /pdp nick <nick> [player]
                        } else if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.other")) {
                            // /pdp nick <color> [player]
                            if (CorePlusAPI.getUtils().containsColorCode(args[2])) {
                                Nick.setNick(sender, args[2], null, args[1], true);
                                // /pdp nick <color> [player]
                            } else {
                                Nick.setNick(sender, args[2], args[1], null, true);
                            }
                        } else {
                            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                    "Message.noPermission", sender);
                        }
                    } else if (length == 2) {
                        // /pdp nick <color>
                        if (CorePlusAPI.getUtils().containsColorCode(args[1])) {
                            Nick.setNick(sender, null, null, args[1], true);
                            // /pdp nick <color> [player]
                        } else if (args[1].equals("off")) {
                            Nick.setNick(sender, null, null, null, false);
                        } else {
                            Nick.setNick(sender, null, args[1], null, true);
                        }
                    }
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nick", sender);
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nickOff", sender);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "playerstatus":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.playerstatus")) {
                    if (length == 3) {
                        if (args[1].equals("start")) {

                        } else if (args[1].equals("stop")) {

                        }
                    }
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nickOff", sender);
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
        }
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                "Message.unknownCommand", sender);
        return true;
    }
}