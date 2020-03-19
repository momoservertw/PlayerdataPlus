package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.*;


public class ColorCorrespond {
    private Map<String, String> colorMap = new HashMap<>();

    public ColorCorrespond() {
        setUp();
    }

    private void setUp() {
        ConfigurationSection colorConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Nick.Colors.Correspond");
        if (colorConfig != null) {
            for (String key : colorConfig.getKeys(false)) {
                colorMap.put(key, String.valueOf(colorConfig.get(key)));
            }
        }
    }

    private Map<String, String> getColorMap() {
        return this.colorMap;
    }

    public String getColorCode(String color) {
        if (color.length() == 1) {
            String pattern = "[a-fA-F0-9]";
            if (color.matches(pattern)) {
                return color;
            }
        } else {
            String keyColor;
            for (String key : colorMap.keySet()) {
                keyColor = colorMap.get(key);
                if (keyColor.equals(color)) {
                    ServerHandler.sendConsoleMessage(key);
                    return key;
                }
            }
        }
        return "";
    }
}
