package tw.momocraft.playerdataplus;

import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;
import tw.momocraft.playerdataplus.utils.MySQLAPI;

import java.sql.Connection;

public class PlayerdataPlus extends JavaPlugin {
    private static PlayerdataPlus instance;

    MySQLAPI mySQLAPI;

    static Connection connection; //This is the variable we will use to connect to database

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        ConfigHandler.registerEvents();
        mySQLAPI = new MySQLAPI();
        ServerHandler.sendConsoleMessage("&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        mySQLAPI.disabledConnect();
        ServerHandler.sendConsoleMessage("&fhas been Disabled.");
    }

    public static PlayerdataPlus getInstance() {
        return instance;
    }


}
