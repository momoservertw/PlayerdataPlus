package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ConfigPath {
    private int timeoutTime;
    private boolean cleanAutoEnable;
    private long cleanAutoDelay;
    private boolean timeoutWarning;
    private ConfigurationSection cleanConfig;
    private List<String> cleanList = new ArrayList<>();
    private HashMap<String, Long> cleanExpireTimeMap = new HashMap<>();
    private List<String> backupList = new ArrayList<>();
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

    private boolean psFlyEnable;
    private List<String> psFlyPerms;
    private boolean psFlyCMIT;
    private boolean psFlyCMIC;
    private boolean psFlyRes;
    private boolean psFlySchedule;
    private int psFlyInterval;
    private boolean psFlyLogin;
    private boolean psFlyLeave;
    private boolean psFlyWorld;

    private boolean psGodEnable;
    private List<String> psGodPerms;
    private boolean psGodCMIT;
    private boolean psGodSchedule;
    private int psGodInterval;
    private boolean psGodLogin;
    private boolean psGodLeave;
    private boolean psGodWorld;

    private boolean psOpEnable;
    private List<String> psOpPerms;
    private boolean psOpSchedule;
    private int psOpInterval;
    private boolean psOpLogin;
    private boolean psOpLeave;
    private boolean psOpWorld;

    private boolean psGmEnable;
    private boolean psGm0Enable;
    private boolean psGm1Enable;
    private boolean psGm2Enable;
    private boolean psGm3Enable;
    private List<String> psGm0Perms;
    private List<String> psGm1Perms;
    private List<String> psGm2Perms;
    private List<String> psGm3Perms;
    private String psGm0Default;
    private String psGm1Default;
    private String psGm2Default;
    private String psGm3Default;
    private boolean psGmSchedule;
    private int psGmInterval;
    private boolean psGmLogin;
    private boolean psGmLeave;
    private boolean psGmWorld;

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

    public ConfigPath() {
        setUp();
    }

    private void setUp() {
        timeoutTime = ConfigHandler.getServerConfig("spigot.yml").getInt("settings.timeout-time");
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

        psFlyEnable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Enable");
        psFlyPerms = ConfigHandler.getConfig("config.yml").getStringList("Player-Status.Fly.Ignore.Permissions");
        if (ConfigHandler.getDepends().CMIEnabled()) {
            psFlyCMIT = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Ignore.CMI.tfly");
            psFlyCMIC = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Ignore.CMI.cfly");
        } else {
            psFlyCMIT = false;
            psFlyCMIC = false;
        }
        if (ConfigHandler.getDepends().ResidenceEnabled()) {
            psFlyRes = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Ignore.Residence");
        } else {
            psFlyRes = false;
        }
        psFlySchedule = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Check.Schedule.Enable");
        psFlyInterval = ConfigHandler.getConfig("config.yml").getInt("Player-Status.Fly.Check.Schedule.Interval");
        psFlyLogin = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Check.Login");
        psFlyLeave = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Check.Leave");
        psFlyWorld = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Check.World-Change");

        psGodEnable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.God.Enable");
        psGodPerms = ConfigHandler.getConfig("config.yml").getStringList("Player-Status.God.Ignore.Permissions");
        if (ConfigHandler.getDepends().CMIEnabled()) {
            psGodCMIT = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.God.Ignore.CMI.tgod");
        } else {
            psGodCMIT = false;
        }
        psGodSchedule = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.God.Check.Schedule.Enable");
        psGodInterval = ConfigHandler.getConfig("config.yml").getInt("Player-Status.God.Check.Schedule.Interval");
        psGodLogin = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.God.Check.Login");
        psGodLeave = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.God.Check.Leave");
        psGodWorld = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.God.Check.World-Change");

        psOpEnable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Enable");
        psOpPerms = ConfigHandler.getConfig("config.yml").getStringList("Player-Status.Op.Ignore.Permissions");
        psOpSchedule = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.Schedule.Enable");
        psOpInterval = ConfigHandler.getConfig("config.yml").getInt("Player-Status.Op.Check.Schedule.Interval");
        psOpLogin = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.Login");
        psOpLeave = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.Leave");
        psOpWorld = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.World-Change");

        psGmEnable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Gamemode.Enable");
        psGm0Enable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Ignore.Survival.Enable");
        psGm0Perms = ConfigHandler.getConfig("config.yml").getStringList("Player-Status.Ignore.Survival.Permissions");
        psGm0Default = ConfigHandler.getConfig("config.yml").getString("Player-Status.Ignore.Survival.Default");
        psGm1Enable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Ignore.Creative.Enable");
        psGm1Perms = ConfigHandler.getConfig("config.yml").getStringList("Player-Status.Ignore.Creative.Permissions");
        psGm1Default = ConfigHandler.getConfig("config.yml").getString("Player-Status.Ignore.Creative.Default");
        psGm2Enable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Ignore.Adventure.Enable");
        psGm2Perms = ConfigHandler.getConfig("config.yml").getStringList("Player-Status.Ignore.Adventure.Permissions");
        psGm2Default = ConfigHandler.getConfig("config.yml").getString("Player-Status.Ignore.Adventure.Default");
        psGm3Enable = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Ignore.Spectator.Enable");
        psGm3Perms = ConfigHandler.getConfig("config.yml").getStringList("Player-Status.Ignore.Spectator.Permissions");
        psGm3Default = ConfigHandler.getConfig("config.yml").getString("Player-Status.Ignore.Spectator.Default");

        psGmSchedule = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.Schedule.Enable");
        psGmInterval = ConfigHandler.getConfig("config.yml").getInt("Player-Status.Op.Check.Schedule.Interval");
        psGmLogin = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.Login");
        psGmLeave = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.Leave");
        psGmWorld = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Op.Check.World-Change");
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


    public boolean isPsFlyEnable() {
        return psFlyEnable;
    }

    public boolean isPsFlyRes() {
        return psFlyRes;
    }

    public boolean isPsFlyCMIT() {
        return psFlyCMIT;
    }

    public boolean isPsFlyCMIC() {
        return psFlyCMIC;
    }

    public List<String> getPsFlyPerms() {
        return psFlyPerms;
    }

    public boolean isPsFlySchedule() {
        return psFlySchedule;
    }

    public int getPsFlyInterval() {
        return psFlyInterval;
    }

    public boolean isPsFlyLogin() {
        return psFlyLogin;
    }

    public boolean isPsFlyLeave() {
        return psFlyLeave;
    }

    public boolean isPsFlyWorld() {
        return psFlyWorld;
    }


    public boolean isPsGodEnable() {
        return psGodEnable;
    }

    public boolean isPsGodCMIT() {
        return psGodCMIT;
    }

    public List<String> getPsGodPerms() {
        return psGodPerms;
    }

    public boolean isPsGodSchedule() {
        return psGodSchedule;
    }

    public int getPsGodInterval() {
        return psGodInterval;
    }

    public boolean isPsGodLogin() {
        return psGodLogin;
    }

    public boolean isPsGodLeave() {
        return psGodLeave;
    }

    public boolean isPsGodWorld() {
        return psGodWorld;
    }

    public boolean isPsOpEnable() {
        return psOpEnable;
    }

    public List<String> getPsOpPerms() {
        return psOpPerms;
    }

    public boolean isPsOpSchedule() {
        return psOpSchedule;
    }

    public int getPsOpInterval() {
        return psOpInterval;
    }

    public boolean isPsOpLogin() {
        return psOpLogin;
    }

    public boolean isPsOpLeave() {
        return psOpLeave;
    }

    public boolean isPsOpWorld() {
        return psOpWorld;
    }


    public boolean isPsGmEnable() {
        return psGmEnable;
    }

    public boolean isPsGm0Enable() {
        return psGm0Enable;
    }

    public boolean isPsGm1Enable() {
        return psGm1Enable;
    }

    public boolean isPsGm2Enable() {
        return psGm2Enable;
    }

    public boolean isPsGm3Enable() {
        return psGm3Enable;
    }

    public List<String> getPsGm0Perms() {
        return psGm0Perms;
    }

    public List<String> getPsGm1Perms() {
        return psGm1Perms;
    }

    public List<String> getPsGm2Perms() {
        return psGm2Perms;
    }

    public List<String> getPsGm3Perms() {
        return psGm3Perms;
    }

    public boolean isPsGmSchedule() {
        return psGmSchedule;
    }

    public int getPsGmInterval() {
        return psGmInterval;
    }

    public boolean isPsGmLogin() {
        return psGmLogin;
    }

    public boolean isPsGmLeave() {
        return psGmLeave;
    }

    public boolean isPsGmWorld() {
        return psGmWorld;
    }

    public String getPsGm0Default() {
        return psGm0Default;
    }

    public String getPsGm1Default() {
        return psGm1Default;
    }

    public String getPsGm2Default() {
        return psGm2Default;
    }

    public String getPsGm3Default() {
        return psGm3Default;
    }
}



