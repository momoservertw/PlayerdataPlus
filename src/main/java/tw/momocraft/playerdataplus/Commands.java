package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.features.nick.Nick;
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
                    if (length == 1) {
                        if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.other")) {
                            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                    ConfigHandler.getConfigPath().getMsgCmdNickOther(), sender);
                        } else {
                            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                    ConfigHandler.getConfigPath().getMsgCmdNick(), sender);
                        }
                        return true;
                    }
                    String targetName = null;
                    String nickName = null;
                    String nickColor = null;
                    for (int i = 1; i < length; i++) {
                        if (nickColor == null) {
                            if (CorePlusAPI.getUtils().equalColorWithoutSymbol(args[i])) {
                                nickColor = args[i];
                                System.out.println("color: "+ nickColor);
                                continue;
                            }
                        }
                        if (targetName == null) {
                            if (CorePlusAPI.getPlayer().isPlayerPlayerBefore(args[i])) {
                                if (CorePlusAPI.getPlayer().isPlayerOnline(args[i])) {
                                    targetName = args[i];
                                    continue;
                                }
                            }
                        }
                        if (nickName == null)
                            nickName = args[i];
                    }
                    if (targetName == null) {
                        if (sender instanceof ConsoleCommandSender) {
                            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                                    "Message.onlyPlayer", sender);
                            return true;
                        }
                        targetName = sender.getName();
                    }
                    nickColor = CorePlusAPI.getUtils().getColorCode(nickColor);

                    Nick.set(sender, targetName, nickName, nickColor, true);
                    return true;
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                    return true;
                }
            case "dataconvert":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.dataconvert")) {

                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
        }
        CorePlusAPI.getMsg().

                sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.unknownCommand", sender);
        return true;
    }
}