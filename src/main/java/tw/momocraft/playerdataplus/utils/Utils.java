package tw.momocraft.playerdataplus.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.*;

import static tw.momocraft.playerdataplus.handlers.ConfigHandler.getColors;

public class Utils {

    public static boolean containsIgnoreCase(String string1, String string2) {
        if (string1 != null && string2 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }

    public static Integer returnInteger(String text) {
        if (text == null) {
            return null;
        } else {
            char[] characters = text.toCharArray();
            Integer value = null;
            boolean isPrevDigit = false;
            for (int i = 0; i < characters.length; i++) {
                if (isPrevDigit == false) {
                    if (Character.isDigit(characters[i])) {
                        isPrevDigit = true;
                        value = Character.getNumericValue(characters[i]);
                    }
                } else {
                    if (Character.isDigit(characters[i])) {
                        value = (value * 10) + Character.getNumericValue(characters[i]);
                    } else {
                        break;
                    }
                }
            }
            return value;
        }
    }

    private static String getNearbyPlayer(Player player, int range) {
        try {
            ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
            ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight((Set<Material>) null, range);
            ArrayList<Location> sight = new ArrayList<Location>();
            for (int i = 0; i < sightBlock.size(); i++)
                sight.add(sightBlock.get(i).getLocation());
            for (int i = 0; i < sight.size(); i++) {
                for (int k = 0; k < entities.size(); k++) {
                    if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
                        if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
                            if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
                                if (entities.get(k) instanceof Player) {
                                    return entities.get(k).getName();
                                }
                            }
                        }
                    }
                }
            }
            return "INVALID";
        } catch (NullPointerException e) {
            return "INVALID";
        }
    }

    public static String translateLayout(String name, Player player) {
        String playerName = "EXEMPT";

        if (player != null) { playerName = player.getName(); }

        if (player != null && !(player instanceof ConsoleCommandSender)) {
            try { name = name.replace("%player%", playerName); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
            try { name = name.replace("%mob_kills%", String.valueOf(player.getStatistic(Statistic.MOB_KILLS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
            try { name = name.replace("%player_kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
            try { name = name.replace("%player_deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
            try { name = name.replace("%player_food%", String.valueOf(player.getFoodLevel())); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
            try { name = name.replace("%player_health%", String.valueOf(player.getHealth())); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
            try { name = name.replace("%player_location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ""); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
            try { name = name.replace("%player_interact%", getNearbyPlayer(player, 3)); } catch (Exception e) { ServerHandler.sendDebugTrace(e); } }
        if (player == null) { try { name = name.replace("%player%", "CONSOLE"); } catch (Exception e) { ServerHandler.sendDebugTrace(e); } }

        name = ChatColor.translateAlternateColorCodes('&', name);
        if (ConfigHandler.getDepends().PlaceHolderAPIEnabled()) {
            try { try { return PlaceholderAPI.setPlaceholders(player, name); }
            catch (NoSuchFieldError e) { ServerHandler.sendDebugMessage("Error has occured when setting the PlaceHolder " + e.getMessage() + ", if this issue persits contact the developer of PlaceholderAPI."); return name; }
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
        }
        return name;
    }
}