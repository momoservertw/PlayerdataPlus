package tw.momocraft.playerdataplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

public class PlayerdataPlus extends JavaPlugin {

    private static PlayerdataPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginPrefix(), "&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginPrefix(), "&fhas been Enabled.");
    }

    public static PlayerdataPlus getInstance() {
        return instance;
    }

    public static void disablePlugin() {
        CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginPrefix(),
                "&fStarting to disable the plugin...");
        Bukkit.getServer().getPluginManager().disablePlugin(instance);
    }
}
