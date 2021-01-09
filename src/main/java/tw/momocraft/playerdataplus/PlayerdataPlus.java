package tw.momocraft.playerdataplus;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.RegisterHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

public class PlayerdataPlus extends JavaPlugin {
    private static PlayerdataPlus instance;

    //static Connection connection; //This is the variable we will use to connect to database

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        RegisterHandler.registerEvents();
        ServerHandler.sendConsoleMessage("&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        ServerHandler.sendConsoleMessage("&fhas been Disabled.");
        if (ConfigHandler.getConfigPath().isMycmd()) {
            ConfigHandler.getMySQLAPI().disabledConnect();
        }

        instance = null;
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static PlayerdataPlus getInstance() {
        return instance;
    }
}