package tw.momocraft.playerdataplus.utils.clean;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import com.earth2me.essentials.Essentials;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import fr.xephi.authme.api.v3.AuthMeApi;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.handlers.UtilsHandler;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.bukkit.Bukkit.getServer;

public class Clean {
    private Map<UUID, Long> playerTimeMap;
    private Map<String, UUID> playerUUIDMap;
    // Type, Group, Element
    private Table<String, String, List<String>> expiredTable;

    private boolean starting = false;

    public void setStarting(boolean enable) {
        starting = enable;
    }

    public boolean getStarting() {
        return starting;
    }

    public void toggle(CommandSender sender, String type, boolean toggle) {
        if (toggle) {
            if (starting) {
                // Already on
                CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanAlreadyOn());
            } else {
                // Turns on
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanToggleOn(), sender);
                start(sender, type);
            }
        } else {
            if (!starting) {
                // Already off
                CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanAlreadyOff());
            } else {
                // Turns off
                starting = false;
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanToggleOff(), sender);
            }
        }
    }

    public void start(CommandSender sender, String title) {
        starting = true;
        Map<String, CleanMap> cleanProp = ConfigHandler.getConfigPath().getCleanProp();
        setPlayersLoginTime();
        expiredTable = HashBasedTable.create();
        // Scanning the data.
        if (title != null) {
            CleanMap cleanMap = cleanProp.get(title);
            if (cleanMap != null)
                scanData(cleanMap);
        } else {
            scanAllData();
        }
        if (expiredTable.isEmpty()) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgCleanEnd(), sender);
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean restart = false;
                cleanManager(title);
                backup();
                if (expiredTable.isEmpty()) {
                    CorePlusAPI.getMsg().sendMsg(ConfigHandler.getPrefix(), sender,
                            "&6There has no any expired data!");
                    cancel();
                    return;
                }
                if (restart) {
                    CorePlusAPI.getMsg().sendMsg(ConfigHandler.getPrefix(), sender,
                            "&eCleanup process has not finished yet! &8- &6" + title);
                    CorePlusAPI.getMsg().sendMsg(ConfigHandler.getPrefix(), sender, "");
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(PlayerdataPlus.getInstance(), 0, 60L);
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgCleanEnd(), sender);
    }

    private void setPlayersLoginTime() {
        playerTimeMap = new HashMap<>();
        playerUUIDMap = new HashMap<>();
        List<String> variables = new ArrayList<>();
        variables.add("username");
        variables.add("last_login");
        Map<String, Map<String, String>> map = UtilsHandler.getMySQL().getValues(ConfigHandler.getPlugin(),
                "coreplus", "players", "uuid", variables);
        UUID uuid;
        for (String uuidString : map.keySet()) {
            uuid = UUID.fromString(uuidString);
            playerTimeMap.put(uuid, Long.parseLong(map.get(uuidString).get("last_login")));
            playerUUIDMap.put(map.get(uuidString).get("username"), uuid);
        }
    }

    private void scanData(CleanMap cleanMap) {
        String groupName = cleanMap.getGroupName();
        switch (groupName) {
            case "logs":
                expiredTable.put(groupName, cleanMap);
                break;
            case "playerdata":
                expiredTable.put(groupName, getPlayerdataList());
                break;
            case "advancements":
                expiredTable.put(groupName, getAdvancementList());
                break;
            case "stats":
                expiredTable.put(groupName, getStatsList());
                break;
            case "regions":
                expiredTable.put(groupName, getRegionList());
                break;
            case "authme":
                expiredTable.add(groupName, getAuthMeExpiredList());
                break;
            case "cmi":
                expiredTable.put(groupName, getCMIExpiredList());
                break;
            case "discordsrv":
                expiredTable.put(groupName, getDiscordSRVList());
                break;
            case "mypet":
                expiredTable.put(groupName, getMyPetList());
                break;
            case "mycommand":
                expiredTable.put(groupName, getMyCommandList());
                break;
        }
    }

    private void cleanManager(String title) {
        title = title.toLowerCase();
        switch (title) {
            case "logs":
                cleanLogs();
                return;
            case "playerdata":
                cleanPlayerdata("Playerdata");
                return;
            case "advancements":
                cleanPlayerdata("Advancements");
                return;
            case "stats":
                cleanPlayerdata("Stats");
                return;
            case "regions":
                cleanRegions();
                return;
            case "authme":
                cleanAuthMe();
                return;
            case "cmi":
                cleanCMI();
                return;
            case "essentials":
                cleanEssentials();
                return;
            case "discordsrv":
                cleanDiscordSRV();
                return;
            case "mypet":
                cleanMyPet();
                return;
            case "mycommand":
                cleanMyCommand();
                return;
            default:
                break;
                /*
            case "LuckPerms":
                if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
                    if (ConfigHandler.getDepends().SkinsRestorerEnabled()) {
                        Set<UUID> userList;
                        try {
                            userList = LuckPermsProvider.get().getUser().getUniqueUsers().get();
                        } catch (InterruptedException | ExecutionException e) {
                            ServerHandler.sendDebugTrace(e);
                            break;
                        }
                        List<String> uuidList = new ArrayList<>();
                        for (UUID uuid : userList) {
                            uuidList.add(uuid.toString());
                        }
                        expiredList = getExpiredUUIDList(title, uuidList);
                        if (!expiredList.isEmpty()) {
                            cleanTable.put(title, "users", expiredList);
                            for (String uuid : expiredList) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "authme unregister " + user);
                                LuckPermsProvider.get().getUser().getUser(UUID.fromString(uuid));
                            }
                        }
                    }
                }
                break;
                 */
        }
    }

    // AuthMe
    private Map<String, List<String>> getAuthMeExpiredList(CleanMap cleanMap) {
        Map<String, List<String>> output = new HashMap<>();
        List<String> list = new ArrayList<>();
        long time = cleanMap.getExpiration();
        for (String name : AuthMeApi.getInstance().getRegisteredNames()) {
            if (playerTimeMap.get(playerUUIDMap.get(name)) >= time)
                list.add(name);
        }
        output.put("player", list);
        return output;
    }

    private void purgeAuthMe(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "authme unregister " + value);
    }

    // CMI
    private Map<String, List<String>> getCMIExpiredList(CleanMap cleanMap) {
        Map<String, List<String>> output = new HashMap<>();
        List<String> list = new ArrayList<>();
        long time = cleanMap.getExpiration();
        for (UUID uuid : CMI.getInstance().getPlayerManager().getAllUsers().keySet()) {
            if (playerTimeMap.get(uuid) >= time)
                list.add(uuid.toString());
        }
        output.put("player", list);
        return output;
    }

    private void purgeCMI(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "authme unregister " + value);
    }

    private Map<String, List<String>> getMyPetExpiredList(CleanMap cleanMap) {
        if (CorePlusAPI.getDepend().MyPetEnabled()) {
            Map<String, List<String>> output = new HashMap<>();
            List<String> list = new ArrayList<>();
            long time = cleanMap.getExpiration();
            UUID uuid;
            for (MyPetPlayer myPetPlayers : MyPetApi.getPlayerManager().getMyPetPlayers()) {
                uuid = myPetPlayers.getPlayerUUID();
                if (playerTimeMap.get(uuid) >= time)
                    list.add(uuid.toString());
            }
            output.put("player", list);
            return output;
        }
        return null;
    }

    private void purgeMyPet(List<String> list) {
        String database = CorePlusAPI.getConfig().get
        for (String uuid : list) {
            CorePlusAPI.getFile().removeMySQLValue(ConfigHandler.getPlugin(), "mypet", );
        }
    }


    private void backup() {
        ServerHandler.sendConsoleMessage("&fData successfully cleaned  &a✔");
        ServerHandler.sendConsoleMessage("");
        ServerHandler.sendConsoleMessage("&f---- Statistics ----");
        for (String title : expiredMap.rowKeySet()) {
            ServerHandler.sendConsoleMessage(title + ":" + cleanCustomStatus(title));
            for (String subtitle : expiredMap.rowMap().get(title).keySet()) {
                ServerHandler.sendConsoleMessage("> " + subtitle + " - " + expiredMap.get(title, subtitle).size());
            }
            ServerHandler.sendConsoleMessage("");
        }
        if (ConfigHandler.getConfigPath().isBackupToZip()) {
            if (backupFile.exists()) {
                ServerHandler.sendConsoleMessage("&6Starting to compression the backup folder...");
                if (zipFiles(backupPath)) {
                    ServerHandler.sendConsoleMessage("&fZip successfully created &8\"&e" + backupName + ".zip&8\"  &a✔");
                } else {
                    ServerHandler.sendConsoleMessage("&fZip creation failed &8\"&e" + backupName + ".zip&8\"  &c✘");
                }
            }
        }
        if (ConfigHandler.getConfigPath().isCleanLogEnable()) {
            ServerHandler.sendConsoleMessage("");
            ServerHandler.sendConsoleMessage("&6Starting to create the log...");
            if (saveLogs(backupFile)) {
                ServerHandler.sendConsoleMessage("&fLog successfully created &8\"&elatest.log&8\"  &a✔");
            } else {
                ServerHandler.sendConsoleMessage("&fLog creation failed &8\"&elatest.log&8\"  &c✘");
            }
        }
    }

    private String cleanCustomStatus(String title) {
        long customExpiredDay = ConfigHandler.getConfigPath().getCleanExpireTimeMap().get(title);
        boolean customBackup = ConfigHandler.getConfigPath().getBackupList().contains(title);
        if (!customBackup || customExpiredDay != 0) {
            return " ("
                    + (!customBackup ? "Backup: false, " : "")
                    + (customExpiredDay != 0 ? "Expiry-Day: " + customExpiredDay : "")
                    + ")";
        } else {
            return "";
        }
    }

    private void cleanLogs() {
        File dataPath = getDataFolder("logs", "");
        List<String> dataList;
        if (dataPath != null) {
            dataList = getDataList("Logs", dataPath);
            if (dataList.isEmpty()) {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Logs", "dataList", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        } else {
            CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Logs", "dataPath", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        List<String> expiredList = getExpiredDataList("Logs", dataList, dataPath);
        if (expiredList.isEmpty()) {
            CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Logs", "expiredList", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        List<String> cleanedList = deleteFiles("Logs", null, dataPath, expiredList);
        if (!cleanedList.isEmpty()) {
            expiredMap.put("Logs", "logs", cleanedList);
        }
    }

    private void cleanPlayerdata(String title) {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            String folderTitle = title.toLowerCase();
            File dataPath = getWorldDataFolder(folderTitle, "world");
            List<String> dataList;
            if (dataPath != null) {
                dataList = getDataList(title, dataPath);
                if (dataList.isEmpty()) {
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "dataList", "return",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                int maxData = ConfigHandler.getConfigPath().getCleanMaxDataSize();
                if (maxData > 0) {
                    if (dataList.size() > maxData) {
                        dataList = dataList.subList(0, maxData);
                        restart = true;
                    }
                }
            } else {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "dataPath", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            List<String> expiredList = getExpiredUUIDDataList(title, dataList);
            if (expiredList.isEmpty()) {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredList", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            List<String> cleanedList = deleteFiles(title, null, dataPath, expiredList);
            if (!cleanedList.isEmpty()) {
                expiredMap.put(title, folderTitle, cleanedList);
            }
        } else {
            ServerHandler.sendConsoleMessage("&cYou need Vault to check the offline player's permission");
        }
    }

    private void cleanRegions() {
        List<String> dataList;
        List<String> expiredList;
        List<String> cleanedList;
        File dataPath;
        for (String worldName : ConfigHandler.getConfigPath().getCleanRegionWorlds()) {
            dataPath = getWorldDataFolder("region", worldName);
            if (dataPath != null) {
                dataList = getDataList("Regions", dataPath);
                if (dataList.isEmpty()) {
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Regions " + worldName, "dataList", "continue", "check another world",
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
                int maxData = ConfigHandler.getConfigPath().getCleanMaxDataSize();
                if (maxData > 0) {
                    if (dataList.size() > maxData) {
                        dataList = dataList.subList(0, maxData);
                        restart = true;
                    }
                }
            } else {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Regions " + worldName, "dataPath", "continue", "check another world",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            expiredList = getExpiredDataList("Regions", dataList, dataPath);
            if (expiredList.isEmpty()) {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Regions", "expiredList", "break",
                        new Throwable().getStackTrace()[0]);
                break;
            }
            List<String> unignoredList = getUnignoreRegions(worldName, expiredList);
            if (unignoredList.isEmpty()) {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Regions", "unignoredList", "break",
                        new Throwable().getStackTrace()[0]);
                break;
            }
            cleanedList = deleteFiles("Regions", worldName, dataPath, unignoredList);
            if (!cleanedList.isEmpty()) {
                expiredMap.put("Regions", worldName, cleanedList);
            }
        }
    }

    private void cleanCMI() {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            if (ConfigHandler.getDepends().CMIEnabled()) {
                Map<UUID, CMIUser> userMap = CMI.getInstance().getPlayer().getAllUsers();
                List<String> uuidList = new ArrayList<>();
                for (UUID uuid : userMap.keySet()) {
                    uuidList.add(uuid.toString());
                }
                int maxData = ConfigHandler.getConfigPath().getCleanMaxDataSize();
                if (maxData > 0) {
                    if (uuidList.size() > maxData) {
                        uuidList = uuidList.subList(0, maxData);
                        restart = true;
                    }
                }
                List<String> expiredList = getExpiredUUIDList("CMI", uuidList);
                if (!expiredList.isEmpty()) {
                    expiredMap.put("CMI", "users", expiredList);
                    for (String uuid : expiredList) {
                        try {
                            CMI.getInstance().getPlayer().removeUser(userMap.get(UUID.fromString(uuid)));
                        } catch (Exception e) {
                            ServerHandler.sendDebugTrace(e);
                        }
                    }
                }
            }
        }
    }

    private void cleanAuthMe() {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            if (ConfigHandler.getDepends().AuthMeEnabled()) {
                List<String> dataList = AuthMeApi.getInstance().getRegisteredNames();
                int maxData = ConfigHandler.getConfigPath().getCleanMaxDataSize();
                if (maxData > 0) {
                    if (dataList.size() > maxData) {
                        dataList = dataList.subList(0, maxData);
                    }
                }
                List<String> expiredList = getExpiredPlayerList("AuthMe", dataList);
                if (!expiredList.isEmpty()) {
                    expiredMap.put("AuthMe", "users", expiredList);
                    for (String user : expiredList) {
                        getServer().dispatchCommand(Bukkit.getConsoleSender(), "authme unregister " + user);
                    }
                }
            }
        }
    }

    private void cleanDiscordSRV() {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            if (ConfigHandler.getDepends().DiscordSRVEnabled()) {
                Map<String, UUID> linkedAccounts = DiscordSRV.getPlugin().getAccountLink().getLinkedAccounts();
                List<String> uuidList = new ArrayList<>();
                for (String id : linkedAccounts.keySet()) {
                    uuidList.add(linkedAccounts.get(id).toString());
                }
                int maxData = ConfigHandler.getConfigPath().getCleanMaxDataSize();
                if (maxData > 0) {
                    if (uuidList.size() > maxData) {
                        uuidList = uuidList.subList(0, maxData);
                        restart = true;
                    }
                }
                List<String> expiredList = getExpiredUUIDList("DiscordSRV", uuidList);
                if (!expiredList.isEmpty()) {
                    expiredMap.put("DiscordSRV", "users", expiredList);
                    for (String uuid : expiredList) {
                        try {
                            DiscordSRV.getPlugin().getAccountLink().unlink(UUID.fromString(uuid));
                        } catch (Exception e) {
                            ServerHandler.sendDebugTrace(e);
                        }
                    }
                }
            }
        }
    }

    private void cleanMyCommand() {
        if (ConfigHandler.getDepends().MyCommandEnabled()) {
            List<String> ignorePlayerdatas = ConfigHandler.getConfigPath().getCleanMycmdPlayers();
            List<String> ignoreVars = ConfigHandler.getConfigPath().getCleanMycmdVars();
            List<String> ignoreValues = ConfigHandler.getConfigPath().getCleanMycmdIgnore();
            String value;
            ConfigurationSection playerdatasConfig = ConfigHandler.getConfig("playerdata.yml").getConfigurationSection("");
            ConfigurationSection playerConfig;
            ConfigurationSection varConfig = ConfigHandler.getConfig("othersdb.yml").getConfigurationSection("");
            boolean uuidSet;
            if (playerdatasConfig != null) {
                ConfigHandler.getLogger().createLog("", PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\MyCommand", "playerdata.yml");
                for (String uuid : playerdatasConfig.getKeys(false)) {
                    playerConfig = ConfigHandler.getConfig("playerdata.yml").getConfigurationSection(uuid);
                    if (playerConfig != null) {
                        uuidSet = false;
                        for (String key : playerConfig.getKeys(false)) {
                            if (ignorePlayerdatas.contains(key)) {
                                value = ConfigHandler.getConfig("playerdata.yml").getString(uuid + "." + key);
                                if (ignoreValues.contains(value)) {
                                    continue;
                                }
                                if (!uuidSet) {
                                    ConfigHandler.getLogger().addLog("", uuid + ":", false);
                                    uuidSet = true;
                                }
                                ConfigHandler.getLogger().addLog("", "  " + key + ":" + value, false);
                            }
                        }
                    }
                }
                if (varConfig != null) {
                    for (String key : varConfig.getKeys(false)) {
                        if (ignoreVars.contains(key)) {
                            value = ConfigHandler.getConfig("playerdata.yml").getString(key);
                            if (ignoreValues.contains(value)) {
                                continue;
                            }
                            ConfigHandler.getLogger().addLog("", key + ": " + ConfigHandler.getConfig("playerdata.yml").getString(key), false);
                        }
                    }
                }
            }
        }
    }

    private List<String> getFileFormats(String title) {
        List<String> fileFormats = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control." + title + ".File-formats");
        switch (title) {
            case "Playerdata":
                fileFormats.add("dat");
                break;
            case "Advancements":
            case "Stats":
                fileFormats.add("json");
                break;
            case "Logs":
                fileFormats.add("log");
                fileFormats.add("gz");
                break;
        }
        return fileFormats;
    }

    private File getDataFolder(String folderTitle, String customPath) {
        String path;
        if (customPath.equals("")) {
            path = Bukkit.getWorldContainer().getPath();
        } else {
            path = Bukkit.getWorldContainer().getPath() + "\\" + customPath;
        }
        File fileDir = new File(path);
        File[] subdir = fileDir.listFiles(file -> file.isDirectory() && (file.getName().startsWith(folderTitle)));
        if (subdir != null) {
            for (File file : subdir) {
                if (file.getName().equals(folderTitle)) {
                    return file;
                }
            }
            for (File file : subdir) {
                File[] subsubdir = file.listFiles(file1 -> file1.isDirectory() && file1.getName().equals(folderTitle));
                if (subsubdir != null) {
                    if (subsubdir.length > 0) {
                        return subsubdir[0];
                    }
                }
            }
        }
        return null;
    }

    private File getWorldDataFolder(String folderTitle, String worldName) {
        File worldDir = new File(Bukkit.getWorldContainer().getPath() + "\\" + worldName);
        if (worldDir.exists()) {
            File[] subdir;
            if (folderTitle.equals("region")) {
                subdir = worldDir.listFiles(file -> file.isDirectory() && (file.getName().startsWith("DIM") || file.getName().equals("region")));
            } else {
                subdir = worldDir.listFiles(file -> file.isDirectory() && (file.getName().startsWith(folderTitle)));
            }
            if (subdir != null) {
                for (File file : subdir) {
                    if (file.getName().equals(folderTitle)) {
                        return file;
                    }
                }
                for (File file : subdir) {
                    File[] subsubdir = file.listFiles(file1 -> file1.isDirectory() && file1.getName().equals(folderTitle));
                    if (subsubdir != null) {
                        if (subsubdir.length > 0) {
                            return subsubdir[0];
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<String> getDataList(String title, File path) {
        List<String> fileFormats = getFileFormats(title);
        List<String> dataList = new ArrayList<>();
        String[] stringArray;
        if (title.equals("Regions")) {
            stringArray = path.list((dir, name) -> name.matches("r\\.-?\\d+\\.-?\\d+\\.mca"));
            if (stringArray != null) {
                Collections.addAll(dataList, stringArray);
            }
        } else {
            for (String fileFormat : fileFormats) {
                stringArray = path.list((dir, name) -> name.endsWith("." + fileFormat));
                if (stringArray != null) {
                    Collections.addAll(dataList, stringArray);
                }
            }
        }
        return dataList;
    }

    private List<String> getExpiredDataList(String title, List<String> dataList, File path) {
        List<String> expiredData = new ArrayList<>();
        Date currentDate = new Date();
        long diffDays;
        long expiredDay = ConfigHandler.getConfigPath().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
        }
        File file;
        Date lastDate;
        for (String data : dataList) {
            file = new File(path + "\\" + data);
            lastDate = new Date(file.lastModified());
            diffDays = TimeUnit.DAYS.convert(Math.abs(currentDate.getTime() - lastDate.getTime()), TimeUnit.MILLISECONDS);
            if (diffDays > expiredDay) {
                expiredData.add(data);
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredDataList", "continue", data,
                        new Throwable().getStackTrace()[0]);
            } else {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredDataList", "bypass", data,
                        new Throwable().getStackTrace()[0]);
            }
        }
        return expiredData;
    }

    private List<String> getExpiredUUIDDataList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>();
        Map<String, String> expiredMap = new HashMap<>();
        long expiredDay = ConfigHandler.getConfigPath().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
        }
        long diffDays;
        long currentTime = new Date().getTime();
        long lastTime;
        OfflinePlayer offlinePlayer;
        UUID playerUUID;
        for (String data : dataList) {
            for (String fileFormat : getFileFormats(title)) {
                if (data.endsWith("." + fileFormat)) {
                    expiredList.add(data);
                    expiredMap.put(data.replace("." + fileFormat, ""), data);
                    data = data.replace("." + fileFormat, "");
                }
            }
            try {
                playerUUID = UUID.fromString(data);
            } catch (IllegalArgumentException e) {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "bypass", data + "&f not UUID",
                        new Throwable().getStackTrace()[0]);
                expiredList.remove(expiredMap.get(data));
                break;
            }
            offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            if (offlinePlayer.getName() != null) {
                if (!PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean." + title.toLowerCase()) &&
                        !PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean.*")) {
                    if (ConfigHandler.getDepends().AuthMeEnabled()) {
                        try {
                            lastTime = Date.from(AuthMeApi.getInstance().getLastLoginTime(offlinePlayer.getName())).getTime();
                        } catch (Exception e) {
                            lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                        }
                    } else {
                        lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                    }
                    diffDays = TimeUnit.DAYS.convert(Math.abs(lastTime - currentTime), TimeUnit.MILLISECONDS);
                    if (diffDays > expiredDay) {
                        CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "remove", data,
                                new Throwable().getStackTrace()[0]);
                        continue;
                    }
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "bypass", data + "&f not expired",
                            new Throwable().getStackTrace()[0]);
                } else {
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "bypass", data + "&f has bypass permission",
                            new Throwable().getStackTrace()[0]);
                }
                expiredList.remove(expiredMap.get(data));
            }
        }
        return expiredList;
    }

    private List<String> getExpiredPlayerDataList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>();
        Map<String, String> expiredMap = new HashMap<>();
        long expiredDay = ConfigHandler.getConfigPath().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
        }
        long diffDays;
        long currentTime = new Date().getTime();
        long lastTime;
        OfflinePlayer offlinePlayer;
        for (String data : dataList) {
            for (String fileFormat : getFileFormats(title)) {
                if (data.endsWith("." + fileFormat)) {
                    expiredList.add(data);
                    expiredMap.put(data.replace("." + fileFormat, ""), data);
                    data = data.replace("." + fileFormat, "");
                }
            }
            offlinePlayer = PlayerHandler.getOfflinePlayer(data);
            if (offlinePlayer != null) {
                if (!PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean." + title.toLowerCase()) && !PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean.*")) {
                    if (ConfigHandler.getDepends().AuthMeEnabled()) {
                        try {
                            lastTime = Date.from(AuthMeApi.getInstance().getLastLoginTime(offlinePlayer.getName())).getTime();
                        } catch (Exception e) {
                            lastTime = new Date(offlinePlayer.getLastLogin().getLastPlayed()).getTime();
                        }
                    } else {
                        lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                    }
                    diffDays = TimeUnit.DAYS.convert(Math.abs(lastTime - currentTime), TimeUnit.MILLISECONDS);
                    if (diffDays > expiredDay) {
                        CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerDataList", "remove", data,
                                new Throwable().getStackTrace()[0]);
                        continue;
                    }
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerDataList", "bypass", data,
                            new Throwable().getStackTrace()[0]);
                } else {
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerDataList", "bypass", data + "&f has bypass permission",
                            new Throwable().getStackTrace()[0]);
                }
                expiredList.remove(expiredMap.get(data));
            }
        }
        return expiredList;
    }

    private List<String> getExpiredUUIDList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>(dataList);
        long expiredDay = ConfigHandler.getConfigPath().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
        }
        long diffDays;
        long currentTime = new Date().getTime();
        long lastTime;
        OfflinePlayer offlinePlayer;
        UUID playerUUID;
        for (String data : dataList) {
            try {
                playerUUID = UUID.fromString(data);
            } catch (IllegalArgumentException e) {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not UUID",
                        new Throwable().getStackTrace()[0]);
                expiredList.remove(data);
                break;
            }
            offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            if (offlinePlayer.getName() != null) {
                if (!PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean." + title.toLowerCase()) && !PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean.*")) {
                    if (ConfigHandler.getDepends().AuthMeEnabled()) {
                        try {
                            lastTime = Date.from(AuthMeApi.getInstance().getLastLoginTime(offlinePlayer.getName())).getTime();
                        } catch (Exception e) {
                            lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                        }
                    } else {
                        lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                    }
                    diffDays = TimeUnit.DAYS.convert(Math.abs(lastTime - currentTime), TimeUnit.MILLISECONDS);
                    if (diffDays > expiredDay) {
                        CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "remove", data,
                                new Throwable().getStackTrace()[0]);
                        continue;
                    }
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not expired",
                            new Throwable().getStackTrace()[0]);
                } else {
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f has bypass permission",
                            new Throwable().getStackTrace()[0]);
                }
                expiredList.remove(data);
            }
        }
        return expiredList;
    }

    private List<String> getExpiredPlayerList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>(dataList);
        long expiredDay = ConfigHandler.getConfigPath().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
        }
        long diffDays;
        long currentTime = new Date().getTime();
        long lastTime;
        OfflinePlayer offlinePlayer;
        for (String data : dataList) {
            offlinePlayer = PlayerHandler.getOfflinePlayer(data);
            if (offlinePlayer != null) {
                if (!PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean." + title.toLowerCase()) && !PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean.*")) {
                    if (ConfigHandler.getDepends().AuthMeEnabled()) {
                        try {
                            lastTime = Date.from(AuthMeApi.getInstance().getLastLoginTime(offlinePlayer.getName())).getTime();
                        } catch (Exception e) {
                            lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                        }
                    } else {
                        lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                    }
                    diffDays = TimeUnit.DAYS.convert(Math.abs(lastTime - currentTime), TimeUnit.MILLISECONDS);
                    if (diffDays > expiredDay) {
                        CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerList", "remove", data,
                                new Throwable().getStackTrace()[0]);
                        continue;
                    }
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerList", "bypass", data + "&f not expired",
                            new Throwable().getStackTrace()[0]);
                } else {
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerList", "bypass", data + "&f has bypass permission",
                            new Throwable().getStackTrace()[0]);
                }
                expiredList.remove(data);
            }
        }
        return expiredList;
    }

    private boolean getExpiredUUID(String title, String data) {
        long expiredDay = ConfigHandler.getConfigPath().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
        }
        long diffDays;
        long currentTime = new Date().getTime();
        long lastTime;
        OfflinePlayer offlinePlayer;
        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(data);
        } catch (IllegalArgumentException e) {
            CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not UUID",
                    new Throwable().getStackTrace()[0]);
            return false;
        }
        offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        if (offlinePlayer.getName() != null) {
            if (!PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean." + title.toLowerCase()) && !PermissionsHandler.hasPermissionOffline(offlinePlayer, "playerdataplus.bypass.clean.*")) {
                if (ConfigHandler.getDepends().AuthMeEnabled()) {
                    try {
                        lastTime = Date.from(AuthMeApi.getInstance().getLastLoginTime(offlinePlayer.getName())).getTime();
                    } catch (Exception e) {
                        lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                    }
                } else {
                    lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                }
                diffDays = TimeUnit.DAYS.convert(Math.abs(lastTime - currentTime), TimeUnit.MILLISECONDS);
                if (diffDays > expiredDay) {
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "remove", data,
                            new Throwable().getStackTrace()[0]);
                    return true;
                }
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not expired",
                        new Throwable().getStackTrace()[0]);
            } else {
                CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f has bypass permission",
                        new Throwable().getStackTrace()[0]);
            }
            return false;
        }
        return true;
    }

    private List<String> getUnignoreRegions(String worldName, List<String> expiredList) {
        List<String> ignoreRegionList = ConfigHandler.getConfigPath().getCleanIgnoreRegions();
        int regionHighX;
        int regionLowX;
        int regionHighZ;
        int regionLowZ;
        int resHighX;
        int resHighY;
        int resHighZ;
        int resLowX;
        int resLowZ;
        World world = Bukkit.getWorld(worldName);
        Location loc;
        String[] ignoreRegion;
        Map<String, String> ignoreMap = new HashMap<>();
        for (String ir : ignoreRegionList) {
            ignoreRegion = ir.split("\\s+");
            ignoreMap.put(ignoreRegion[0], ignoreRegion[1]);
        }
        Iterator<String> i = expiredList.iterator();
        back:
        while (i.hasNext()) {
            String region = i.next();
            region = region.replace("r.", "");
            region = region.replace(".mca", "");
            String[] regionXZ = region.split("\\.");
            if (ignoreMap.keySet().contains(worldName)) {
                if (ignoreMap.get(worldName).contains(region)) {
                    i.remove();
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Logs", "DataList", "continue",
                            new Throwable().getStackTrace()[0]);
                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Regions " + worldName + " r." + region + ".mac", "ignore-regions", "bypass",
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
            }
            if (ConfigHandler.getDepends().ResidenceEnabled()) {
                for (ClaimedResidence residence : Residence.getInstance().getResidence().getResidences().values()) {
                    if (residence.isSubzone()) {
                        continue;
                    }
                    if (!worldName.equals(residence.getWorld())) {
                        continue;
                    }
                    regionHighX = 511 + Integer.valueOf(regionXZ[0]) * 512;
                    regionLowX = Integer.valueOf(regionXZ[0]) * 512;
                    regionHighZ = 511 + Integer.valueOf(regionXZ[1]) * 512;
                    regionLowZ = Integer.valueOf(regionXZ[1]) * 512;
                    for (CuboidArea area : residence.getAreaMap().values()) {
                        resHighX = area.getHighLoc().getBlockX();
                        resHighY = area.getHighLoc().getBlockY();
                        resHighZ = area.getHighLoc().getBlockZ();
                        resLowX = area.getLowLoc().getBlockX();
                        resLowZ = area.getLowLoc().getBlockZ();
                        if (regionHighX >= resHighX && resHighX >= regionLowX || regionHighX >= resLowX && resLowX >= regionLowX) {
                            if (regionHighZ >= resHighZ && resHighZ >= regionLowZ || regionHighZ >= resLowZ && resLowZ >= regionLowZ) {
                                if (ConfigHandler.getConfigPath().isCleanRegionBypassRes()) {
                                    CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean - Regions", worldName + " r." + region + ".mac", "has-residence \"" + resHighX + "." + resHighY + "." + resHighZ + "\"", "bypass",
                                            new Throwable().getStackTrace()[0]);
                                    i.remove();
                                    continue back;
                                } else {
                                    loc = new Location(world, resHighX, resHighY, resHighZ);
                                    ClaimedResidence res = Residence.getInstance().getResidence().getByLoc(loc);
                                    if (res != null) {
                                        ResidencePermissions perms = res.getPermissions();
                                        if (perms.has("bypassclean", false)) {
                                            CorePlusAPI.getMsg().sendFeatureMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean - Regions", worldName + " r." + region + ".mac", "has-residence-bypass-flag \"" + resHighX + "." + resHighY + "." + resHighZ + "\"", "bypass",
                                                    new Throwable().getStackTrace()[0]);
                                            i.remove();
                                            continue back;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return expiredList;
    }

    private static String getBackupTimeName() {
        String timeFormat = "yyyy-MM-dd";
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        return currentDate.format(formatter);
    }

    private String getBackupPath() {
        String backupTimeName = getBackupTimeName();
        String backupMode = ConfigHandler.getConfigPath().getBackupMode();
        String backupFolderName = ConfigHandler.getConfigPath().getBackupFolderName();
        if (backupMode == null) {
            backupMode = "plugin";
            ServerHandler.sendConsoleMessage("&cThe option &8\"&eClean.Settings.Backup.Name&8\" &cis missing, using the default value \"plugin\".");
        }
        String backupPath;
        String backupCustomPath;
        switch (backupMode) {
            case "plugin":
                backupPath = PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\" + backupFolderName + "\\" + backupTimeName;
                break;
            case "custom":
                backupCustomPath = ConfigHandler.getConfigPath().getBackupCustomPath();
                if (backupCustomPath != null) {
                    backupPath = backupCustomPath + "\\" + backupFolderName + "\\" + backupTimeName;
                } else {
                    ServerHandler.sendConsoleMessage("&cThe option &8\"&eClean.Settings.Backup.Custom-Path&8\" &cis empty, using the default mode \"plugin\".");
                    backupPath = PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\Backup\\" + backupTimeName;
                }
                break;
            default:
                ServerHandler.sendConsoleMessage("&cThe option &8\"&eClean.Settings.Backup.Mode&8\" &cis empty, using the default mode \"plugin\".");
                backupPath = PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\" + backupFolderName + "\\" + backupTimeName;
                break;
        }

        String backupNewPath = backupPath;
        File zipFile = new File(backupNewPath + ".zip");
        int number = 1;
        while (zipFile.exists()) {
            backupNewPath = backupPath + "-" + number;
            zipFile = new File(backupNewPath + ".zip");
            number++;
        }
        File backupFolder = new File(backupNewPath);
        while (backupFolder.exists()) {
            backupNewPath = backupPath + "-" + number;
            backupFolder = new File(backupNewPath);
            number++;
        }
        return backupNewPath;
    }

    private boolean saveLogs(File backupFile) {
        ConfigHandler.getLogger().createLog("log", "", "");
        DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm");
        String date = dateFormat.format(new Date());
        long expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
        boolean autoClean = ConfigHandler.getConfigPath().isCleanAutoEnable();
        boolean toZip = ConfigHandler.getConfigPath().isBackupToZip();
        StringBuilder sb = new StringBuilder();
        for (String value : expiredMap.rowKeySet()) {
            sb.append(value);
        }
        String controlList = sb.toString();
        ConfigHandler.getLogger().addLog("log", "---- PlayerdataPlus Clean Log ----", false);
        ConfigHandler.getLogger().addLog("log", "", false);
        ConfigHandler.getLogger().addLog("log", "Time: " + date, false);
        if (backupFile.exists()) {
            if (toZip) {
                ConfigHandler.getLogger().addLog("log", "Backup: " + backupFile.getPath() + ".zip", false);
            } else {
                ConfigHandler.getLogger().addLog("log", "Backup: " + backupFile.getPath(), false);
            }
        } else {
            ConfigHandler.getLogger().addLog("log", "Backup: false", false);
        }
        ConfigHandler.getLogger().addLog("log", "Control-List: " + controlList, false);
        ConfigHandler.getLogger().addLog("log", "Expiry-Days: " + expiredDay, false);
        ConfigHandler.getLogger().addLog("log", "Auto-Clean: " + autoClean, false);
        ConfigHandler.getLogger().addLog("log", "", false);
        ConfigHandler.getLogger().addLog("log", "---- Statistics ----", false);
        for (String title : expiredMap.rowKeySet()) {
            ConfigHandler.getLogger().addLog("log", title + ":" + cleanCustomStatus(title), false);
            for (String subtitle : expiredMap.rowMap().get(title).keySet()) {
                ConfigHandler.getLogger().addLog("log", "> " + subtitle + " - " + expiredMap.get(title, subtitle).size(), false);
            }
            ConfigHandler.getLogger().addLog("log", "", false);
        }
        ConfigHandler.getLogger().addLog("log", "---- Details ----", false);
        for (String title : expiredMap.rowKeySet()) {
            ConfigHandler.getLogger().addLog("log", title + ":", false);
            for (String subtitle : expiredMap.rowMap().get(title).keySet()) {
                for (String value : expiredMap.get(title, subtitle)) {
                    ConfigHandler.getLogger().addLog("log", " - " + value, false);
                }
            }
            ConfigHandler.getLogger().addLog("log", "", false);
        }
        /*
        if (!zipFiles(ConfigHandler.getLogger().getFile().getPath(), backupFile.getName())) {
            ServerHandler.sendConsoleMessage("&Log: &Compression the log &8\"&e" + backupFile.getName() + "&8\"  &c✘");
        }
        */
        return true;
    }

    private List<String> deleteFiles(String title, String subtitle, File dataPath, List<String> expiredList) {
        List<String> cleanedList = new ArrayList<>();
        String titleName;
        if (subtitle != null) {
            titleName = title + "\\" + subtitle;
        } else {
            titleName = title;
        }
        if (!ConfigHandler.getConfigPath().isBackupEnable() || !ConfigHandler.getConfigPath().isBackupEnable(title)) {
            // Backup is disabled - only delete the file.
            for (String fileName : expiredList) {
                File dataFile = new File(dataPath + "\\" + fileName);
                // Backup is disabled - only delete the file.
                try {
                    // Delete the file.
                    if (dataFile.delete()) {
                        cleanedList.add(fileName);
                    } else {
                        ServerHandler.sendConsoleMessage("&6Delete: &f" + titleName + " &8\"&f" + fileName + "&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
            }
            return cleanedList;
        }
        // Backup is enabled - backup and delete the original files.
        String backupTitlePath = backupPath + "\\" + title + "\\";
        String backupSubtitlePath = null;
        if (subtitle != null) {
            backupSubtitlePath = backupTitlePath + subtitle + "\\";
        }
        File titleFolder = new File(backupTitlePath);
        File subtitleFolder = null;
        if (subtitle != null) {
            subtitleFolder = new File(backupSubtitlePath);
        }
        // Create all parent, "Backup", and "time" folder like "C:\\Server\\Playerdata_Backup\\Backup\\2020-12-16".
        if (!backupFile.exists()) {
            try {
                Path pathToFile = Paths.get(backupFile.getPath());
                Files.createDirectories(pathToFile);
                if (!backupFile.exists()) {
                    ServerHandler.sendConsoleMessage("&6Backup: &fcreate folder &8\"" + backupFile.getName() + "&8\"  &c✘");
                    return cleanedList;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Create a title folder like "Playerdata".
        if (!titleFolder.exists()) {
            try {
                if (!titleFolder.mkdir()) {
                    ServerHandler.sendConsoleMessage("&6Backup: &fcreate folder &8\"&e" + titleFolder.getName() + "&8\"  &c✘");
                }
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
        }
        // Create a subtitle folder like "world".
        if (subtitle != null) {
            if (!subtitleFolder.exists()) {
                try {
                    if (!subtitleFolder.mkdir()) {
                        ServerHandler.sendConsoleMessage("&6Backup: &fcreate folder &8\"&e" + subtitleFolder.getName() + "&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
            }
        }
        for (String fileName : expiredList) {
            File dataFile = new File(dataPath + "\\" + fileName);
            // Copy all file to the backup folder.
            try {
                if (subtitle != null) {
                    Files.copy(dataFile.toPath(), (new File(backupSubtitlePath + dataFile.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(dataFile.toPath(), (new File(backupTitlePath + dataFile.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                // Delete the original file.
                if (dataFile.delete()) {
                    // Add the file name in cleaned list.
                    cleanedList.add(fileName);
                } else {
                    ServerHandler.sendConsoleMessage("&6Backup: &fdelete the files &8\"&e" + fileName + "&8\"  &c✘");
                }
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
        }
        return cleanedList;
    }

    // After all expired regions have been deleted or backup, zip the backup files.
    private boolean zipFiles(String zipName) {
        String OUTPUT_ZIP_FILE;
        if (zipName == null || zipName.equals("")) {
            OUTPUT_ZIP_FILE = backupPath + ".zip";
        } else {
            OUTPUT_ZIP_FILE = backupFile.getParentFile().getPath() + "\\" + zipName + ".zip";
        }
        String SOURCE_FOLDER = backupPath;
        List<String> fileList = new ArrayList<>();
        generateFileList(new File(SOURCE_FOLDER), fileList, SOURCE_FOLDER);
        zipIt(OUTPUT_ZIP_FILE, SOURCE_FOLDER, fileList);
        try (Stream<Path> walk = Files.walk(backupFile.toPath())) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            ServerHandler.sendDebugTrace(e);
            return false;
        }
        return true;
    }

    private static void zipIt(String zipFile, String SOURCE_FOLDER, List<String> fileList) {
        byte[] buffer = new byte[1024];
        String source = new File(SOURCE_FOLDER).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            FileInputStream in = null;
            for (String file : fileList) {
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }
            zos.closeEntry();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void generateFileList(File node, List<String> fileList, String SOURCE_FOLDER) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString(), SOURCE_FOLDER));
        }
        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename), fileList, SOURCE_FOLDER);
            }
        }
    }

    private static String generateZipEntry(String file, String SOURCE_FOLDER) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }
}
