package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.util.*;


public class ColorCorrespond {
    private Map<String, String> colorMap = new HashMap<>();

    public ColorCorrespond() {
     setUp();
    }

    private void setUp() {
        ConfigurationSection colorConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Nick.Colors");
        if (colorConfig != null) {
            for (String key : colorConfig.getKeys(false)) {
                colorMap.put(key, String.valueOf(colorConfig.get(key)));
            }
        }
    }

    public Map<String, String> getColorMap() {
        return this.colorMap;
    }

    public String getColorCode(String color) {
        Map<String, String> colorMap = ConfigHandler.getColors().getColorMap();
        for (String key : colorMap.keySet()) {
            if (color.equals(key)) {
                return colorMap.get(key);
            }
        }
        return "";
    }
}
