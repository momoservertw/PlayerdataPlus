package tw.momocraft.playerdataplus;

import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

public class PlayerdataPlus extends JavaPlugin {
    private static PlayerdataPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        ConfigHandler.registerEvents();
        ServerHandler.sendConsoleMessage("&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        ServerHandler.sendConsoleMessage("&fhas been Disabled.");
    }

    public static PlayerdataPlus getInstance() {
        return instance;
    }
}
