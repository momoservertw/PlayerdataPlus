package tw.momocraft.playerdataplus.features.nick;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NickMap {

    private String playerName;

    private String displayName;

    private UUID playerUUID;

    private String nickName;

    private String nickColor;

    private Player player;

    private OfflinePlayer offlinePlayer;

    private boolean sentBySelf;

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

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickColor() {
        return nickColor;
    }

    public void setNickColor(String nickColor) {
        this.nickColor = nickColor;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public void setOfflinePlayer(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public boolean isSentBySelf() {
        return sentBySelf;
    }

    public void setSentBySelf(boolean sentBySelf) {
        this.sentBySelf = sentBySelf;
    }
}