package tw.momocraft.playerdataplus.utils;

import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ColorCorrespond {
    private Map<String, String> colorMap = new HashMap<>();
    private final List<String> colorList = new ArrayList<>();

    public ColorCorrespond() {
        setUp();
    }

    private void setUp() {
        colorMap = ConfigHandler.getConfigPath().getNickColorsMap();
        colorList.addAll(colorMap.values());
        String[] colorArray = new String[]{"a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        Collections.addAll(colorList, colorArray);
    }

    public List<String> getColorList() {
        return colorList;
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
