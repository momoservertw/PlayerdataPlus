package tw.momocraft.playerdataplus.playerdata;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final Map<String, PlayerDataMap> playerMap = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLoginEvent(PlayerLoginEvent e) {
        if (!ConfigHandler.getConfigPath().isPlayerData())
            return;
        Player player = e.getPlayer();
        if (ConfigHandler.getConfigPath().isPlayerDataMsgStarting())
            CorePlusAPI.getMsg().sendLangMsg("Message.playerDataSyncStarted", player);
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

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

}
