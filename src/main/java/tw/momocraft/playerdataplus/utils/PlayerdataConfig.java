package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class PlayerdataConfig {
    private int timeoutTime;
    private boolean timeoutWarning;
    private ConfigurationSection cleanConfig;
    private List<String> cleanList = new ArrayList<>();
    private HashMap<String, Long> cleanExpireTimeMap = new HashMap<>();
    private List<String> backupList = new ArrayList<>();
    private boolean cleanAutoEnable;
    private int cleanMaxDataSize;
    private long cleanExpiryDay;
    private boolean cleanLogEnable;
    private List<String> cleanRegionWorlds = new ArrayList<>();
    private List<String> cleanIgnoreRegions = new ArrayList<>();
    private boolean cleanRegionBypassRes;
    private boolean backupEnable;
    private String backupMode;
    private boolean backupToZip;
    private String backupFolderName;
    private String backupCustomPath;

    public PlayerdataConfig() {
        setUp();
    }

    private void setUp() {
        timeoutTime = ConfigHandler.getServerConfig("spigot.yml").getInt("settings.timeout-time");
        timeoutWarning = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Timeout-Warning");
        cleanConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Clean.Control");
        cleanAutoEnable = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Auto-Clean.Enable");
        cleanMaxDataSize = ConfigHandler.getConfig("config.yml").getInt("Clean.Settings.Max-Clean-Per-Data");
        cleanExpiryDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
        backupToZip = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.To-Zip");
        cleanLogEnable = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Log");
        cleanRegionWorlds = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.Regions.Worlds");
        cleanIgnoreRegions = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.Regions.Ignore-Regions");
        cleanRegionBypassRes = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Control.Regions.Residence-Bypass");
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

    public boolean isBackupEnable(String title) {
        return backupList.contains(title);
    }
}

