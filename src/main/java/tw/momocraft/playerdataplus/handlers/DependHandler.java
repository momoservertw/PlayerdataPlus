package tw.momocraft.playerdataplus.handlers;

import org.bukkit.Bukkit;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.Commands;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.TabComplete;
import tw.momocraft.playerdataplus.features.nick.Nick;
import tw.momocraft.playerdataplus.features.playerstatus.PlayerStatusControl;
import tw.momocraft.playerdataplus.features.playerstatus.listeners.PlayerChangedWorld;
import tw.momocraft.playerdataplus.features.playerstatus.listeners.PlayerJoin;

public class DependHandler {

    public void setup(boolean reload) {
        if (!reload) {
            registerEvents();
            checkUpdate();
        }
        setupHooks();
    }

    public void checkUpdate() {
        if (!ConfigHandler.isCheckUpdates())
            return;
        CorePlusAPI.getUpdate().check(ConfigHandler.getPluginName(),
                ConfigHandler.getPluginPrefix(), Bukkit.getConsoleSender(),
                PlayerdataPlus.getInstance().getDescription().getName(),
                PlayerdataPlus.getInstance().getDescription().getVersion(), true);
    }

    public static void registerEvents() {
        PlayerdataPlus.getInstance().getCommand("playerdataplus").setExecutor(new Commands());
        PlayerdataPlus.getInstance().getCommand("playerdataplus").setTabCompleter(new TabComplete());

        // Nick
        PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(
                new Nick(), PlayerdataPlus.getInstance());
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "Register-Event", "Nick", "PlayerJoinEvent", "continue",
                new Throwable().getStackTrace()[0]);

        // Fly
        PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(
                new PlayerJoin(), PlayerdataPlus.getInstance());
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                new Throwable().getStackTrace()[0]);
        PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(
                new PlayerChangedWorld(), PlayerdataPlus.getInstance());
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                "Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                new Throwable().getStackTrace()[0]);
    }

    private boolean AuthMe = false;
    private boolean CMI = false;
    private boolean DiscordSRV = false;
    private boolean FeatureBoard = false;
    private boolean MyPet = false;
    private boolean MySQLPlayerDataBridge = false;
    private boolean PlayerPoints = false;
    private boolean Residence = false;
    private boolean SkinsRestorer = false;

    private void setupHooks() {
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.AuthMe"))
            AuthMe = Bukkit.getServer().getPluginManager().getPlugin("AuthMe") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.CMI"))
            CMI = Bukkit.getServer().getPluginManager().getPlugin("CMI") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.FeatureBoard"))
            FeatureBoard = Bukkit.getServer().getPluginManager().getPlugin("FeatureBoard") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.DiscordSRV"))
            DiscordSRV = Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.MyPet"))
            MyPet = Bukkit.getServer().getPluginManager().getPlugin("MyPet") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.MySQLPlayerDataBridge"))
            MySQLPlayerDataBridge = Bukkit.getServer().getPluginManager().getPlugin("MySQLPlayerDataBridge") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.PlayerPoints"))
            PlayerPoints = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.Residence"))
            Residence = Bukkit.getServer().getPluginManager().getPlugin("Residence") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Features.Hook.SkinsRestorer"))
            SkinsRestorer = Bukkit.getServer().getPluginManager().getPlugin("SkinsRestorer") != null;
    }

    public boolean AuthMeEnabled() {
        return this.AuthMe;
    }

    public boolean CMIEnabled() {
        return this.CMI;
    }

    public boolean FeatureBoardEnabled() {
        return this.FeatureBoard;
    }

    public boolean DiscordSRVEnabled() {
        return this.DiscordSRV;
    }

    public boolean MyPetEnabled() {
        return this.MyPet;
    }

    public boolean MySQLPlayerDataBridgeEnabled() {
        return this.MySQLPlayerDataBridge;
    }

    public boolean PlayerPointsEnabled() {
        return this.PlayerPoints;
    }

    public boolean ResidenceEnabled() {
        return this.Residence;
    }

    public boolean SkinsRestorerEnabled() {
        return this.SkinsRestorer;
    }
}
