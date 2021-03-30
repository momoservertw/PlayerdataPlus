package tw.momocraft.playerdataplus.handlers;

import tw.momocraft.playerdataplus.Commands;
import tw.momocraft.playerdataplus.playerstatus.listeners.PlayerChangedWorld;
import tw.momocraft.playerdataplus.playerstatus.listeners.PlayerJoin;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.TabComplete;

public class RegisterHandler {

    public static void registerEvents() {
        PlayerdataPlus.getInstance().getCommand("playerdataplus").setExecutor(new Commands());
        PlayerdataPlus.getInstance().getCommand("playerdataplus").setTabCompleter(new TabComplete());

        if (ConfigHandler.getConfigPath().isPlayerStatus()) {
            if (ConfigHandler.getConfigPath().isPsLogin()) {
                PlayerdataPlus.getInstance().getServer().getPlugin().registerEvents(new PlayerJoin(), PlayerdataPlus.getInstance());
                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginName(), "Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                        new Throwable().getStackTrace()[0]);
            }
            if (ConfigHandler.getConfigPath().isPsWorldChange()) {
                PlayerdataPlus.getInstance().getServer().getPlugin().registerEvents(new PlayerChangedWorld(), PlayerdataPlus.getInstance());
                CorePlusAPI.getLang().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginName(), "Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

}
