package tw.momocraft.playerdataplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.utils.BungeeCord;
import tw.momocraft.playerdataplus.utils.Utils;

public class ServerHandler {
    public static void sendConsoleMessage(String message) {
        String prefix = "&7[&dPlayerdataPlus&7] ";
        message = prefix + message;
        message = ChatColor.translateAlternateColorCodes('&', message);
        PlayerdataPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    public static void sendDebugMessage(String message) {
        if (ConfigHandler.getDebugging()) {
            String prefix = "&7[&dPlayerdataPlus_Debug&7] ";
            message = prefix + message;
            message = ChatColor.translateAlternateColorCodes('&', message);
            PlayerdataPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
        }
    }

    public static void sendErrorMessage(String message) {
        String prefix = "&7[&cPlayerdataPlus_ERROR&7]&c ";
        message = prefix + message;
        message = ChatColor.translateAlternateColorCodes('&', message);
        PlayerdataPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    public static void sendPlayerMessage(Player player, String message) {
        String prefix = "&7[&dPlayerdataPlus&7] ";
        message = prefix + message;
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (message.contains("blankmessage")) {
            message = "";
        }
        player.sendMessage(message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        String prefix = "&7[&dPlayerdataPlus&7] ";
        message = prefix + message;
        message = ChatColor.translateAlternateColorCodes('&', message);
        sender.sendMessage(message);
    }

    public static void sendDebugTrace(Exception e) {
        if (ConfigHandler.getDebugging()) {
            e.printStackTrace();
        }
    }

    public static void debugMessage(String feature, String depiction) {
        if (ConfigHandler.getDebugging()) {
            ServerHandler.sendDebugMessage("&8" + feature + " - &f" + depiction);
        }
    }

    public static void debugMessage(String feature, String target, String check, String action, String detail) {
        if (ConfigHandler.getDebugging()) {
            if (action.equals("return")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&a" + action + "&8, " + detail);
            } else if (action.equals("cancel")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action + "&8, " + detail);
            } else if (action.equals("continue")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action + "&8, " + detail);
            } else if (action.equals("break")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action + "&8, " + detail);
            } else if (action.equals("bypass")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action + "&8, " + detail);
            } else if (action.equals("remove")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action + "&8, " + detail);
            } else if (action.equals("change")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action + "&8, " + detail);
            } else if (action.equals("kill")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action + "&8, " + detail);
            } else if (action.equals("damage")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action + "&8, " + detail);
            }
        }
    }

    public static void debugMessage(String feature, String target, String check, String action) {
        if (ConfigHandler.getDebugging()) {
            if (action.equals("return")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&a" + action);
            } else if (action.equals("cancel")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action);
            } else if (action.equals("continue")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action);
            } else if (action.equals("break")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action);
            } else if (action.equals("bypass")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action);
            } else if (action.equals("remove")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action);
            } else if (action.equals("change")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action);
            } else if (action.equals("kill")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action);
            } else if (action.equals("damage")) {
                ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action);
            }
        }
    }

    public static void executeCommands(Player player, String input) {
        if (player != null && !(player instanceof ConsoleCommandSender)) {
            if (input.startsWith("log:")) {
                input = input.replace("log: ", "");
                sendConsoleMessage(input);
                return;
            } else if (input.startsWith("broadcast:")) {
                input = input.replace("broadcast: ", "");
                Bukkit.broadcastMessage(input);
                return;
            } else if (input.startsWith("console:")) {
                input = input.replace("console: ", "");
                dispatchConsoleCommand(player, input, true);
                return;
            } else if (input.startsWith("op:")) {
                input = input.replace("op: ", "");
                dispatchOpCommand(player, input, true);
                return;
            } else if (input.startsWith("player:")) {
                input = input.replace("player: ", "");
                dispatchPlayerCommand(player, input, true);
                return;
            } else if (input.startsWith("chat:")) {
                input = input.replace("chat: ", "");
                dispatchChatCommand(player, input, true);
                return;
            } else if (input.startsWith("message:")) {
                input = input.replace("message: ", "");
                dispatchMessageCommand(player, input, true);
                return;
            } else if (input.startsWith("bungee:")) {
                input = input.replace("bungee: ", "");
                dispatchBungeeCordCommand(player, input, true);
                return;
            } else if (input.startsWith("switch:")) {
                input = input.replace("switch: ", "");
                dispatchServerSwitchCommand(player, input);
                return;
            }
            dispatchConsoleCommand(null, input);
        } else {
            executeCommands(input);
        }
    }

    public static void executeCommands(String input) {
        if (input.startsWith("log:")) {
            input = input.replace("log: ", "");
            ServerHandler.sendConsoleMessage(input);
            return;
        } else if (input.startsWith("broadcast:")) {
            input = input.replace("broadcast: ", "");
            Bukkit.broadcastMessage(input);
            return;
        } else if (input.startsWith("console:")) {
            input = input.replace("console: ", "");
            dispatchConsoleCommand(null, input, true);
            return;
        } else if (input.startsWith("op:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&eop: " + input + "&c\" &8- &cCan not find the execute target.");
            return;
        } else if (input.startsWith("player:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&eplayer:" + input + "&c\" &8- &cCan not find the execute target.");
            return;
        } else if (input.startsWith("chat:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&echat: " + input + "&c\" &8- &cCan not find the execute target.");
            return;
        } else if (input.startsWith("message:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&emessage: " + input + "&c\" &8- &cCan not find the execute target.");
            return;
        } else if (input.startsWith("bungee:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&ebungee: " + input + "&c\" &8- &cCan not find the execute target.");
            return;
        } else if (input.startsWith("switch:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&eswitch: " + input + "&c\" &8- &cCan not find the execute target.");
            return;
        }
        dispatchConsoleCommand(null, input);
    }

    private static void dispatchConsoleCommand(Player player, String command) {
        try {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utils.translateLayout(command, player));
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a console command, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    private static void dispatchConsoleCommand(Player player, String command, boolean placeholder) {
        if (player != null) {
            try {
                if (placeholder) {
                    player.chat("/" + Utils.translateLayout(command, player));
                } else {
                    player.chat("/" + command);
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utils.translateLayout(command, player));
            } catch (Exception e) {
                ServerHandler.sendErrorMessage("&cThere was an issue executing a console command, if this continues please report it to the developer!");
                ServerHandler.sendDebugTrace(e);
            }
        } else {
            try {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            } catch (Exception e) {
                ServerHandler.sendErrorMessage("&cThere was an issue executing a console command, if this continues please report it to the developer!");
                ServerHandler.sendDebugTrace(e);
            }
        }
    }

    private static void dispatchOpCommand(Player player, String command) {
        boolean isOp = player.isOp();
        try {
            player.setOp(true);
            player.chat("/" + command);
        } catch (Exception e) {
            ServerHandler.sendDebugTrace(e);
            player.setOp(isOp);
            ServerHandler.sendErrorMessage("&cAn error has occurred while setting " + player.getName() + " status on the OP list, to ensure server security they have been removed as an OP.");
        } finally {
            player.setOp(isOp);
        }
    }

    private static void dispatchOpCommand(Player player, String command, boolean placeholder) {
        boolean isOp = player.isOp();
        try {
            player.setOp(true);
            if (placeholder) {
                player.chat("/" + Utils.translateLayout(command, player));
            } else {
                player.chat("/" + command);
            }
        } catch (Exception e) {
            ServerHandler.sendDebugTrace(e);
            player.setOp(isOp);
            ServerHandler.sendErrorMessage("&cAn error has occurred while setting " + player.getName() + " status on the OP list, to ensure server security they have been removed as an OP.");
        } finally {
            player.setOp(isOp);
        }
    }

    private static void dispatchPlayerCommand(Player player, String command) {
        try {
            player.chat("/" + command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a player command, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    private static void dispatchPlayerCommand(Player player, String command, boolean placeholder) {
        try {
            if (placeholder) {
                player.chat("/" + Utils.translateLayout(command, player));
            } else {
                player.chat("/" + command);
            }
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a player command, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }


    private static void dispatchChatCommand(Player player, String command) {
        try {
            player.chat(command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a player command, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    private static void dispatchChatCommand(Player player, String command, boolean placeholder) {
        try {
            if (placeholder) {
                player.chat(Utils.translateLayout(command, player));
            } else {
                player.chat(command);
            }
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a player command, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    private static void dispatchMessageCommand(Player player, String command) {
        try {
            player.sendMessage(command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a command to send a message, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    private static void dispatchMessageCommand(Player player, String command, boolean placeholder) {
        try {
            if (placeholder) {
                player.sendMessage(Utils.translateLayout(command, player));
            } else {
                player.sendMessage(command);
            }
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a command to send a message, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    private static void dispatchServerSwitchCommand(Player player, String server) {
        try {
            BungeeCord.SwitchServers(player, server);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing an item's command to switch servers, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    private static void dispatchBungeeCordCommand(Player player, String command) {
        try {
            BungeeCord.ExecuteCommand(player, command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing an item's command to BungeeCord, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }
    private static void dispatchBungeeCordCommand(Player player, String command, boolean placeholder) {
        try {
            if (placeholder) {
                BungeeCord.ExecuteCommand(player, Utils.translateLayout(command, player));
            } else {
                BungeeCord.ExecuteCommand(player, command);
            }
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing an item's command to BungeeCord, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }
}
