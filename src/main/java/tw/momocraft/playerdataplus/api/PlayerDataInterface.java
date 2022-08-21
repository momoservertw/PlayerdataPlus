package tw.momocraft.playerdataplus.api;

public interface PlayerDataInterface {

    /**
     * Executing command for a player when he is login.
     *
     * @param playerName the target player name.
     * @param variable   the variable name.
     */
    String getPlayerData(String playerName, String variable);

    String getDisplayName(String playerName);

    String getNickName(String playerName);

    long getLastLogin(String playerName);

    String getUuid(String playerName);
}