package tw.momocraft.playerdataplus.utils.locationutils;

import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationMap {

    private List<String> worlds;
    private final Map<String, String> cord;

    public LocationMap() {
        worlds = new ArrayList<>();
        cord = new HashMap<>();
    }

    public void setWorlds(List<String> worlds) {
        this.worlds = worlds;
    }

    public void addWorld(String world) {
        this.worlds.add(world);
    }

    public void addCord(String type, String value) {
        if (isCordFormat(type, value)) {
            cord.put(type, value);
        } else {
            ServerHandler.sendConsoleMessage("&cThere is an error occurred. Please check the \"Location\" format.");
            ServerHandler.sendConsoleMessage("&c" + type + ": " + value);
        }
    }

    public List<String> getWorlds() {
        return worlds;
    }

    Map<String, String> getCord() {
        return cord;
    }

    /**
     * @param type  the location value. Like x, y, z, !x...
     * @param value the value of the type.
     * @return check if the coordinate format is correct.
     */
    private static boolean isCordFormat(String type, String value) {
        String[] values = value.split("\\s+");
        int valueLen = values.length;
        int typeLen = type.length();
        if (valueLen == 1) {
            if (typeLen == 1) {
                if (type.matches("[XYZRS]")) {
                    return values[0].matches("-?[0-9]\\d*$");
                }
            } else if (typeLen == 2) {
                if (type.matches("[!][XYZRS]")) {
                    return values[0].matches("-?[0-9]\\d*$");
                } else if (type.matches("[XYZ][XYZ]")) {
                    return values[0].matches("-?[0-9]\\d*$");
                }
            }
        } else if (valueLen == 2) {
            if (typeLen == 1) {
                if (type.matches("[XYZ]")) {
                    if (values[0].length() == 1 && values[0].matches("[><=]") ||
                            values[0].length() == 2 && values[0].matches("[><=][><=]")) {
                        return values[1].matches("-?[0-9]\\d*$");
                    }
                }
            } else if (typeLen == 2) {
                if (type.matches("[!][XYZ]")) {
                    if (values[0].length() == 1 && values[0].matches("[><=]") || values[0].length() == 2 &&
                            values[0].matches("[><=][><=]")) {
                        return values[1].matches("-?[0-9]\\d*$");
                    }
                }
            }
        } else if (valueLen == 3) {
            if (typeLen == 1) {
                if (type.matches("[RS]")) {
                    return values[0].matches("-?[0-9]\\d*$") && values[1].matches("-?[0-9]\\d*$") &&
                            values[2].matches("-?[0-9]\\d*$");
                } else if (type.matches("[XYZ]")) {
                    if (values[0].matches("-?[0-9]\\d*$") && values[2].matches("-?[0-9]\\d*$")) {
                        return values[1].equals("~");
                    }
                }
            } else if (typeLen == 2) {
                if (type.matches("[!][RS]")) {
                    return values[0].matches("-?[0-9]\\d*$") && values[1].matches("-?[0-9]\\d*$") &&
                            values[2].matches("-?[0-9]\\d*$");
                } else if (type.matches("[XYZ][XYZ]")) {
                    if (values[0].matches("-?[0-9]\\d*$") && values[2].matches("-?[0-9]\\d*$")) {
                        return values[1].equals("~");
                    }
                } else if (type.matches("[!][XYZ]")) {
                    if (values[0].matches("-?[0-9]\\d*$") && values[2].matches("-?[0-9]\\d*$")) {
                        return values[1].equals("~");
                    }
                }
            }
        } else if (valueLen == 4) {
            if (typeLen == 1) {
                if (type.matches("[RS]")) {
                    return values[0].matches("-?[0-9]\\d*$") && values[1].matches("-?[0-9]\\d*$") &&
                            values[2].matches("-?[0-9]\\d*$") && values[3].matches("-?[0-9]\\d*$");
                }
            } else if (typeLen == 2) {
                if (type.matches("[!][RS]")) {
                    return values[0].matches("-?[0-9]\\d*$") && values[1].matches("-?[0-9]\\d*$") &&
                            values[2].matches("-?[0-9]\\d*$") && values[3].matches("-?[0-9]\\d*$");
                }
            }
        }
        return false;
    }
}
