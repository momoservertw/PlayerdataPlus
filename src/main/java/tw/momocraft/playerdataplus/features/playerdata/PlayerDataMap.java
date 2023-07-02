package tw.momocraft.playerdataplus.features.playerdata;

import java.util.Map;
import java.util.UUID;

public class PlayerDataMap {

    private UUID uuid;

    private String playerName;

    private String displayName;

    private String nickName;

    private long lastLogin;

    private double money;

    private Map<String, String> homes;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Map<String, String> getPlayerData() {
        return playerData;
    }

    public void setPlayerData(Map<String, String> playerData) {
        this.playerData = playerData;
    }
}
