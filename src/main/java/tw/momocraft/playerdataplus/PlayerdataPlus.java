package tw.momocraft.playerdataplus;

import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.DependHandler;

public class PlayerdataPlus extends JavaPlugin {

    private static PlayerdataPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(), "&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(), "&fhas been Enabled.");
    }

    public static PlayerdataPlus getInstance() {
        return instance;
    }


}
