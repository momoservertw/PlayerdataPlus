package tw.momocraft.playerdataplus.utils;

import tw.momocraft.playerdataplus.handlers.ConfigHandler;

public class PlayerdataConfig {
    private int cleanMaxData;

    public PlayerdataConfig() {
        setUp();
    }

    private void setUp() {
        cleanMaxData = ConfigHandler.getConfig("config.yml").getInt("Clean.Settings.Max-Clean-Per-Data");
    }

    public int getCleanMaxData() {
        return cleanMaxData;
    }
}
