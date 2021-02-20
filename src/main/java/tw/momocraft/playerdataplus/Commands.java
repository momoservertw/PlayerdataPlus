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
            if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.use")) {
                CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender,
                        "");
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgTitle(), sender);
                CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender,
                        "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgHelp(), sender);
                CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "");
            } else {
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(),
                        ConfigHandler.getPrefix(), "Message.noPermission", sender);
            }
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.use")) {
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "");
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgTitle(), sender);
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender,
                            "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                    + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgHelp(), sender);
                    if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.reload")) {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgReload(), sender);
                    }
                    if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.version")) {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                                ConfigHandler.getConfigPath().getMsgVersion(), sender);
                    }
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "");
                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "reload":
                if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.reload")) {
                    ConfigHandler.generateData(true);
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.configReload", sender);
                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "version":
                if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.version")) {
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender,
                            "&f " + PlayerdataPlus.getInstance().getDescription().getName()
                                    + " &ev" + PlayerdataPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getUpdateManager().check(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(), sender,
                            PlayerdataPlus.getInstance().getName(), PlayerdataPlus.getInstance().getDescription().getVersion(), true);
                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "clean":
                if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.clean")) {
                    if (length == 2) {

                    }
                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "nick":
                if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.nick")) {
                    if (length == 3) {
                        if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.nick.other")) {
                            //Nick.change()
                        } else {
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                                    "Message.noPermission", sender);
                        }
                    }
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nick", sender);
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nickOff", sender);
                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "playerstatus":
                if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.playerstatus")) {
                    if (length == 3) {
                        if (args[1].equals("start")) {

                        } else if (args[1].equals("stop")) {

                        }
                    }
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.PlayerdataPlus.Commands.nickOff", sender);
                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "dataconvert":
                if (CorePlusAPI.getPlayerManager().hasPerm(sender, "playerdataplus.command.dataconvert")) {

                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
        }
        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                "Message.unknownCommand", sender);
        return true;
    }
}