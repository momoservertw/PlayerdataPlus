package tw.momocraft.playerdataplus.utils.locationutils;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationUtils {

    private Map<String, LocationMap> locMaps;

    public LocationUtils() {
        setUp();
    }

    /**
     * Setup LocMaps.
     */
    private void setUp() {
        locMaps = new HashMap<>();
        ConfigurationSection locConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("General.Location");
        if (locConfig == null) return;

        ConfigurationSection groupConfig;
        LocationMap locMap;
        ConfigurationSection areaConfig;

        for (String group : locConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("General.Location." + group);

            if (groupConfig == null) continue;

            locMap = new LocationMap();
            areaConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("General.Location." + group + ".Area");
            locMap.setWorlds(ConfigHandler.getConfig("config.yml").getStringList("General.Location." + group + ".Worlds"));
            if (areaConfig != null) {
                for (String area : areaConfig.getKeys(false)) {
                    locMap.addCord(area, ConfigHandler.getConfig("config.yml").getString("General.Location." + group + ".Area." + area));
                }
            }
            locMaps.put(group, locMap);
        }
    }

    /**
     * @return get LocMaps.
     */
    public Map<String, LocationMap> getLocMaps() {
        return locMaps;
    }

    /**
     * @param path the specific path.
     * @return the specific maps from LocMaps.
     */
    public List<LocationMap> getSpeLocMaps(String file, String path) {
        List<LocationMap> locMapList = new ArrayList<>();
        LocationMap locMap;
        LocationMap locWorldMap = new LocationMap();
        for (String group : ConfigHandler.getConfig(file).getStringList(path)) {
            locMap = locMaps.get(group);
            if (locMap != null) {
                locMapList.add(locMap);
            } else {
                locWorldMap.addWorld(group);
            }
        }
        if (!locWorldMap.getWorlds().isEmpty()) {
            locMapList.add(locWorldMap);
        }
        return locMapList;
    }

    /**
     * @param loc     location.
     * @param locMaps the checking location maps.
     * @return if the location is one of locMaps.
     */
    public boolean checkLocation(Location loc, List<LocationMap> locMaps) {
        if (locMaps.isEmpty()) {
            return true;
        }
        String worldName = loc.getWorld().getName();
        Map<String, String> cord;
        back:
        for (LocationMap locMap : locMaps) {
            if (locMap.getWorlds().contains("global") || locMap.getWorlds().contains(worldName)) {
                cord = locMap.getCord();
                if (cord != null) {
                    for (String key : cord.keySet()) {
                        if (!isCord(loc, key, cord.get(key))) {
                            continue back;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @param loc   location.
     * @param type  the checking name of "x, y, z" in for loop.
     * @param value the value of "x, y, z" in config.yml. It contains operator, range and value..
     * @return if the entity spawn in key's (x, y, z) location range.
     */
    private boolean isCord(Location loc, String type, String value) {
        String[] values = value.split("\\s+");
        int length = values.length;
        try {
            if (length == 1) {
                // X: 1000
                // R: 1000
                switch (type) {
                    case "X":
                        return getRange(loc.getBlockX(), Integer.parseInt(values[0]));
                    case "Y":
                        return getRange(loc.getBlockY(), Integer.parseInt(values[0]));
                    case "Z":
                        return getRange(loc.getBlockZ(), Integer.parseInt(values[0]));
                    case "!X":
                        return !getRange(loc.getBlockX(), Integer.parseInt(values[0]));
                    case "!Y":
                        return !getRange(loc.getBlockY(), Integer.parseInt(values[0]));
                    case "!Z":
                        return !getRange(loc.getBlockZ(), Integer.parseInt(values[0]));
                    case "R":
                        return getRound(loc, Integer.parseInt(values[0]));
                    case "!R":
                        return !getRound(loc, Integer.parseInt(values[0]));
                    case "S":
                        return getSquared(loc, Integer.parseInt(values[0]));
                    case "!S":
                        return !getSquared(loc, Integer.parseInt(values[0]));
                }
            } else if (length == 2) {
                // X: ">= 1000"
                switch (type) {
                    case "X":
                        return getCompare(values[0], loc.getBlockX(), Integer.parseInt(values[1]));
                    case "Y":
                        return getCompare(values[0], loc.getBlockY(), Integer.parseInt(values[1]));
                    case "Z":
                        return getCompare(values[0], loc.getBlockZ(), Integer.parseInt(values[1]));
                    case "!X":
                        return !getCompare(values[0], loc.getBlockX(), Integer.parseInt(values[1]));
                    case "!Y":
                        return !getCompare(values[0], loc.getBlockY(), Integer.parseInt(values[1]));
                    case "!Z":
                        return !getCompare(values[0], loc.getBlockZ(), Integer.parseInt(values[1]));
                }
            } else if (length == 3) {
                // X: "-1000 ~ 1000"
                // R: "1000 0 0"
                switch (type) {
                    case "X":
                        return getRange(loc.getBlockX(), Integer.parseInt(values[0]), Integer.parseInt(values[2]));
                    case "Y":
                        return getRange(loc.getBlockY(), Integer.parseInt(values[0]), Integer.parseInt(values[2]));
                    case "Z":
                        return getRange(loc.getBlockZ(), Integer.parseInt(values[0]), Integer.parseInt(values[2]));
                    case "!X":
                        return !getRange(loc.getBlockX(), Integer.parseInt(values[0]), Integer.parseInt(values[2]));
                    case "!Y":
                        return !getRange(loc.getBlockY(), Integer.parseInt(values[0]), Integer.parseInt(values[2]));
                    case "!Z":
                        return !getRange(loc.getBlockZ(), Integer.parseInt(values[0]), Integer.parseInt(values[2]));
                    case "R":
                        return getRound(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    case "!R":
                        return !getRound(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    case "S":
                        return getSquared(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    case "!S":
                        return !getSquared(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                }
            } else if (length == 4) {
                // X: "-1000 ~ 1000"
                // R: "1000 0 0"
                switch (type) {
                    case "R":
                        return getRound(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]));
                    case "!R":
                        return !getRound(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]));
                    case "S":
                        return getSquared(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]));
                    case "!S":
                        return !getSquared(loc, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]));
                }
            }
        } catch (Exception e) {
            ServerHandler.sendConsoleMessage("&cThere is an error occurred. Please check the \"Location\" format.");
            ServerHandler.sendConsoleMessage("&c" + type + ": " + value);
        }
        return false;
    }

    /**
     * @param operator the comparison operator to compare two numbers.
     * @param number1  first number.
     * @param number2  second number.
     */
    private boolean getCompare(String operator, int number1, int number2) {
        switch (operator) {
            case ">":
                return number1 > number2;
            case "<":
                return number1 < number2;
            case ">=":
            case "=>":
                return number1 >= number2;
            case "<=":
            case "=<":
                return number1 <= number2;
            case "==":
            case "=":
                return number1 == number2;
        }
        return false;
    }

    /**
     * @param number the checking number.
     * @param r1     the first side of range.
     * @param r2     another side of range.
     * @return if the check number is inside the range.
     * It will return false if the two side of range numbers are equal.
     */
    private boolean getRange(int number, int r1, int r2) {
        return r1 <= number && number <= r2 || r2 <= number && number <= r1;
    }

    /**
     * @param number the location of event.
     * @param r      the side of range.
     * @return if the check number is inside the range.
     */
    private boolean getRange(int number, int r) {
        return -r <= number && number <= r || r <= number && number <= -r;
    }

    /**
     * @param loc location.
     * @param r   the checking radius.
     * @param x   the center checking X.
     * @param y   the center checking Y
     * @param z   the center checking Z
     * @return if the entity spawn in three-dimensional radius.
     */
    private boolean getRound(Location loc, int r, int x, int y, int z) {
        x = loc.getBlockX() - x;
        y = loc.getBlockY() - y;
        z = loc.getBlockZ() - z;
        return r > Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    /**
     * @param loc location.
     * @param r   the checking radius.
     * @param x   the center checking X.
     * @param z   the center checking Z
     * @return if the entity spawn in flat radius.
     */
    private boolean getRound(Location loc, int r, int x, int z) {
        x = loc.getBlockX() - x;
        z = loc.getBlockZ() - z;
        return r > Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
    }

    /**
     * @param loc location.
     * @param r   the checking radius.
     * @return if the entity spawn in flat radius.
     */
    private boolean getRound(Location loc, int r) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        return r > Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
    }

    /**
     * @param loc location.
     * @param r   the checking radius.
     * @param x   the center checking X.
     * @param y   the center checking Y
     * @param z   the center checking Z
     * @return if the entity spawn in three-dimensional radius.
     */
    private boolean getSquared(Location loc, int r, int x, int y, int z) {
        return r > loc.getBlockX() - x && r > loc.getBlockY() - y && r > loc.getBlockZ() - z;
    }

    /**
     * @param loc location.
     * @param r   the checking radius.
     * @param x   the center checking X.
     * @param z   the center checking Z
     * @return if the entity spawn in flat radius.
     */
    private boolean getSquared(Location loc, int r, int x, int z) {
        return r > loc.getBlockX() - x && r > loc.getBlockZ() - z;
    }

    /**
     * @param loc location.
     * @param r   the checking radius.
     * @return if the entity spawn in flat radius.
     */
    private boolean getSquared(Location loc, int r) {
        return r > loc.getBlockX() && r > loc.getBlockZ();
    }
}