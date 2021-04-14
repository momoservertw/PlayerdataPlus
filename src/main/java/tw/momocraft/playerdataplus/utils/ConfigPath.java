package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.playerstatus.PlayerStatusMap;
import tw.momocraft.playerdataplus.utils.clean.CleanMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

    private String msgCleanStart;
    private String msgCleanEnd;
    private String msgCleanSucceed;
    private String msgCleanListed;
    private String msgCleanToggleOn;
    private String msgCleanToggleOff;
    private String msgCleanAlreadyOn;
    private String msgCleanAlreadyOff;

    //  ============================================== //
    //         Clean Variables                         //
    //  ============================================== //
    private boolean clean;
    private boolean cleanAuto;
    private boolean cleanLog;
    private boolean cleanBackup;
    private boolean cleanBackupZip;
    private String cleanBackupPath;
    private final Map<String, CleanMap> cleanProp = new HashMap<>();

    //  ============================================== //
    //         Nick Variables                          //
    //  ============================================== //
    private boolean nick;
    private int nickLimit;
    private boolean nickColorCode;
    private List<String> nickBlackList;
    private boolean nickCMI;
    private boolean nickCMIUpdateTablist;
    private String nickCMISet;
    private String nickCMIClear;
    private boolean nickNameTagEdit;
    private String nickNTESetPrefix;
    private String nickNTESetSuffix;
    private String nickNTEClearPrefix;
    private String nickNTEClearSuffix;
    private List<String> nickCommandSet;
    private List<String> nickCommandClear;

    private final Map<String, String> nickGroupsProp = new LinkedHashMap<>();

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

        msgCleanStart = ConfigHandler.getConfig("config.yml").getString("Message.Clean.start");
        msgCleanEnd = ConfigHandler.getConfig("config.yml").getString("Message.Clean.end");
        msgCleanSucceed = ConfigHandler.getConfig("config.yml").getString("Message.Clean.succeed");
        msgCleanListed = ConfigHandler.getConfig("config.yml").getString("Message.Clean.listed");
        msgCleanToggleOn = ConfigHandler.getConfig("config.yml").getString("Message.Clean.toggleOn");
        msgCleanToggleOff = ConfigHandler.getConfig("config.yml").getString("Message.Clean.toggleOff");
        msgCleanAlreadyOn = ConfigHandler.getConfig("config.yml").getString("Message.Clean.alreadyOn");
        msgCleanAlreadyOff = ConfigHandler.getConfig("config.yml").getString("Message.Clean.alreadyOff");
    }

    //  ============================================== //
    //         Clean Setter                            //
    //  ============================================== //
    private void setClean() {
        clean = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Enable");
        cleanAuto = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Auto-Clean.Enable");
        cleanLog = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Log");
        cleanBackup = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.Enable");
        cleanBackupPath = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Path");
        cleanBackupZip = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.Zip");
        CleanMap cleanMap;
        ConfigurationSection cleanConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Clean.Groups");
        if (cleanConfig == null)
            return;
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
        nickColorCode = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Limits.Prevent-Color-Code");
        nickBlackList = ConfigHandler.getConfig("config.yml").getStringList("Nick.Limits.Black-List");
        nickCMI = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Enable");
        nickCMIUpdateTablist = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Tablist-Update");
        nickCMISet = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.Set");
        nickCMIClear = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.Clear");
        nickNameTagEdit = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.NameTagEdit.Enable");
        nickNTESetPrefix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.Set.Prefix");
        nickNTESetSuffix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.Set.Suffix");
        nickNTEClearPrefix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.Clear.Prefix");
        nickNTEClearSuffix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.NameTagEdit.Clear.Suffix");
        nickCommandSet = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands");
        nickCommandClear = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands-Clear");
        ConfigurationSection nickGroups = ConfigHandler.getConfig("config.yml").getConfigurationSection("Nick.Groups");
        if (nickGroups != null) {
            for (String group : nickGroups.getKeys(false))
                nickGroupsProp.put(group, ConfigHandler.getConfig("config.yml").getString("Nick.Groups." + group));
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

    public String getMsgCleanStart() {
        return msgCleanStart;
    }

    public String getMsgCleanEnd() {
        return msgCleanEnd;
    }

    public String getMsgCleanSucceed() {
        return msgCleanSucceed;
    }

    public String getMsgCleanListed() {
        return msgCleanListed;
    }

    public String getMsgCleanToggleOn() {
        return msgCleanToggleOn;
    }

    public String getMsgCleanToggleOff() {
        return msgCleanToggleOff;
    }

    public String getMsgCleanAlreadyOn() {
        return msgCleanAlreadyOn;
    }

    public String getMsgCleanAlreadyOff() {
        return msgCleanAlreadyOff;
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

    public Map<String, CleanMap> getCleanProp() {
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



