package tw.momocraft.playerdataplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ConfigPath {

    public ConfigPath() {
        setUp();
    }

    private void setUp() {
        setMsg();
        setGeneral();
        //setClean();
        setNick();
        //setPlayerData();
        //setPlayerStatus();
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
    private String msgCmdPlayerstatus;

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

    private String msgCleanSucceed;
    private String msgCleanListed;
    private String msgCleanStart;
    private String msgCleanEnd;
    private String msgCleanEmpty;
    private String msgCleanToggleOn;
    private String msgCleanToggleOff;
    private String msgCleanAlreadyOn;
    private String msgCleanAlreadyOff;
    private String msgPSScheduleStart;
    private String msgPSScheduleEnd;
    private String msgPSScheduleAlreadyStart;
    private String msgPSScheduleAlreadyEnd;

    //  ============================================== //
    //         MySQL Variables                         //
    //  ============================================== //
    private String mysqlHost;
    private String mysqlPort;
    private String mysqlDatabase;
    private String mysqlPrefix;
    private String mysqlUsername;
    private String mysqlPassword;

    /*
    //  ============================================== //
    //         Clean Variables                         //
    //  ============================================== //
    private boolean clean;
    private boolean cleanAuto;
    private boolean cleanLog;
    private boolean cleanBackup;
    private boolean cleanBackupZip;
    private String cleanBackupPath;
    private boolean cleanResBypass;
    private boolean cleanMycmd;
    private List<String> cleanMycmdList;
    private List<String> cleanMycmdIgnoreList;
    private final Map<String, CleanMap> cleanProp = new HashMap<>();

    */

    //  ============================================== //
    //         Nick Variables                          //
    //  ============================================== //
    private boolean nick;
    private boolean nickAlias;
    private int nickLength;
    private List<String> nickBlackList;
    private String nickMsg;
    private boolean nickCMI;
    private boolean nickCMIUpdateTabList;
    private String nickCMINickSet;
    private String nickCMIPlatePrefix;
    private String nickCMIPlateSuffix;
    private String nickCMIPlateColor;
    private boolean nickDiscordSRV;
    private String nickDiscordSRVSet;
    private List<String> nickCommandSet;
    private List<String> nickCommandClear;

    private final Map<String, String> nickGroupsProp = new LinkedHashMap<>();

    /*
    //  ============================================== //
    //         PlayerData Variables                    //
    //  ============================================== //
    private boolean playerData;
    private boolean playerDataAutoSave;
    private boolean playerDataAutoSaveMsg;
    private int playerDataAutoSaveInterval;
    private boolean playerDataMsgStarting;
    private boolean playerDataMsgSucceed;
    private boolean playerDataMsgFailed;
    private boolean playerDataGroupNick;
    private boolean playerDataGroupCustom;

    //  ============================================== //
    //         Player Status Variables                 //
    //  ============================================== //
    private boolean playerStatus;
    private final Map<String, PlayerStatusMap> playerStatusProp = new HashMap<>();
    private boolean psCheckSchedule;
    private int psCheckScheduleInterval;
    private boolean psCheckLogin;
    private boolean psCheckWorldChange;
    private boolean psCMI;
    private boolean psRes;
    private boolean psFlyTp;
    private boolean psFlyTpSpawn;
    private String psGMDefault;



    //  ============================================== //
    //         Data Convertor Variables                //
    //  ============================================== //
    private boolean userConvertor;
    private boolean userConvertorCMI;
    private boolean userConvertorPlayerPoints;
    private boolean userConvertorMyPet;
    private boolean userConvertorLuckPerms;
    private boolean userConvertorResidence;
    private boolean userConvertorMySuite;

     */

    //  ============================================== //
    //         Message Setter                          //
    //  ============================================== //
    private void setMsg() {
        msgTitle = ConfigHandler.getConfig("message.yml").getString("Message.Commands.title");
        msgHelp = ConfigHandler.getConfig("message.yml").getString("Message.Commands.help");
        msgReload = ConfigHandler.getConfig("message.yml").getString("Message.Commands.reload");
        msgVersion = ConfigHandler.getConfig("message.yml").getString("Message.Commands.version");
        msgCmdClean = ConfigHandler.getConfig("message.yml").getString("Message.Commands.clean");
        msgCmdNick = ConfigHandler.getConfig("message.yml").getString("Message.Commands.nick");
        msgCmdNickOther = ConfigHandler.getConfig("message.yml").getString("Message.Commands.nickOther");
        msgCmdPlayerstatus = ConfigHandler.getConfig("message.yml").getString("Message.Commands.playerstatus");

        msgNickInvalidLength = ConfigHandler.getConfig("message.yml").getString("Message.Nick.invalidLength");
        msgNickInvalidLengthTarget = ConfigHandler.getConfig("message.yml").getString("Message.Nick.invalidLengthTarget");
        msgNickInvalidNick = ConfigHandler.getConfig("message.yml").getString("Message.Nick.invalidNick");
        msgNickInvalidNickTarget = ConfigHandler.getConfig("message.yml").getString("Message.Nick.invalidNickTarget");
        msgNickInvalidColor = ConfigHandler.getConfig("message.yml").getString("Message.Nick.invalidColor");
        msgNickInvalidColorTarget = ConfigHandler.getConfig("message.yml").getString("Message.Nick.invalidColorTarget");
        msgNickInvalidColorInside = ConfigHandler.getConfig("message.yml").getString("Message.Nick.invalidColorInside");
        msgNickInvalidColorInsideTarget = ConfigHandler.getConfig("message.yml").getString("Message.Commands.Nick.invalidColorInsideTarget");
        msgNickChange = ConfigHandler.getConfig("message.yml").getString("Message.Nick.change");
        msgNickChangeTarget = ConfigHandler.getConfig("message.yml").getString("Message.Nick.changeTarget");
        msgNickClear = ConfigHandler.getConfig("message.yml").getString("Message.Nick.clear");
        msgNickClearTarget = ConfigHandler.getConfig("message.yml").getString("Message.Nick.clearTarget");

        msgCleanStart = ConfigHandler.getConfig("config.yml").getString("Message.Clean.start");
        msgCleanEnd = ConfigHandler.getConfig("config.yml").getString("Message.Clean.end");
        msgCleanSucceed = ConfigHandler.getConfig("config.yml").getString("Message.Clean.succeed");
        msgCleanEmpty = ConfigHandler.getConfig("config.yml").getString("Message.Clean.empty");
        msgCleanListed = ConfigHandler.getConfig("config.yml").getString("Message.Clean.listed");
        msgCleanToggleOn = ConfigHandler.getConfig("config.yml").getString("Message.Clean.toggleOn");
        msgCleanToggleOff = ConfigHandler.getConfig("config.yml").getString("Message.Clean.toggleOff");
        msgCleanAlreadyOn = ConfigHandler.getConfig("config.yml").getString("Message.Clean.alreadyOn");
        msgCleanAlreadyOff = ConfigHandler.getConfig("config.yml").getString("Message.Clean.alreadyOff");

        msgPSScheduleStart = ConfigHandler.getConfig("config.yml").getString("Message.Player-Status.scheduleStart");
        msgPSScheduleEnd = ConfigHandler.getConfig("config.yml").getString("Message.Player-Status.scheduleEnd");
        msgPSScheduleAlreadyStart = ConfigHandler.getConfig("config.yml").getString("Message.Player-Status.scheduleAlreadyStart");
        msgPSScheduleAlreadyEnd = ConfigHandler.getConfig("config.yml").getString("Message.Player-Status.scheduleAlreadyStart");
    }

    //  ============================================== //
    //         General Setter                          //
    //  ============================================== //
    private void setGeneral() {
        mysqlHost = ConfigHandler.getConfig("config.yml").getString("General.MySQL.Hostname");
        mysqlPort = ConfigHandler.getConfig("config.yml").getString("General.MySQL.Port");
        mysqlDatabase = ConfigHandler.getConfig("config.yml").getString("General.MySQL.Database");
        mysqlPrefix = ConfigHandler.getConfig("config.yml").getString("General.MySQL.Prefix");
        mysqlUsername = ConfigHandler.getConfig("config.yml").getString("General.MySQL.Username");
        mysqlPassword = ConfigHandler.getConfig("config.yml").getString("General.MySQL.Password");
    }


    /*
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
        cleanResBypass = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Groups.Region.Residence-Bypass");
        cleanMycmd = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Groups.MyCommand.Playerdata.Enable");
        cleanMycmdList = ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups.MyCommand.Playerdata.List");
        cleanMycmdIgnoreList = ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups.MyCommand.Playerdata.Ignore-List");
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
            cleanMap.setIgnoreList(ConfigHandler.getConfig("config.yml").getStringList("Clean.Groups." + group + ".Ignore-Variable"));
            cleanProp.put(group, cleanMap);
        }
    }

     */

    //  ============================================== //
    //         Nick Setter                             //
    //  ============================================== //
    private void setNick() {
        nick = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Enable");
        nickAlias = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Settings.Command-Alias");
        nickLength = ConfigHandler.getConfig("config.yml").getInt("Nick.Limits.Length");
        nickBlackList = ConfigHandler.getConfig("config.yml").getStringList("Nick.Limits.Black-List");
        nickMsg = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.Message");
        nickCMI = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Enable");
        nickCMIUpdateTabList = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.CMI.Update-Tablist");
        nickCMINickSet = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.Nick.Set");
        nickCMIPlatePrefix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.Name-Plate.Prefix");
        nickCMIPlateSuffix = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.Name-Plate.Suffix");
        nickCMIPlateColor = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.CMI.Name-Plate.Color");
        nickDiscordSRV = ConfigHandler.getConfig("config.yml").getBoolean("Nick.Formats.DiscordSRV.Enable");
        nickDiscordSRVSet = ConfigHandler.getConfig("config.yml").getString("Nick.Formats.DiscordSRV.Set");
        nickCommandSet = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands");
        nickCommandClear = ConfigHandler.getConfig("config.yml").getStringList("Nick.Formats.Commands-Clear");
        ConfigurationSection nickGroups = ConfigHandler.getConfig("config.yml").getConfigurationSection("Nick.Groups");
        if (nickGroups != null) {
            Map<String, String> nickPriorityGroup = new LinkedHashMap<>();
            for (String group : nickGroups.getKeys(false))
                nickPriorityGroup.put(group, ConfigHandler.getConfig("config.yml").getString("Nick.Groups." + group + ".Priority"));
            nickPriorityGroup = CorePlusAPI.getUtils().sortByValue(nickPriorityGroup);
            for (String group : nickPriorityGroup.keySet()) {
                nickGroupsProp.put(group, ConfigHandler.getConfig("config.yml").getString("Nick.Groups." + group + ".Color"));
            }
        }
    }

    /*
    //  ============================================== //
    //         PlayerData Setter                         //
    //  ============================================== //
    private void setPlayerData() {
        playerDataAutoSave = ConfigHandler.getConfig("config.yml").getBoolean("PlayerData.Settings.Auto-Save.Enable");
        playerDataAutoSaveInterval = ConfigHandler.getConfig("config.yml").getInt("PlayerData.Settings.Auto-Save.Interval");
        playerDataAutoSaveMsg = ConfigHandler.getConfig("config.yml").getBoolean("PlayerData.Settings.Auto-Save.Message");
        playerDataMsgStarting = ConfigHandler.getConfig("config.yml").getBoolean("PlayerData.Message.Starting");
        playerDataMsgSucceed = ConfigHandler.getConfig("config.yml").getBoolean("PlayerData.Message.Succeed");
        playerDataMsgFailed = ConfigHandler.getConfig("config.yml").getBoolean("PlayerData.Message.Failed");
        playerDataGroupNick = ConfigHandler.getConfig("config.yml").getBoolean("PlayerData.Groups.Succeed");
        playerDataGroupCustom = ConfigHandler.getConfig("config.yml").getBoolean("PlayerData.Message.Failed");
    }

     */

    /*
    //  ============================================== //
    //         Playerdata Getter                       //
    //  ============================================== //
    public boolean isPlayerData() {
        return playerData;
    }

    public boolean isPlayerDataAutoSave() {
        return playerDataAutoSave;
    }

    public boolean isPlayerDataAutoSaveMsg() {
        return playerDataAutoSaveMsg;
    }

    public int getPlayerDataAutoSaveInterval() {
        return playerDataAutoSaveInterval;
    }

    public boolean isPlayerDataMsgStarting() {
        return playerDataMsgStarting;
    }

    public boolean isPlayerDataMsgSucceed() {
        return playerDataMsgSucceed;
    }

    public boolean isPlayerDataMsgFailed() {
        return playerDataMsgFailed;
    }

    public boolean isPlayerDataGroupNick() {
        return playerDataGroupNick;
    }

    public boolean isPlayerDataGroupCustom() {
        return playerDataGroupCustom;
    }

     */

    /*
    //  ============================================== //
    //         Player Status Setter                    //
    //  ============================================== //
    private void setPlayerStatus() {
        playerStatus = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Enable");
        psCheckSchedule = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Check.Schedule.Enable");
        psCheckScheduleInterval = ConfigHandler.getConfig("config.yml").getInt("Player-Status.Settings.Check.Schedule.Interval");
        psCheckLogin = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Check.Login");
        psCheckWorldChange = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Check.World-Change");
        psCMI = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Features.CMI");
        psRes = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Settings.Features.Residence");
        psFlyTp = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Teleport.Enable");
        psFlyTpSpawn = ConfigHandler.getConfig("config.yml").getBoolean("Player-Status.Fly.Teleport.Force-Spawn");
        psGMDefault = ConfigHandler.getConfig("config.yml").getString("Player-Status.Gamemode.Default").toUpperCase();

        PlayerStatusMap playerStatusMap;
        ConfigurationSection groupsConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Player-Status");
        List<String> valueStringList;
        if (groupsConfig != null) {
            for (String group : groupsConfig.getKeys(false)) {
                if (!ConfigHandler.getConfig("config.yml").getBoolean("Player-Status." + group + ".Enable"))
                    continue;
                if (!CorePlusAPI.getDepend().CMIEnabled() && (group.equals("God")))
                    continue;
                playerStatusMap = new PlayerStatusMap();
                valueStringList = ConfigHandler.getConfig("entities.yml").getStringList("Player-Status." + group + ".Conditions");
                if (!valueStringList.isEmpty())
                    playerStatusMap.setConditions(valueStringList);
                valueStringList = ConfigHandler.getConfig("entities.yml").getStringList("Player-Status." + group + ".Location");
                if (!valueStringList.isEmpty())
                    playerStatusMap.setLocList(valueStringList);
                playerStatusProp.put(group.toLowerCase(), playerStatusMap);
            }
        }
    }

     */
    /*

    //  ============================================== //
    //         User Convertor Setter                   //
    //  ============================================== //
    private void setUserConvertor() {
        userConvertor = ConfigHandler.getConfig("config.yml").getBoolean("User-Convertor.Enable");
        userConvertorCMI = ConfigHandler.getConfig("config.yml").getBoolean("User-Convertor.Groups.CMI");
        userConvertorPlayerPoints = ConfigHandler.getConfig("config.yml").getBoolean("User-Convertor.Groups.PlayerPoints");
        userConvertorLuckPerms = ConfigHandler.getConfig("config.yml").getBoolean("User-Convertor.Groups.LuckPerms");
        userConvertorResidence = ConfigHandler.getConfig("config.yml").getBoolean("User-Convertor.Groups.Residence");
        userConvertorMySuite = ConfigHandler.getConfig("config.yml").getBoolean("User-Convertor.Groups.MySuite");
    }

     */

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

    public String getMsgCmdPlayerstatus() {
        return msgCmdPlayerstatus;
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

    //  ============================================== //
    //         General Getter                          //
    //  ============================================== //
    public String getMysqlHost() {
        return mysqlHost;
    }

    public String getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlPrefix() {
        return mysqlPrefix;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }


    public String getMsgCleanSucceed() {
        return msgCleanSucceed;
    }

    public String getMsgCleanListed() {
        return msgCleanListed;
    }

    public String getMsgCleanStart() {
        return msgCleanStart;
    }

    public String getMsgCleanEnd() {
        return msgCleanEnd;
    }

    public String getMsgCleanEmpty() {
        return msgCleanEmpty;
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

    public String getMsgPSScheduleStart() {
        return msgPSScheduleStart;
    }

    public String getMsgPSScheduleEnd() {
        return msgPSScheduleEnd;
    }

    public String getMsgPSScheduleAlreadyStart() {
        return msgPSScheduleAlreadyStart;
    }

    public String getMsgPSScheduleAlreadyEnd() {
        return msgPSScheduleAlreadyEnd;
    }

    //  ============================================== //
    //         General Getter                          //
    //  ============================================== //


    //  ============================================== //
    //         Clean Getter                            //
    //  ============================================== //

    /*
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

    public boolean isCleanResBypass() {
        return cleanResBypass;
    }

    public boolean isCleanMycmd() {
        return cleanMycmd;
    }

    public List<String> getCleanMycmdList() {
        return cleanMycmdList;
    }

    public List<String> getCleanMycmdIgnoreList() {
        return cleanMycmdIgnoreList;
    }

    public Map<String, CleanMap> getCleanProp() {
        return cleanProp;
    }

    */

    //  ============================================== //
    //         Nick Getter                             //
    //  ============================================== //
    public boolean isNick() {
        return nick;
    }

    public int getNickLength() {
        return nickLength;
    }

    public boolean isNickAlias() {
        return nickAlias;
    }

    public List<String> getNickBlackList() {
        return nickBlackList;
    }

    public String getNickMsg() {
        return nickMsg;
    }

    public boolean isNickCMI() {
        return nickCMI;
    }

    public boolean isNickCMIUpdateTabList() {
        return nickCMIUpdateTabList;
    }

    public String getNickCMINickSet() {
        return nickCMINickSet;
    }

    public String getNickCMIPlatePrefix() {
        return nickCMIPlatePrefix;
    }

    public String getNickCMIPlateSuffix() {
        return nickCMIPlateSuffix;
    }

    public String getNickCMIPlateColor() {
        return nickCMIPlateColor;
    }

    public boolean isNickDiscordSRV() {
        return nickDiscordSRV;
    }

    public String getNickDiscordSRVSet() {
        return nickDiscordSRVSet;
    }

    public List<String> getNickCommandSet() {
        return nickCommandSet;
    }

    public List<String> getNickCommandClear() {
        return nickCommandClear;
    }

    public Map<String, String> getNickGroupsProp() {
        return nickGroupsProp;
    }

    /*

    //  ============================================== //
    //         Player Status Getter                    //
    //  ============================================== //

    public boolean isPlayerStatus() {
        return playerStatus;
    }

    public Map<String, PlayerStatusMap> getPlayerStatusProp() {
        return playerStatusProp;
    }

    public boolean isPsCheckSchedule() {
        return psCheckSchedule;
    }

    public int getPsCheckScheduleInterval() {
        return psCheckScheduleInterval;
    }

    public boolean isPsCheckLogin() {
        return psCheckLogin;
    }

    public boolean isPsCheckWorldChange() {
        return psCheckWorldChange;
    }

    public boolean isPsCMI() {
        return psCMI;
    }

    public boolean isPsRes() {
        return psRes;
    }

    public boolean isPsFlyTp() {
        return psFlyTp;
    }

    public boolean isPsFlyTpSpawn() {
        return psFlyTpSpawn;
    }

    public String getPsGMDefault() {
        return psGMDefault;
    }

    //  ============================================== //
    //         User Convertor Getter                   //
    //  ============================================== //
    public boolean isUserConvertor() {
        return userConvertor;
    }

    public boolean isUserConvertorCMI() {
        return userConvertorCMI;
    }

    public boolean isUserConvertorPlayerPoints() {
        return userConvertorPlayerPoints;
    }

    public boolean isUserConvertorMyPet() {
        return userConvertorMyPet;
    }

    public boolean isUserConvertorLuckPerms() {
        return userConvertorLuckPerms;
    }

    public boolean isUserConvertorResidence() {
        return userConvertorResidence;
    }

    public boolean isUserConvertorMySuite() {
        return userConvertorMySuite;
    }

     */
}



