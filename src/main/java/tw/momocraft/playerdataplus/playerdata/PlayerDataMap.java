package tw.momocraft.playerdataplus.playerdata;

import java.util.Map;
import java.util.UUID;

public class PlayerDataMap {

    private UUID uuid;
    private String playerName;
    private Map<String, String> playerData;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Map<String, String> getPlayerData() {
        return playerData;
    }

    public void setPlayerData(Map<String, String> playerData) {
        this.playerData = playerData;
    }
}
