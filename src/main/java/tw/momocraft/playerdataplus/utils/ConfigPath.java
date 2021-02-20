package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.playerstatus.PlayerStatusMap;
import tw.momocraft.playerdataplus.utils.clean.CleanMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigPath {

    public ConfigPath() {
        setUp();
    }

    private void setUp() {
        setMsg();
        setClean();
        setNick();
        setPlayerStatus();
    }

    //  ============================================== //
    //         Message Variables                       //
    //  ============================================== //
    private String msgTitle;
    private String msgHelp;
    private String msgReload;
    private String msgVersion;
    private String msgCmdClean;
    private String msgCmdNick;
    private String msgCmdNickOther;

    private String msgNickInvalidLength;
    private String msgNickInvalidLengthTarget;
    private String msgNickInvalidNick;
    private String msgNickInvalidNickTarget;
    private String msgNickInvalidColor;
    private String msgNickInvalidColorTarget;
    private String msgNickInvalidColorInside;
    private String msgNickInvalidColorInsideTarget;
    private String msgNickChange;
    private String msgNickChangeTarget;
    private String msgNickClear;
    private String msgNickClearTarget;
    private String msgNickChangeColor;
    private String msgNickChangeColorTarget;

    //  ============================================== //
    //         Clean Variables                         //
    //  ============================================== //
    private boolean clean;
    private boolean cleanAuto;
    private boolean cleanLog;
    private boolean cleanBackup;
    private boolean cleanBackupZip;
    private String cleanBackupPath;
    private ConfigurationSection cleanConfig;
    private HashMap<String, CleanMap> cleanProp = new HashMap<>();

    //  ============================================== //
    //         Nick Variables                          //
    //  ============================================== //
    private boolean nick;
    private int nickLimit;
    private boolean nickPure;
    private List<String> nickBlackList;
    private boolean nickCMI;
    private boolean nickCMIUpdate;
    private String nickCMIOn;
    private String nickCMIOff;
    private boolean nickNameTagEdit;
    private String nickNTEOnPrefix;
    private String nickNTEOnSuffix;
    private String nickNTEOffPrefix;
    private String nickNTEOffSuffix;
    private List<String> nickCommandOn;
    private List<String> nickCommandOff;

    private String nickGroupsDefault;
    private Map<Integer, String> nickGroupsMap;
    private Map<String, String> nickColorsMap;

    //  ============================================== //
    //         Player Status Variables                 //
    //  ============================================== //
    private boolean playerStatus;
    private Map<String, PlayerStatusMap> playerStatusProp = new HashMap<>();


    //  ============================================== //
    //         Data Convertor Variables                //
    //  ============================================== //

    //  ============================================== //
    //         Message Setter                          //
    //  ============================================== //
    private void setMsg() {
        msgTitle = ConfigHandler.getConfig("config.yml").getString("Message.Commands.title");
        msgHelp = ConfigHandler.getConfig("config.yml").getString("Message.Commands.help");
        msgReload = ConfigHandler.getConfig("config.yml").getString("Message.Commands.reload");
        msgVersion = ConfigHandler.getConfig("config.yml").getString("Message.Commands.version");
        msgCmdClean = ConfigHandler.getConfig("config.yml").getString("Message.Commands.clean");
        msgCmdNick = ConfigHandler.getConfig("config.yml").getString("Message.Commands.nick");
        msgCmdNickOther = ConfigHandler.getConfig("config.yml").getString("Message.Commands.nickOther");

        msgNickInvalidLength = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidLength");
        msgNickInvalidLengthTarget = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidLengthTarget");
        msgNickInvalidNick = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidNick");
        msgNickInvalidNickTarget = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidNickTarget");
        msgNickInvalidColor = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidColor");
        msgNickInvalidColorTarget = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidColorTarget");
        msgNickInvalidColorInside = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidColorInside");
        msgNickInvalidColorInsideTarget = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.invalidColorInsideTarget");
        msgNickChange = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.change");
        msgNickChangeTarget = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.changeTarget");
        msgNickClear = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.clear");
        msgNickClearTarget = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.clearTarget");
        msgNickChangeColor = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.changeColor");
        msgNickChangeColorTarget = ConfigHandler.getConfig("config.yml").getString("Message.Commands.Nick.changeColorTarget");
    }

    //  ============================================== //
    //         Clean Setter                            //
    //  ============================================== //
    private void setClean() {
        clean = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Enable");
        cleanAuto = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Auto-Clean.Enable");
        cleanConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Clean.Groups");
        cleanLog = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Log");
        cleanBackup = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.Enable");
        cleanBackupPath = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Path");
        cleanBackupZip = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.Zip");
        CleanMap cleanMap;
        for (String group : cleanConfig.getKeys(false)) {
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Clean.Groups." + group + ".Enable"))
                continue;
            cleanMap = new CleanMap();
            cleanMap.setGroupName(group);
            cleanMap.setExpiration(ConfigHandler.getConfig("config.yml").getInt("Clean.Groups." + group + ".Expiration"));
            cleanMap.setBackup(ConfigHandler.getConfig("config.yml").getBoolean("Clean.Groups." + group + ".Backup"));
            if (group.equals("Region")) {
                cleanMap.setResidenceBypass(ConfigHandler.getConfig("config.yml").getBoolean("Clean.Groups." + group + ".Residence-Bypass"));
            }
            switch (group.toLowerCase()) {
                case "region":
                    cleanMap.setList(ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups." + group + ".Worlds"));
                    cleanMap.setIgnoreList(ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups." + group + ".Ignore-Regions"));
                    break;
                case "mycommand":
                    cleanMap.setList(ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups." + group + ".Variable"));
                    cleanMap.setIgnoreList(ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups." + group + ".Ignore-Variable"));
                    cleanMap.setList2(ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups." + group + ".Playerdata"));
                    cleanMap.setIgnoreList2(ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups." + group + ".Ignore-Playerdata"));
                    break;
            }
            cleanProp.put(group, cleanMap);
        }
    }

    //  ============================================== //
    //         Nick Setter                             //
    //  ============================================== //
    private void setNick() {
        nick = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Enable");
        nickLimit = ConfigHandler.getConfig("config.yml").getInt("Nick.Limits.Length");
        nickPure = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Limits.Pure-Color");
        nickBlackList = ConfigHandler.getConfig("config.yml").getStringList("Nick.Limits.Black-List");
        nickCMI = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Enable");
        nickCMIUpdate = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Tablist-Update");
        nickCMIOn = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.On");
        nickCMIOff = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.Off");
        nickNameTagEdit = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.NameTagEdit.Enable");
        nickNTEOnPrefix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.On.Prefix");
        nickNTEOnSuffix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.On.Suffix");
        nickNTEOffPrefix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.Off.Prefix");
        nickNTEOffSuffix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.Off.Suffix");
        nickCommandOn = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands");
        nickCommandOff = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands-Off");
        nickGroupsDefault = ConfigHandler.getConfig("config.yml").getString("Nick.Groups.Default");

        nickGroupsMap = new HashMap<>();
        ConfigurationSection nickGroups = ConfigHandler.getConfig("config.yml").getConfigurationSection("Nick.Groups.Custom");
        if (nickGroups != null) {
            for (String group : nickGroups.getKeys(false)) {
                nickGroupsMap.put(Integer.parseInt(group), ConfigHandler.getConfig("config.yml").getString("Nick.Groups.Custom." + group));
            }
        }
        nickColorsMap = new HashMap<>();
        ConfigurationSection nickColors = ConfigHandler.getConfig("config.yml").getConfigurationSection("Nick.Colors.Correspond");
        if (nickColors != null) {
            for (String group : nickColors.getKeys(false)) {
                nickColorsMap.put(group, ConfigHandler.getConfig("config.yml").getString("Nick.Colors.Correspond." + group));
            }
        }
    }

    //  ============================================== //
    //         Player Status Setter                    //
    //  ============================================== //
    private void setPlayerStatus() {
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
    //  ============================================== //
    //         Data Convertor Setter                   //
    //  ============================================== //

    //  ============================================== //
    //         Message Getter                          //
    //  ============================================== //
    public String getMsgTitle() {
        return msgTitle;
    }

    public String getMsgHelp() {
        return msgHelp;
    }

    public String getMsgReload() {
        return msgReload;
    }

    public String getMsgVersion() {
        return msgVersion;
    }

    public String getMsgCmdClean() {
        return msgCmdClean;
    }

    public String getMsgCmdNick() {
        return msgCmdNick;
    }

    public String getMsgCmdNickOther() {
        return msgCmdNickOther;
    }

    public String getMsgNickInvalidLength() {
        return msgNickInvalidLength;
    }

    public String getMsgNickInvalidLengthTarget() {
        return msgNickInvalidLengthTarget;
    }

    public String getMsgNickInvalidNick() {
        return msgNickInvalidNick;
    }

    public String getMsgNickInvalidNickTarget() {
        return msgNickInvalidNickTarget;
    }

    public String getMsgNickInvalidColor() {
        return msgNickInvalidColor;
    }

    public String getMsgNickInvalidColorTarget() {
        return msgNickInvalidColorTarget;
    }

    public String getMsgNickInvalidColorInside() {
        return msgNickInvalidColorInside;
    }

    public String getMsgNickInvalidColorInsideTarget() {
        return msgNickInvalidColorInsideTarget;
    }

    public String getMsgNickChange() {
        return msgNickChange;
    }

    public String getMsgNickChangeTarget() {
        return msgNickChangeTarget;
    }

    public String getMsgNickClear() {
        return msgNickClear;
    }

    public String getMsgNickClearTarget() {
        return msgNickClearTarget;
    }

    public String getMsgNickChangeColor() {
        return msgNickChangeColor;
    }

    public String getMsgNickChangeColorTarget() {
        return msgNickChangeColorTarget;
    }

    //  ============================================== //
    //         Clean Getter                            //
    //  ============================================== //
    public boolean isClean() {
        return clean;
    }

    public boolean isCleanAuto() {
        return cleanAuto;
    }

    public boolean isCleanLog() {
        return cleanLog;
    }

    public boolean isCleanBackup() {
        return cleanBackup;
    }

    public boolean isCleanBackupZip() {
        return cleanBackupZip;
    }

    public String getCleanBackupPath() {
        return cleanBackupPath;
    }

    public ConfigurationSection getCleanConfig() {
        return cleanConfig;
    }

    public HashMap<String, CleanMap> getCleanProp() {
        return cleanProp;
    }


    //  ============================================== //
    //         Nick Getter                             //
    //  ============================================== //

    //  ============================================== //
    //         Player Status Getter                    //
    //  ============================================== //

    //  ============================================== //
    //         Data Convertor Getter                   //
    //  ============================================== //
}



