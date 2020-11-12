package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.PlayerStatus.PlayerStatusMap;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.utils.locationutils.LocationMap;
import tw.momocraft.playerdataplus.utils.locationutils.LocationUtils;

import java.util.*;


public class ConfigPath {

    //  ============================================== //
    //         General Settings                        //
    //  ============================================== //
    private static LocationUtils locationUtils;

    private int timeoutTime;
    private List<String> cleanPriority;
    private boolean cleanAutoEnable;
    private long cleanAutoDelay;
    private boolean timeoutWarning;

    //  ============================================== //
    //         Clean Settings                          //
    //  ============================================== //
    private boolean backupEnable;
    private String backupMode;
    private boolean backupToZip;
    private String backupFolderName;
    private String backupCustomPath;
    private List<String> backupList = new ArrayList<>();
    private ConfigurationSection cleanConfig;
    private List<String> cleanList = new ArrayList<>();
    private HashMap<String, Long> cleanExpireTimeMap = new HashMap<>();
    private int cleanMaxDataSize;
    private long cleanExpiryDay;
    private boolean cleanLogEnable;
    private List<String> cleanRegionWorlds = new ArrayList<>();
    private List<String> cleanIgnoreRegions = new ArrayList<>();
    private boolean cleanRegionBypassRes;
    private List<String> cleanMycmdIgnore;
    private List<String> cleanMycmdVars;
    private List<String> cleanMycmdPlayers;

    //  ============================================== //
    //         PlayerStatus Settings                   //
    //  ============================================== //
    private boolean playerStatus;
    private Map<String, PlayerStatusMap> playerStatusProp = new HashMap<>();

    private boolean psLogin;
    private boolean psWorldChange;
    private boolean psSchdeule;
    private int psSchdeuleInterval;

    //  ============================================== //
    //         User-Convertor Settings                   //
    //  ============================================== //
    private boolean transferUser;
    private boolean tfPlayerdata;
    private boolean tfStats;
    private boolean tfAdvancements;
    private boolean tfEconomy;
    private boolean tfPlayerPoints;
    private boolean tfLuckPerms;
    private boolean tfAuthMe;
    private boolean tfCMINick;
    private boolean tfResidence;
    private boolean tfNameTagEdit;

    private boolean flyResEnable;

    //  ============================================== //
    //         MyCommand Settings                      //
    //  ============================================== //


    public ConfigPath() {
        setUp();
    }

    private void setUp() {
        locationUtils = new LocationUtils();

        timeoutTime = ConfigHandler.getConfig("spigot.yml").getInt("settings.timeout-time");

        setUpClean();
        setUpPlayerStatus();
        setUpMycmd();
    }

    private void setUpClean() {
        cleanPriority = ConfigHandler.getConfig("config.yml").getStringList("Clean.Settings.Offline-Player.Priority-Order");
        cleanAutoEnable = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Auto-Clean.Enable");
        cleanAutoDelay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Auto-Clean.Delay") * 20;
        timeoutWarning = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Timeout-Warning");
        cleanConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Clean.Control");
        cleanMaxDataSize = ConfigHandler.getConfig("config.yml").getInt("Clean.Settings.Max-Clean-Per-Data");
        cleanExpiryDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
        backupToZip = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.To-Zip");
        cleanLogEnable = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Log");
        cleanRegionWorlds = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.Regions.Worlds");
        cleanIgnoreRegions = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.Regions.Ignore-Regions");
        cleanRegionBypassRes = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Control.Regions.Residence-Bypass");
        cleanMycmdIgnore = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.MyCommand.Ignore-Values");
        cleanMycmdPlayers = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.MyCommand.Playerdatas");
        cleanMycmdVars = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.MyCommand.Variables");
        backupMode = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Mode");
        backupFolderName = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Folder-Name");
        backupCustomPath = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Custom-Path");
        backupEnable = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.Enable");
        for (String title : cleanConfig.getKeys(false)) {
            if (ConfigHandler.getConfig("config.yml").getBoolean("Clean.Control." + title + ".Enable")) {
                cleanList.add(title);
            }
        }
        for (String title : cleanList) {
            cleanExpireTimeMap.put(title, ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days"));
        }
        for (String title : cleanList) {
            if (!Arrays.asList("Logs", "Playerdata", "Advancements", "Stats", "Regions").contains(title)) {
                if (ConfigHandler.getConfig("config.yml").getBoolean("Clean.Control." + title + ".Backup")) {
                    backupList.add(title);
                }
            }
        }
    }

    private void setUpPlayerStatus() {
        playerStatus = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Enable");
        if (!playerStatus) {
            return;
        }
        psLogin = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Check.Login");
        psWorldChange = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Check.World-Change");
        psSchdeule = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Check.Schedule.Enable");
        psSchdeuleInterval = ConfigHandler.getConfig("config.yml").getInt("Player-Status.Settings.Check.Schedule.Interval");

        PlayerStatusMap playerStatusMap;
        ConfigurationSection groupsConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Player-Status");
        if (groupsConfig != null) {
            List<LocationMap> locMaps;
            for (String group : groupsConfig.getKeys(false)) {
                if (ConfigHandler.getConfig("config.yml").getBoolean("Player-Status." + group + ".Enable")) {
                    // Checking the CMI enabled.
                    if (group.equals("God") && !ConfigHandler.getDepends().CMIEnabled()) {
                        continue;
                    }
                    playerStatusMap = new PlayerStatusMap();
                    playerStatusMap.setIgnorePerms(ConfigHandler.getConfig("config.yml").getStringList("Player-Status." + group + ".Ignore.Permissions"));
                    // Adding the location setting.
                    locMaps = locationUtils.getSpeLocMaps("config.yml", "Player-Status." + group + ".Location");
                    if (!locMaps.isEmpty()) {
                        playerStatusMap.setLocMaps(locMaps);
                    }
                    // Adding the special setting for CMI.
                    if (group.equals("Fly") && ConfigHandler.getDepends().CMIEnabled()) {
                        playerStatusMap.setFlyCMIT(ConfigHandler.getConfig("config.yml").getBoolean("Player-Status." + group + ".Ignore.CMI.tfly"));
                        playerStatusMap.setFlyCMIC(ConfigHandler.getConfig("config.yml").getBoolean("Player-Status." + group + ".Ignore.CMI.cfly"));
                        playerStatusMap.setFlyRes(ConfigHandler.getConfig("config.yml").getBoolean("Player-Status." + group + ".Ignore.Residence"));
                    } else if (group.equals("God") && ConfigHandler.getDepends().CMIEnabled()) {
                        playerStatusMap.setGodCMIT(ConfigHandler.getConfig("config.yml").getBoolean("Player-Status." + group + ".Ignore.CMI.tgod"));
                    }
                    playerStatusProp.put(group, playerStatusMap);
                }
            }
        }
    }

    private void setUpMycmd() {

    }

    public static LocationUtils getLocationUtils() {
        return locationUtils;
    }

    public Map<String, PlayerStatusMap> getPlayerStatusProp() {
        return playerStatusProp;
    }

    public boolean isPlayerStatus() {
        return playerStatus;
    }

    public boolean isPsLogin() {
        return psLogin;
    }

    public boolean isPsWorldChange() {
        return psWorldChange;
    }

    public boolean isPsSchdeule() {
        return psSchdeule;
    }

    public int getPsSchdeuleInterval() {
        return psSchdeuleInterval;
    }


    public List<String> getCleanPriority() {
        return cleanPriority;
    }

    public int getCleanMaxDataSize() {
        return cleanMaxDataSize;
    }

    public long getCleanExpiryDay() {
        return cleanExpiryDay;
    }

    public ConfigurationSection getCleanConfig() {
        return cleanConfig;
    }

    public int getTimeoutTime() {
        return timeoutTime;
    }

    public boolean isBackupEnable(String title) {
        return backupList.contains(title);
    }

    public long getCleanAutoDelay() {
        return cleanAutoDelay;
    }

    public boolean isTimeoutWarning() {
        return timeoutWarning;
    }

    public List<String> getCleanList() {
        return cleanList;
    }

    public boolean isBackupToZip() {
        return backupToZip;
    }

    public boolean isCleanLogEnable() {
        return cleanLogEnable;
    }

    public List<String> getCleanRegionWorlds() {
        return cleanRegionWorlds;
    }

    public List<String> getCleanIgnoreRegions() {
        return cleanIgnoreRegions;
    }

    public boolean isCleanRegionBypassRes() {
        return cleanRegionBypassRes;
    }

    public String getBackupCustomPath() {
        return backupCustomPath;
    }

    public String getBackupFolderName() {
        return backupFolderName;
    }

    public String getBackupMode() {
        return backupMode;
    }

    public boolean isBackupEnable() {
        return backupEnable;
    }

    public boolean isCleanAutoEnable() {
        return cleanAutoEnable;
    }

    public HashMap<String, Long> getCleanExpireTimeMap() {
        return cleanExpireTimeMap;
    }

    public List<String> getBackupList() {
        return backupList;
    }

    public List<String> getCleanMycmdIgnore() {
        return cleanMycmdIgnore;
    }

    public List<String> getCleanMycmdPlayers() {
        return cleanMycmdPlayers;
    }

    public List<String> getCleanMycmdVars() {
        return cleanMycmdVars;
    }
}



