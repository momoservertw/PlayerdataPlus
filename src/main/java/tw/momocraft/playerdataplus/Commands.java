package tw.momocraft.playerdataplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

public class Commands implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        int length = args.length;
        if (length == 0) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.use")) {
                CorePlusAPI.getLang().sendMsg(ConfigHandler.getPrefix(), sender,
                        "");
                CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgTitle(), sender);
                CorePlusAPI.getLang().sendMsg(ConfigHandler.getPrefix(), sender,
                        "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgHelp(), sender);
                CorePlusAPI.getLang().sendMsg(ConfigHandler.getPrefix(), sender, "");
            } else {
                CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(),
                        ConfigHandler.getPrefix(), "Message.noPermission", sender);
            }
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.use")) {
                    CorePlusAPI.getLang().sendMsg(ConfigHandler.getPrefix(), sender, "");
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgTitle(), sender);
                    CorePlusAPI.getLang().sendMsg(ConfigHandler.getPrefix(), sender,
                            "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                    + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgHelp(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.reload")) {
                        CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgReload(), sender);
                    }
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.version")) {
                        CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgVersion(), sender);
                    }
                    CorePlusAPI.getLang().sendMsg(ConfigHandler.getPrefix(), sender, "");
                } else {
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "reload":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.reload")) {
                    ConfigHandler.generateData(true);
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.configReload", sender);
                } else {
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "version":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.version")) {
                    CorePlusAPI.getLang().sendMsg(ConfigHandler.getPrefix(), sender,
                            "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                    + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getUpdate().check(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(), sender,
                            PlayerdataPlus.getInstance().getName(), PlayerdataPlus.getInstance().getDescription().getVersion(), true);
                } else {
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "clean":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.clean")) {
                    if (length == 2) {

                    }
                } else {
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "nick":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick")) {
                    if (length == 3) {
                        if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.nick.other")) {
                            //Nick.change()
                        } else {
                            CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                                    "Message.noPermission", sender);
                        }
                    }
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nick", sender);
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nickOff", sender);
                } else {
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
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
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nickOff", sender);
                } else {
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "dataconvert":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "playerdataplus.command.dataconvert")) {

                } else {
                    CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
        }
        CorePlusAPI.getLang().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                "Message.unknownCommand", sender);
        return true;
    }
}