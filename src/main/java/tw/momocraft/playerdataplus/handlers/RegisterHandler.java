package tw.momocraft.playerdataplus.handlers;

import tw.momocraft.playerdataplus.Commands;
import tw.momocraft.playerdataplus.PlayerStatus.listeners.PlayerChangedWorld;
import tw.momocraft.playerdataplus.PlayerStatus.listeners.PlayerJoin;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.utils.TabComplete;

public class RegisterHandler {

    public static void registerEvents() {
        PlayerdataPlus.getInstance().getCommand("playerdataplus").setExecutor(new Commands());
        PlayerdataPlus.getInstance().getCommand("playerdataplus").setTabCompleter(new TabComplete());

        if (ConfigHandler.getConfigPath().isPlayerStatus()) {
            if (ConfigHandler.getConfigPath().isPsLogin()) {
                PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(new PlayerJoin(), PlayerdataPlus.getInstance());
                ServerHandler.sendFeatureMessage("Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                        new Throwable().getStackTrace()[0]);
            }
            if (ConfigHandler.getConfigPath().isPsWorldChange()) {
                PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(new PlayerChangedWorld(), PlayerdataPlus.getInstance());
                ServerHandler.sendFeatureMessage("Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

}
