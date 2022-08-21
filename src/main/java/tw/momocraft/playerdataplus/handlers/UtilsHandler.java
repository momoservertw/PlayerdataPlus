package tw.momocraft.playerdataplus.handlers;

import tw.momocraft.playerdataplus.features.playerdata.PlayerData;

public class UtilsHandler {

    private static DependHandler dependence;
    private static PlayerData playerData;

    public static void setup(boolean reload) {
        dependence = new DependHandler();
        dependence.setup(reload);
        playerData = new PlayerData();
        playerData.setupMySQL();
    }

    public static DependHandler getDepend() {
        return dependence;
    }

    public static PlayerData getPlayerData() {
        return playerData;
    }
}
