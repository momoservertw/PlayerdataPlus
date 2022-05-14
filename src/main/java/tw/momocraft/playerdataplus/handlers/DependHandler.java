package tw.momocraft.playerdataplus.handlers;

import org.bukkit.Bukkit;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.Commands;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.TabComplete;
import tw.momocraft.playerdataplus.utils.nick.Nick;

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

        if (ConfigHandler.getConfigPath().isNick()) {
            if (ConfigHandler.getConfigPath().isNickAutoUpdate()) {
                PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(
                        new Nick(), PlayerdataPlus.getInstance());
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Register-Event", "Nick", "PlayerJoinEvent", "continue",
                        new Throwable().getStackTrace()[0]);
            }
        }
        /*
        if (ConfigHandler.getConfigPath().isPlayerStatus()) {
            if (ConfigHandler.getConfigPath().isPsCheckLogin()) {
                PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(
                        new PlayerJoin(), PlayerdataPlus.getInstance());
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                        new Throwable().getStackTrace()[0]);
            }
            if (ConfigHandler.getConfigPath().isPsCheckWorldChange()) {
                PlayerdataPlus.getInstance().getServer().getPluginManager().registerEvents(
                        new PlayerChangedWorld(), PlayerdataPlus.getInstance());
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Register-Event", "Player-Status", "FlyPlayerJoin", "continue",
                        new Throwable().getStackTrace()[0]);
            }
        }
         */
    }

    private boolean SkinsRestorer = false;
    private boolean DiscordSRV = false;
    private boolean MyPet = false;
    private boolean Essentials = false;
    private boolean MyCommand = false;

    private void setupHooks() {
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.SkinsRestorer"))
            SkinsRestorer = Bukkit.getServer().getPluginManager().getPlugin("SkinsRestorer") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.DiscordSRV"))
            DiscordSRV = Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.MyPet"))
            MyPet = Bukkit.getServer().getPluginManager().getPlugin("MyPet") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.Essentials"))
            Essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null;
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.MyCommand"))
            MyCommand = Bukkit.getServer().getPluginManager().getPlugin("MyCommand") != null;
    }

    public boolean SkinsRestorerEnabled() {
        return this.SkinsRestorer;
    }

    public boolean DiscordSRVEnabled() {
        return this.DiscordSRV;
    }

    public boolean MyPetEnabled() {
        return this.MyPet;
    }

    public boolean EssentialsEnabled() {
        return this.Essentials;
    }

    public boolean MyCommandEnabled() {
        return this.MyCommand;
    }
}
