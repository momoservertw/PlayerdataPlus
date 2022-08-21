package tw.momocraft.playerdataplus.features.playerdata;

import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.handlers.UtilsHandler;
import tw.momocraft.coreplus.utils.file.maps.MySQLMap;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.api.PlayerDataInterface;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.sql.ResultSet;
import java.util.*;

public class PlayerData implements PlayerDataInterface {

    private final Map<String, PlayerDataMap> playerMap = new HashMap<>();

    public void setupMySQL() {
        // Load MySQL settings.
        MySQLMap mySQLMap = new MySQLMap();
        Map<String, String> tableMap = new HashMap<>();
        mySQLMap.setGroupName("playerdataplus");
        mySQLMap.setDatabase(ConfigHandler.getConfigPath().getMysqlDatabase());
        mySQLMap.setHostName(ConfigHandler.getConfigPath().getMysqlPort());
        mySQLMap.setPort(ConfigHandler.getConfigPath().getMysqlPort());
        mySQLMap.setUsername(ConfigHandler.getConfigPath().getMysqlUsername());
        mySQLMap.setPassword(ConfigHandler.getConfigPath().getMysqlPassword());
        tableMap.put("players", ConfigHandler.getConfigPath().getMysqlPrefix() + "players");
        tableMap.put("playerdata", ConfigHandler.getConfigPath().getMysqlPrefix() + "playerdata");
        mySQLMap.setTables(tableMap);
        // Connect the MySQL.
        CorePlusAPI.getFile().getMySQL().connect(ConfigHandler.getPluginName(), mySQLMap);
        // Table: players
        List<String> columnsList = new ArrayList<>();
        columnsList.add("uuid");
        columnsList.add("name");
        columnsList.add("display_name");
        columnsList.add("nick");
        columnsList.add("last_login");
        // Create the table if not exist.
        CorePlusAPI.getFile().getMySQL().createTables(ConfigHandler.getPluginName(),
                mySQLMap.getDatabase(),
                tableMap.get("players"),
                columnsList);
        // Table: playerdata
        columnsList = new ArrayList<>();
        columnsList.add("uuid");
        columnsList.add("variable");
        columnsList.add("value");
        // Create the table if not exist.
        CorePlusAPI.getFile().getMySQL().createTables(ConfigHandler.getPluginName(),
                mySQLMap.getDatabase(),
                tableMap.get("playerdata"),
                columnsList);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        if (!ConfigHandler.getConfigPath().isPlayerData())
            return;
        Player player = e.getPlayer();
        if (ConfigHandler.getConfigPath().isPlayerDataMsgStarting())
            CorePlusAPI.getMsg().sendLangMsg("Message.playerDataSyncStarted", player);
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        String displayName = player.getDisplayName();

        new BukkitRunnable() {
            int i = 1;
            PlayerDataMap playerDataMap;

            @Override
            public void run() {
                if (i > 5) {
                    if (ConfigHandler.getConfigPath().isPlayerDataMsgFailed())
                        CorePlusAPI.getMsg().sendLangMsg("Message.playerDataSyncFailed", player);
                    cancel();
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "PlayerData", "Load Player", "sync", "cancel", playerName,
                            new Throwable().getStackTrace()[0]);
                } else {
                    i++;
                    playerDataMap = new PlayerDataMap();
                    playerDataMap.setPlayerName(playerName);
                    playerDataMap.setUuid(uuid);
                    playerDataMap.setDisplayName(displayName);
                    playerDataMap.setLastLogin(player.getLastLogin());
                    Map<String, String> map = CorePlusAPI.getFile().getMySQL().getValueMap(ConfigHandler.getPluginName()
                            , "CorePlus", "playerdata", "uuid", uuid.toString());
                    if (Boolean.getBoolean(map.get("synced"))) {
                        playerDataMap.setPlayerData(map);
                        playerMap.put(playerName, playerDataMap);

                        if (ConfigHandler.getConfigPath().isPlayerDataMsgFailed())
                            CorePlusAPI.getMsg().sendLangMsg("Message.playerDataSyncSucceed", player);
                        cancel();
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "PlayerData", "Load Player", "sync", "cancel", playerName,
                                new Throwable().getStackTrace()[0]);
                    }
                }
            }
        }.runTaskTimer(PlayerdataPlus.getInstance(), 0, 20);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        if (!ConfigHandler.getConfigPath().isPlayerData())
            return;
        Player player = e.getPlayer();
        String playerName = player.getName();
        if (ConfigHandler.getConfigPath().isPlayerDataMsgFailed())
            CorePlusAPI.getMsg().sendLangMsg("Message.playerDataSyncWaiting", player);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "PlayerData", "Load Player", "sync", "cancel", playerName,
                new Throwable().getStackTrace()[0]);
        e.setTo(e.getFrom());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (!ConfigHandler.getConfigPath().isPlayerData())
            return;
        Player player = e.getPlayer();
        unloadPlayer(player);
    }

    private void unloadPlayer(Player player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

        Map<String, String> map = CorePlusAPI.getFile().getMySQL().getValueMap(ConfigHandler.getPluginName()
                , "CorePlus", "PLAYERDATA", "uuid", uuid.toString());
        map.put("synced", "true");
        for (Map.Entry<String, String> entry : map.entrySet())
            CorePlusAPI.getFile().getMySQL().setValueWhere(ConfigHandler.getPluginName(),
                    "CorePlus", "PLAYERDATA", "uuid",
                    uuid.toString(), entry.getKey(), entry.getValue());
        playerMap.remove(playerName);
    }

    /*
     * 1. 當玩家加入伺服器
     * if (Synced=true)
     * 載入MySQL的資料到快取中，設置Synced=true
     * else
     *   if (player.isOnlineInBungee)
     *   Waiting
     *   else
     *   # 異常狀況，當作玩家在線時伺服器崩潰。
     *
     * 2. 當玩家登出後，儲存該玩家的資料、設置Synced=true，之後從快取中移除該玩家。
     *
     * 3. 每隔3分鐘自動存檔玩家資料
     */

    @Override
    public String getPlayerData(String playerName, String variable) {
        try {
            return playerMap.get(playerName).getPlayerData().get(variable);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName(String playerName) {
        try {
            return playerMap.get(playerName).getDisplayName();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getNickName(String playerName) {
        try {
            return playerMap.get(playerName).getNickName();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public long getLastLogin(String playerName) {
        try {
            return playerMap.get(playerName).getLastLogin();
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public String getUuid(String playerName) {
        try {
            return playerMap.get(playerName).getUuid().toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /*
    public void syncData() {
        if (!CorePlusAPI.getFile().getMySQL().)
            return;
        importPlayerList();
        importPlayerLastLogin();
    }
     */

    private void importPlayerLastLogin() {
        List<String> uuidList = UtilsHandler.getFile().getMySQL().getValueList(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(),
                "coreplus", "player", "uuid");
        if (uuidList == null)
            return;
        long dataTime;
        long checkTime;
        OfflinePlayer offlinePlayer;
        for (String uuid : uuidList) {
            // Getting the CorePlus login time.
            dataTime = Long.parseLong(UtilsHandler.getFile().getMySQL().getValueWhere(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(),
                    "coreplus", "players", "uuid", uuid, "last_login"));
            // Checking the Server login time.
            offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if (offlinePlayer.getName() == null || offlinePlayer.getName().equals("CMI-Fake-Operator"))
                continue;
            checkTime = offlinePlayer.getLastLogin();
            if (checkTime == 0 && dataTime > checkTime) {
                UtilsHandler.getFile().getMySQL().setValueWhere(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(),
                        "coreplus", "players", "uuid", uuid, "last_login", String.valueOf(checkTime));
                dataTime = checkTime;
            }
            // Checking the AuthMe login time.
            if (UtilsHandler.getDepend().AuthMeEnabled()) {
                checkTime = AuthMeApi.getInstance().getLastLoginTime(offlinePlayer.getName()).toEpochMilli();
                if (checkTime == 0 && dataTime > checkTime) {
                    UtilsHandler.getFile().getMySQL().setValueWhere(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(),
                            "coreplus", "players", "uuid", uuid, "last_login", String.valueOf(checkTime));
                }
            }
        }
    }

    private void importPlayerList() {
        if (UtilsHandler.getDepend().LuckPermsEnabled()) {
            MySQLMap mySQLMap = tw.momocraft.coreplus.handlers.ConfigHandler.getConfigPath().getMySQLProp().get("luckperms");
            if (mySQLMap != null) {
                ResultSet resultSet = UtilsHandler.getFile().getMySQL().getResultSet(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(),
                        mySQLMap.getDatabase(), mySQLMap.getTables().get("Players"));
                try {
                    while (resultSet.next()) {
                        resultSet.getString("uuid");
                        resultSet.getString("username");
                    }
                } catch (Exception ex) {
                    UtilsHandler.getMsg().sendErrorMsg(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(), "An error occurred while importing the player data.");
                    UtilsHandler.getMsg().sendErrorMsg(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(), "Please check the settings \"MySQL.LuckPerms\" in data.yml.");
                }
            }
        }
        // Getting the Server player list.
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName() == null || offlinePlayer.getName().equals("CMI-Fake-Operator"))
                continue;
            UtilsHandler.getFile().getMySQL().setValueWhere(tw.momocraft.coreplus.handlers.ConfigHandler.getPluginName(),
                    "playerdataplus", "players", "uuid", offlinePlayer.getUniqueId().toString(),
                    "username", offlinePlayer.getName());
        }
    }
}
