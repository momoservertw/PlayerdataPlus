package tw.momocraft.playerdataplus.handlers;

import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.utils.file.maps.MySQLMap;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.features.playerdata.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilsHandler {

    private static DependHandler dependence;

    private static PlayerData playerData;

    public static void setup(boolean reload) {
        dependence = new DependHandler();
        dependence.setup(reload);

        //playerData = new PlayerData();
        setupMySQL();
    }

    public static DependHandler getDepend() {
        return dependence;
    }

    /*
    public static PlayerData getPlayerData() {
        return playerData;
    }
     */

    public static void setupMySQL() {
        try {
            MySQLMap mySQLMap = new MySQLMap();
            Map<String, String> tableMap = new HashMap<>();
            mySQLMap.setGroupName("playerdataplus");
            mySQLMap.setDatabase(ConfigHandler.getConfigPath().getMysqlDatabase());
            mySQLMap.setHostName(ConfigHandler.getConfigPath().getMysqlHost());
            mySQLMap.setPort(ConfigHandler.getConfigPath().getMysqlPort());
            mySQLMap.setUsername(ConfigHandler.getConfigPath().getMysqlUsername());
            mySQLMap.setPassword(ConfigHandler.getConfigPath().getMysqlPassword());

            tableMap.put("players", ConfigHandler.getConfigPath().getMysqlPrefix() + "players");
            tableMap.put("playerdata", ConfigHandler.getConfigPath().getMysqlPrefix() + "playerdata");
            mySQLMap.setTables(tableMap);

            // Connect the MySQL.
            if (!CorePlusAPI.getFile().getMySQL().connect(ConfigHandler.getPluginName(), mySQLMap)) {
                CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                        "Can not connect the MySQL database.");
                CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                        "Please check the settings in config.yml.");
                PlayerdataPlus.disablePlugin();
                return;
            }

            // Table: players
            // Create the table if not exist.
            List<String> columnsList = new ArrayList<>();
            columnsList.add("uuid");
            columnsList.add("player_name");
            columnsList.add("display_name");
            columnsList.add("nick_name");
            columnsList.add("last_login");

            CorePlusAPI.getFile().getMySQL().createTables(ConfigHandler.getPluginName(),
                    mySQLMap.getDatabase(),
                    tableMap.get("players"),
                    columnsList);

            // Table: playerdata
            // Create the table if not exist.
            columnsList = new ArrayList<>();
            columnsList.add("uuid");
            columnsList.add("variable_key");
            columnsList.add("variable_value");
            CorePlusAPI.getFile().getMySQL().createTables(ConfigHandler.getPluginName(),
                    mySQLMap.getDatabase(),
                    tableMap.get("playerdata"),
                    columnsList);
        } catch (Exception ex) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "There is an error while connecting the MySQL database.");
            PlayerdataPlus.disablePlugin();
        }
    }
}
