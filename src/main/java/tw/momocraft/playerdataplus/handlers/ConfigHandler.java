package tw.momocraft.playerdataplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.playerdataplus.Commands;
import tw.momocraft.playerdataplus.PlayerStatus.PlayerStatusControl;
import tw.momocraft.playerdataplus.PlayerStatus.listeners.PlayerChangedWorld;
import tw.momocraft.playerdataplus.PlayerStatus.listeners.PlayerJoin;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.utils.*;
import tw.momocraft.playerdataplus.utils.clean.PurgeHandler;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.bukkit.Bukkit.getServer;

public class ConfigHandler {

    private static YamlConfiguration configYAML;
    private static YamlConfiguration spigotYAML;
    private static YamlConfiguration mycmdPlayerYAML;
    private static YamlConfiguration mycmdVarYAML;
    private static DependAPI depends;
    private static ConfigPath configPath;
    private static UpdateHandler updater;
    private static Logger logger;
    private static ColorCorrespond colors;
    private static MySQLAPI mySQLAPI;

    public static void generateData(boolean reload) {
        genConfigFile("config.yml");
        setDepends(new DependAPI());
        sendUtilityDepends();
        setConfigPath(new ConfigPath());
        setUpdater(new UpdateHandler());
        setLogger(new Logger());
        setColorConvert(new ColorCorrespond());
        mySQLAPI = new MySQLAPI();

        if (!reload && getConfigPath().isCleanAutoEnable()) {
            if (ConfigHandler.getConfigPath().isTimeoutWarning() && ConfigHandler.getConfigPath().getTimeoutTime() < 180) {
                ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ServerHandler.sendConsoleMessage("&6Starting to clean the expired data...");
                        PurgeHandler purgeHandler = new PurgeHandler();
                        purgeHandler.start(Bukkit.getConsoleSender());
                    }
                }.runTaskLater(PlayerdataPlus.getInstance(), getConfigPath().getCleanAutoDelay());
            }
        }
        if (ConfigHandler.getConfigPath().isPlayerStatus()) {
            if (ConfigHandler.getConfigPath().isPsSchdeule()) {
                PlayerStatusControl.startSchedule();
            }
        }
    }

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

    public static FileConfiguration getConfig(String fileName) {
        File filePath = PlayerdataPlus.getInstance().getDataFolder();
        File file;
        Plugin mycmd = Bukkit.getPluginManager().getPlugin("MyCommand");
        switch (fileName) {
            case "config.yml":
                filePath = Bukkit.getWorldContainer();
                if (configYAML == null) {
                    getConfigData(filePath, fileName);
                }
                break;
            case "spigot.yml":
                filePath = Bukkit.getServer().getWorldContainer();
                if (spigotYAML == null) {
                    getConfigData(filePath, fileName);
                }
                break;
            case "playerdata.yml":
                if (mycmd != null) {
                    try {
                        filePath = Bukkit.getPluginManager().getPlugin("MyCommand").getDataFolder();
                    } catch (Exception ignored) {
                        ServerHandler.sendConsoleMessage("&4The file \"" + fileName + "\" is out of date, generating a new one!");
                    }
                    if (mycmdPlayerYAML == null) {
                        getConfigData(filePath, fileName);
                    }
                }
                break;
            case "othersdb.yml":
                if (mycmd != null) {
                    try {
                        filePath = Bukkit.getPluginManager().getPlugin("MyCommand").getDataFolder();
                    } catch (Exception ignored) {
                        ServerHandler.sendConsoleMessage("&4The file \"" + fileName + "\" is out of date, generating a new one!");
                    }
                    if (mycmdVarYAML == null) {
                        getConfigData(filePath, fileName);
                    }
                }
                break;
            default:
                break;
        }
        file = new File(filePath, fileName);
        return getPath(fileName, file, false);
    }

    private static FileConfiguration getConfigData(File filePath, String fileName) {
        File file = new File(filePath, fileName);
        if (!(file).exists()) {
            try {
                PlayerdataPlus.getInstance().saveResource(fileName, false);
            } catch (Exception e) {
                PlayerdataPlus.getInstance().getLogger().warning("Cannot save " + fileName + " to disk!");
                return null;
            }
        }
        return getPath(fileName, file, true);
    }

    private static YamlConfiguration getPath(String fileName, File filePath, boolean saveData) {
        if (fileName.contains("config.yml")) {
            if (saveData) {
                configYAML = YamlConfiguration.loadConfiguration(filePath);
            }
            return configYAML;
        } else if (fileName.contains("spigot.yml")) {
            if (saveData) {
                spigotYAML = YamlConfiguration.loadConfiguration(filePath);
            }
            return spigotYAML;
        } else if (fileName.contains("playerdata.yml")) {
            if (saveData) {
                mycmdPlayerYAML = YamlConfiguration.loadConfiguration(filePath);
            }
            return mycmdPlayerYAML;
        } else if (fileName.contains("othersdb.yml")) {
            if (saveData) {
                mycmdVarYAML = YamlConfiguration.loadConfiguration(filePath);
            }
            return mycmdPlayerYAML;
        }
        return null;
    }

    private static void genConfigFile(String fileName) {
        String[] fileNameSlit = fileName.split("\\.(?=[^\\.]+$)");
        int configVersion = 0;
        File filePath = PlayerdataPlus.getInstance().getDataFolder();
        switch (fileName) {
            case "config.yml":
                configVersion = 4;
                break;
        }
        getConfigData(filePath, fileName);
        File File = new File(filePath, fileName);
        if (File.exists() && getConfig(fileName).getInt("Config-Version") != configVersion) {
            if (PlayerdataPlus.getInstance().getResource(fileName) != null) {
                LocalDateTime currentDate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                String currentTime = currentDate.format(formatter);
                String newGen = fileNameSlit[0] + " " + currentTime + "." + fileNameSlit[0];
                File newFile = new File(filePath, newGen);
                if (!newFile.exists()) {
                    File.renameTo(newFile);
                    File configFile = new File(filePath, fileName);
                    configFile.delete();
                    getConfigData(filePath, fileName);
                    ServerHandler.sendConsoleMessage("&4The file \"" + fileName + "\" is out of date, generating a new one!");
                }
            }
        }
        getConfig(fileName).options().copyDefaults(false);
    }

    private static void sendUtilityDepends() {
        ServerHandler.sendConsoleMessage("&fHooked: "
                + (getDepends().getVault().vaultEnabled() ? "Vault, " : "")
                + (getDepends().CMIEnabled() ? "CMI, " : "")
                + (getDepends().ResidenceEnabled() ? "Residence, " : "")
                + (getDepends().PlaceHolderAPIEnabled() ? "PlaceHolderAPI, " : "")
                + (getDepends().MySQLPlayerDataBridgeEnabled() ? "MySQLPlayerDataBridge, " : "")
                + (getDepends().SkinsRestorerEnabled() ? "SkinsRestorer, " : "")
                + (getDepends().DiscordSRVEnabled() ? "DiscordSRV, " : "")
                + (getDepends().LuckPermsEnabled() ? "LuckPerms, " : "")
                + (getDepends().MyPetEnabled() ? "MyPet, " : "")
                + (getDepends().AuthMeEnabled() ? "Authme, " : "")
                + (getDepends().EssentialsEnabled() ? "Essentials," : "")
                + (getDepends().MultiverseCoreEnabled() ? "MultiverseCore," : "")
                + (getDepends().PlayerPointsEnabled() ? "PlayerPoints," : "")
                + (getDepends().MyCommandEnabled() ? "MyCommand," : "")
        );
    }

    public static DependAPI getDepends() {
        return depends;
    }

    private static void setDepends(DependAPI depend) {
        depends = depend;
    }

    private static void setUpdater(UpdateHandler update) {
        updater = update;
    }

    static boolean getDebugging() {
        return ConfigHandler.getConfig("config.yml").getBoolean("Debugging");
    }

    public static UpdateHandler getUpdater() {
        return updater;
    }

    private static void setLogger(Logger log) {
        logger = log;
    }

    public static Logger getLogger() {
        return logger;
    }

    private static void setConfigPath(ConfigPath configPath) {
        ConfigHandler.configPath = configPath;
    }

    public static ConfigPath getConfigPath() {
        return configPath;
    }

    private static void setColorConvert(ColorCorrespond colorCorrespond) {
        colors = colorCorrespond;
    }

    public static ColorCorrespond getColors() {
        return colors;
    }

    public static MySQLAPI getMySQLAPI() {
        return mySQLAPI;
    }

    /**
     * Converts a serialized location to a Location. Returns null if string is empty
     *
     * @param s - serialized location in format "world:x:y:z"
     * @return Location
     */
    public static Location getLocationString(final String s) {
        if (s == null || s.trim().equals("")) {
            return null;
        }
        final String[] parts = s.split(":");
        if (parts.length == 4) {
            final World w = getServer().getWorld(parts[0]);
            final int x = Integer.parseInt(parts[1]);
            final int y = Integer.parseInt(parts[2]);
            final int z = Integer.parseInt(parts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }

    public static boolean isEnable(String path, Boolean empty) {
        String enable = ConfigHandler.getConfig("config.yml").getString(path);
        if (enable == null) {
            return empty;
        }
        return enable.equals("true");
    }

    public static boolean isExist(String path) {
        String exist = ConfigHandler.getConfig("config.yml").getString(path);
        return exist != null;
    }
}