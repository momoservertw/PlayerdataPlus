package tw.momocraft.playerdataplus.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

public class Language {
    private static Lang langType = Lang.ENGLISH;

    public static void dispatchMessage(CommandSender sender, String langMessage, boolean hasPrefix) {
        if (hasPrefix) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            langMessage = Utils.translateLayout(langMessage, player);
            String prefix = Utils.translateLayout(ConfigHandler.getConfig(langType.nodeLocation()).getString("Message.prefix"), player);
            if (prefix == null) {
                prefix = "";
            } else {
                prefix += "";
            }
            langMessage = prefix + langMessage;
            sender.sendMessage(langMessage);
        } else {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            langMessage = Utils.translateLayout(langMessage, player);
            sender.sendMessage(langMessage);
        }
    }

    public static void dispatchMessage(CommandSender sender, String langMessage) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        langMessage = Utils.translateLayout(langMessage, player);
        sender.sendMessage(langMessage);
    }

    public static void sendLangMessage(String nodeLocation, CommandSender sender, String... placeHolder) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        String langMessage = ConfigHandler.getConfig(langType.nodeLocation()).getString(nodeLocation);
        String prefix = Utils.translateLayout(ConfigHandler.getConfig(langType.nodeLocation()).getString("Message.prefix"), player);
        if (prefix == null) {
            prefix = "";
        } else {
            prefix += "";
        }
        if (langMessage != null && !langMessage.isEmpty()) {
            langMessage = translateLangHolders(langMessage, initializeRows(placeHolder));
            langMessage = Utils.translateLayout(langMessage, player);
            String[] langLines = langMessage.split(" /n ");
            for (String langLine : langLines) {
                String langStrip = prefix + langLine;
                    sender.sendMessage(langStrip);
            }
        }
    }
    public static void sendLangMessage(String nodeLocation, CommandSender sender, boolean hasPrefix, String... placeHolder) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        String prefix = "";
        if (hasPrefix) {
            prefix = Utils.translateLayout(ConfigHandler.getConfig("config.yml").getString("Message.prefix"), player);
        }
        String langMessage = ConfigHandler.getConfig("config.yml").getString(nodeLocation);
        if (langMessage != null && !langMessage.isEmpty()) {
            langMessage = translateLangHolders(langMessage, initializeRows(placeHolder));
            langMessage = Utils.translateLayout(langMessage, player);
            String[] langLines = langMessage.split(" /n ");
            for (String langLine : langLines) {
                String langStrip = prefix + langLine;
                sender.sendMessage(langStrip);
            }
        }
    }

    private static String[] initializeRows(String... placeHolder) {
        if (placeHolder == null || placeHolder.length != newString().length) {
            String[] langHolder = Language.newString();
            for (int i = 0; i < langHolder.length; i++) {
                langHolder[i] = "null";
            }
            return langHolder;
        } else {
            String[] langHolder = placeHolder;
            for (int i = 0; i < langHolder.length; i++) {
                if (langHolder[i] == null) {
                    langHolder[i] = "null";
                }
            }
            return langHolder;
        }
    }

    private static String translateLangHolders(String langMessage, String... langHolder) {
        return langMessage
                .replace("%command%", langHolder[0])
                .replace("%player%", langHolder[1])
                .replace("%targetplayer%", langHolder[2])
                .replace("%nick%", langHolder[3])
                .replace("%nick_color%", langHolder[4])
                .replace("%nick_length%", ConfigHandler.getConfig("config.yml").getString("Nick.Limits.Length"));
    }

    public static String[] newString() {
        return new String[14];
    }


    private enum Lang {
        DEFAULT("config.yml", 0), ENGLISH("config.yml", 1);

        private Lang(final String nodeLocation, final int i) {
            this.nodeLocation = nodeLocation;
        }

        private final String nodeLocation;

        private String nodeLocation() {
            return nodeLocation;
        }
    }

    private static boolean isConsoleMessage(String nodeLocation) {
        return false;
    }
}