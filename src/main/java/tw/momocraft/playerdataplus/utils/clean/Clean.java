package tw.momocraft.playerdataplus.utils.clean;

import com.Zrips.CMI.CMI;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import fr.xephi.authme.api.v3.AuthMeApi;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.handlers.UtilsHandler;
import tw.momocraft.coreplus.utils.file.MySQLMap;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Clean {
    private Map<UUID, Long> playerTimeMap;
    private Map<String, UUID> playerUUIDMap;
    private Map<UUID, String> playerNameMap;

    // Type, Group, Element
    // CMI, player, UUIDs
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
                CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanAlreadyOn());
            } else {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanToggleOn(), sender);
                start(sender, type);
            }
        } else {
            if (!starting) {
                CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanAlreadyOff());
            } else {
                starting = false;
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgCleanToggleOff(), sender);
            }
        }
    }

    public void start(CommandSender sender, String groupName) {
        starting = true;
        expiredTable = HashBasedTable.create();
        // Setup the players login time.
        setPlayersLoginTime();
        // Scanning the data.
        Map<String, CleanMap> cleanProp = ConfigHandler.getConfigPath().getCleanProp();
        if (groupName != null) {
            CleanMap cleanMap = ConfigHandler.getConfigPath().getCleanProp().get(groupName);
            if (cleanMap != null)
                scanData(cleanMap);
        } else {
            for (CleanMap cleanMap : ConfigHandler.getConfigPath().getCleanProp().values())
                scanData(cleanMap);
            for (CleanMap cleanMap : cleanProp.values())
                if (cleanMap != null)
                    scanData(cleanMap);
        }
        // Purging the data.
        if (expiredTable.isEmpty()) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgCleanEmpty(), sender);
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgCleanEnd(), sender);
            return;
        }
        // Purging the data.
        new BukkitRunnable() {
            int speed;
            Table.Cell<String, String, List<String>> cell;
            List<String> cellValue;

            @Override
            public void run() {
                if (expiredTable.isEmpty()) {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgCleanEnd(), sender);
                    cancel();
                    return;
                }
                cell = expiredTable.cellSet().stream().iterator().next();
                cellValue = cell.getValue();
                speed = (int) Math.sqrt(Bukkit.getServer().getTPS()[0]);
                if (speed >= cellValue.size()) {
                    purgeData(cell.getRowKey(), cell.getColumnKey(), cellValue.subList(0, cellValue.size()));
                    expiredTable.remove(cell.getRowKey(), cell.getColumnKey());
                } else {
                    purgeData(cell.getRowKey(), cell.getColumnKey(), cellValue.subList(0, speed));
                    expiredTable.remove(cell.getRowKey(), cell.getColumnKey());
                    expiredTable.put(cell.getRowKey(), cell.getColumnKey(),
                            cellValue.subList(speed + 1, cellValue.size()));
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            ConfigHandler.getConfigPath().getMsgCleanEnd(), sender);
                    cancel();
                }
            }
        }.runTaskTimer(PlayerdataPlus.getInstance(), 0, 20L);
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
        playerNameMap = CorePlusAPI.getUtils().invertMap(playerUUIDMap);
    }

    private void scanData(CleanMap cleanMap) {
        String groupName = cleanMap.getGroupName();
        switch (groupName) {
            case "Log":
            case "Playerdata":
            case "Statistic":
            case "Advancement":
            case "Region":
                addFile(groupName, cleanMap);
                break;
            case "AuthMe":
                addAuthMe(cleanMap);
                break;
            case "CMI":
                addCMI(cleanMap);
                break;
            case "DiscordSRV":
                addDiscordSRV(cleanMap);
                break;
            case "MyPet":
                addMyPet(cleanMap);
                break;
            case "MyCommand":
                addMyCommand(cleanMap);
                break;
            default:
                CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPlugin(),
                        "Can not clean the clean group: " + groupName);
        }
    }

    private void purgeData(String dataType, String group, List<String> list) {
        switch (dataType) {
            case "Log":
            case "Playerdata":
            case "Statistic":
            case "Advancement":
            case "Region":
                purgeFile(dataType, list);
                return;
            case "AuthMe":
                purgeAuthMe(list);
                return;
            case "CMI":
                purgeCMI(list);
                return;
            case "DiscordSRV":
                purgeDiscordSRV(list);
                return;
            case "MyPet":
                purgeMyPet(list);
                return;
            case "MyCommand":
                purgeMyCommand(list);
        }
    }

    // AuthMe
    private void addAuthMe(CleanMap cleanMap) {
        if (CorePlusAPI.getDepend().AuthMeEnabled()) {
            List<String> list = new ArrayList<>();
            long time = cleanMap.getExpiration();
            for (String value : AuthMeApi.getInstance().getRegisteredNames()) {
                if (playerTimeMap.get(playerUUIDMap.get(value)) >= time)
                    list.add(value);
            }
            expiredTable.put("AuthMe", "player", list);
        }
    }

    private void purgeAuthMe(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "authme unregister " + value);
    }

    // CMI
    private void addCMI(CleanMap cleanMap) {
        if (CorePlusAPI.getDepend().CMIEnabled()) {
            List<String> list = new ArrayList<>();
            long time = cleanMap.getExpiration();
            for (UUID value : CMI.getInstance().getPlayerManager().getAllUsers().keySet())
                if (playerTimeMap.get(value) >= time)
                    list.add(value.toString());
            expiredTable.put("CMI", "player", list);
        }
    }

    private void purgeCMI(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "cmi removeuser " + value);
    }

    // MyPet
    private void addMyPet(CleanMap cleanMap) {
        if (CorePlusAPI.getDepend().MyPetEnabled()) {
            List<String> list = new ArrayList<>();
            long time = cleanMap.getExpiration();
            UUID uuid;
            for (MyPetPlayer value : MyPetApi.getPlayerManager().getMyPetPlayers()) {
                uuid = value.getPlayerUUID();
                if (playerTimeMap.get(uuid) >= time)
                    list.add(uuid.toString());
            }
            expiredTable.put("MyPet", "player", list);
        }
    }

    private void purgeMyPet(List<String> list) {
        MySQLMap mySQLMap = CorePlusAPI.getFile().getMySQL().getMySQLProp().get("MyPet");
        String database = mySQLMap.getDatabase();
        String playersTable = mySQLMap.getTables().get("players");
        String petsTable = mySQLMap.getTables().get("pets");
        for (String value : list) {
            CorePlusAPI.getFile().getMySQL().removeValue(ConfigHandler.getPlugin(),
                    database, petsTable, "owner_uuid", value);
            CorePlusAPI.getFile().getMySQL().removeValue(ConfigHandler.getPlugin(),
                    database, playersTable, "name", playerNameMap.get(value));
        }
    }

    // DiscordSRV
    private void addDiscordSRV(CleanMap cleanMap) {
        List<String> list = new ArrayList<>();
        long time = cleanMap.getExpiration();
        for (UUID value : DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts().values()) {
            if (playerTimeMap.get(value) >= time)
                list.add(value.toString());
        }
        expiredTable.put("Discord", "player", list);
    }

    private void purgeDiscordSRV(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "discord unlink " + value);
    }

    // MyCommand
    private void addMyCommand(CleanMap cleanMap) {
        List<String> list = new ArrayList<>();
        long time = cleanMap.getExpiration();
        MySQLMap mySQLMap = CorePlusAPI.getFile().getMySQL().getMySQLProp().get("MyCommand");
        String database = mySQLMap.getDatabase();
        for (String uuid : CorePlusAPI.getFile().getMySQL().getValueList(ConfigHandler.getPlugin(),
                database, "PLAYERDATA", "PLAYER")) {
            if (playerTimeMap.get(UUID.fromString(uuid)) >= time)
                list.add(uuid);
        }
        expiredTable.put("MyCommand", "playerdata", list);
    }

    private void purgeMyCommand(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    " unlink " + value);
    }

    // Logs
    private void addLogsList(CleanMap cleanMap) {
        List<String> list = new ArrayList<>();
        long time = cleanMap.getExpiration();
        UUID uuid;
        for (MyPetPlayer myPetPlayers : MyPetApi.getPlayerManager().getMyPetPlayers()) {
            uuid = myPetPlayers.getPlayerUUID();
            if (playerTimeMap.get(uuid) >= time)
                list.add(uuid.toString());
        }
        expiredTable.put("Logs", "data", list);
    }

    private void purgeFile(String groupName, List<String> list) {
        switch (groupName) {
            case "Logs":
            case "Logs":
            case "Logs":
            case "Logs":

        }
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "discord unlink " + value);
        File dataPath = getDataFolder("logs", "");
        List<String> dataList;
        if (dataPath != null) {
            dataList = getDataList("logs", dataPath);
            if (dataList.isEmpty()) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Clean", "Logs", "dataList", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        } else {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                    "Clean", "Logs", "dataPath", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        List<String> expiredList = getExpiredDataList("Logs", dataList, dataPath);
        if (expiredList.isEmpty()) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                    "Clean", "Logs", "expiredList", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        List<String> cleanedList = deleteFiles("Logs", null, dataPath, expiredList);
        if (!cleanedList.isEmpty()) {
            expiredTable.put("Logs", "logs", cleanedList);
        }
    }

    private void purgeFile(String title, List<String> list) {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            String folderTitle = title.toLowerCase();
            File dataPath = getWorldDataFolder(folderTitle, "world");
            List<String> dataList;
            if (dataPath != null) {
                dataList = getDataList(title, dataPath);
                if (dataList.isEmpty()) {
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                            "Clean", title, "dataList", "return",
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
                private void purgePlayerdata (String title, List < String > list){
                    String folderTitle = title.toLowerCase();
                    File dataPath = getWorldDataFolder(folderTitle, "world");
                    List<String> dataList;
                    if (dataPath != null) {
                        dataList = getDataList(title, dataPath);
                        if (dataList.isEmpty()) {
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                    "Clean", title, "dataList", "return",
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
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                "Clean", title, "dataPath", "return",
                                new Throwable().getStackTrace()[0]);
                        return;
                    }
                    List<String> expiredList = getExpiredUUIDDataList(title, dataList);
                    if (expiredList.isEmpty()) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                "Clean", title, "expiredList", "return",
                                new Throwable().getStackTrace()[0]);
                        return;
                    }
                    List<String> cleanedList = deleteFiles(title, null, dataPath, expiredList);
                    if (!cleanedList.isEmpty()) {
                        expiredTable.put(title, folderTitle, cleanedList);
                    }
                }

                private void purgeRegions (List < String > list) {
                    List<String> dataList;
                    List<String> expiredList;
                    List<String> cleanedList;
                    File dataPath;
                    for (String worldName : ConfigHandler.getConfigPath().getCleanRegionWorlds()) {
                        dataPath = getWorldDataFolder("region", worldName);
                        if (dataPath != null) {
                            dataList = getDataList("Regions", dataPath);
                            if (dataList.isEmpty()) {
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                        "Clean", "Regions " + worldName, "dataList", "continue", "check another world",
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
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                    "Clean", "Regions " + worldName, "dataPath", "continue", "check another world",
                                    new Throwable().getStackTrace()[0]);
                            continue;
                        }
                        expiredList = getExpiredDataList("Regions", dataList, dataPath);
                        if (expiredList.isEmpty()) {
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                    "Clean", "Regions", "expiredList", "break",
                                    new Throwable().getStackTrace()[0]);
                            break;
                        }
                        List<String> unignoredList = getUnignoreRegions(worldName, expiredList);
                        if (unignoredList.isEmpty()) {
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                                    "Clean", "Regions", "unignoredList", "break",
                                    new Throwable().getStackTrace()[0]);
                            break;
                        }
                        cleanedList = deleteFiles("Regions", worldName, dataPath, unignoredList);
                        if (!cleanedList.isEmpty()) {
                            expiredTable.put("Regions", worldName, cleanedList);
                        }
                    }
                }

                private File getDataFolder (String folderTitle, String customPath){
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

                private File getWorldDataFolder (String folderTitle, String worldName){
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

                private List<String> getDataList (String type, File path){
                    List<String> fileFormats = new ArrayList<>();
                    switch (type) {
                        case "regions":
                            fileFormats.add("mca");
                            break;
                        case "playerdata":
                            fileFormats.add("dat");
                            break;
                        case "advancements":
                        case "stats":
                            fileFormats.add("json");
                            break;
                        case "logs":
                            fileFormats.add("log");
                            fileFormats.add("gz");
                            break;
                        default:
                            return null;
                    }
                    List<String> dataList = new ArrayList<>();
                    String[] stringArray;
                    for (String fileFormat : fileFormats) {
                        stringArray = path.list((dir, name) -> name.endsWith("." + fileFormat));
                        if (stringArray != null)
                            Collections.addAll(dataList, stringArray);
                    }
                    return dataList;
                }

                private List<String> getExpiredDataList (String title, List < String > dataList, File
                path){
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
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredDataList", "continue", data,
                                    new Throwable().getStackTrace()[0]);
                        } else {
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredDataList", "bypass", data,
                                    new Throwable().getStackTrace()[0]);
                        }
                    }
                    return expiredData;
                }

                private List<String> getExpiredUUIDDataList (String title, List < String > dataList){
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
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "bypass", data + "&f not UUID",
                                    new Throwable().getStackTrace()[0]);
                            expiredList.remove(expiredMap.get(data));
                            break;
                        }
                        offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                        if (offlinePlayer.getName() != null) {
                            if (!CorePlusAPI.getPlayer().hasPerm(offlinePlayer, "playerdataplus.bypass.clean." + title.toLowerCase()) &&
                                    !CorePlusAPI.getPlayer().hasPerm(offlinePlayer, "playerdataplus.bypass.clean.*")) {
                                if (CorePlusAPI.getDepend().AuthMeEnabled()) {
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
                                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "remove", data,
                                            new Throwable().getStackTrace()[0]);
                                    continue;
                                }
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "bypass", data + "&f not expired",
                                        new Throwable().getStackTrace()[0]);
                            } else {
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "bypass", data + "&f has bypass permission",
                                        new Throwable().getStackTrace()[0]);
                            }
                            expiredList.remove(expiredMap.get(data));
                        }
                    }
                    return expiredList;
                }

                private List<String> getExpiredPlayerDataList (String title, List < String > dataList){
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
                        offlinePlayer = CorePlusAPI.getPlayer().getOfflinePlayer(data);
                        if (offlinePlayer != null) {
                            if (!CorePlusAPI.getPlayer().hasPerm(offlinePlayer, "playerdataplus.bypass.clean." + title.toLowerCase()) &&
                                    !CorePlusAPI.getPlayer().hasPerm(offlinePlayer, "playerdataplus.bypass.clean.*")) {
                                if (CorePlusAPI.getDepend().AuthMeEnabled()) {
                                    try {
                                        lastTime = Date.from(AuthMeApi.getInstance().getLastLoginTime(offlinePlayer.getName())).getTime();
                                    } catch (Exception e) {
                                        lastTime = new Date(offlinePlayer.getLastLogin()).getTime();
                                    }
                                } else {
                                    lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                                }
                                diffDays = TimeUnit.DAYS.convert(Math.abs(lastTime - currentTime), TimeUnit.MILLISECONDS);
                                if (diffDays > expiredDay) {
                                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerDataList", "remove", data,
                                            new Throwable().getStackTrace()[0]);
                                    continue;
                                }
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerDataList", "bypass", data,
                                        new Throwable().getStackTrace()[0]);
                            } else {
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredPlayerDataList", "bypass", data + "&f has bypass permission",
                                        new Throwable().getStackTrace()[0]);
                            }
                            expiredList.remove(expiredMap.get(data));
                        }
                    }
                    return expiredList;
                }

                private List<String> getExpiredUUIDList (String title, List < String > dataList){
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
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not UUID",
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
                                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "remove", data,
                                            new Throwable().getStackTrace()[0]);
                                    continue;
                                }
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not expired",
                                        new Throwable().getStackTrace()[0]);
                            } else {
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f has bypass permission",
                                        new Throwable().getStackTrace()[0]);
                            }
                            expiredList.remove(data);
                        }
                    }
                    return expiredList;
                }


                private boolean getExpiredUUID (String title, String data){
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
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not UUID",
                                new Throwable().getStackTrace()[0]);
                        return false;
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
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "remove", data,
                                        new Throwable().getStackTrace()[0]);
                                return true;
                            }
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not expired",
                                    new Throwable().getStackTrace()[0]);
                        } else {
                            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f has bypass permission",
                                    new Throwable().getStackTrace()[0]);
                        }
                        return false;
                    }
                    return true;
                }

                private List<String> getUnignoreRegions (String worldName, List < String > expiredList){
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
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Logs", "DataList", "continue",
                                        new Throwable().getStackTrace()[0]);
                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", "Regions " + worldName + " r." + region + ".mac", "ignore-regions", "bypass",
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
                                                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean - Regions", worldName + " r." + region + ".mac", "has-residence \"" + resHighX + "." + resHighY + "." + resHighZ + "\"", "bypass",
                                                        new Throwable().getStackTrace()[0]);
                                                i.remove();
                                                continue back;
                                            } else {
                                                loc = new Location(world, resHighX, resHighY, resHighZ);
                                                ClaimedResidence res = Residence.getInstance().getResidence().getByLoc(loc);
                                                if (res != null) {
                                                    ResidencePermissions perms = res.getPermissions();
                                                    if (perms.has("bypassclean", false)) {
                                                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean - Regions", worldName + " r." + region + ".mac", "has-residence-bypass-flag \"" + resHighX + "." + resHighY + "." + resHighZ + "\"", "bypass",
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

                private String getBackupPath () {
                    String backupPath = ConfigHandler.getConfigPath().getCleanBackupPath();
                    if (backupMode == null) {
                        backupMode = "plugins";
                    }
                    CorePlusAPI.getFile().getProperty().
                            String backupCustomPath;
                    switch (backupMode) {
                        case "plugin":
                            backupPath = PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\" + backupPath;
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

                private List<String> deleteFiles (String title, String subtitle, File
                dataPath, List < String > expiredList){
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


                private void backupMsg () {
                    ServerHandler.sendConsoleMessage("&fData successfully cleaned  &a✔");
                    ServerHandler.sendConsoleMessage("");
                    ServerHandler.sendConsoleMessage("&f---- Statistics ----");
                    for (String title : expiredTable.rowKeySet()) {
                        ServerHandler.sendConsoleMessage(title + ":" + cleanCustomStatus(title));
                        for (String subtitle : expiredTable.rowMap().get(title).keySet()) {
                            ServerHandler.sendConsoleMessage("> " + subtitle + " - " + expiredTable.get(title, subtitle).size());
                        }
                        ServerHandler.sendConsoleMessage("");
                    }
                    if (log) {
                        ServerHandler.sendConsoleMessage("");
                        ServerHandler.sendConsoleMessage("&6Starting to create the log...");
                        if (saveLogs(backupFile)) {
                            ServerHandler.sendConsoleMessage("&fLog successfully created &8\"&elatest.log&8\"  &a✔");
                        } else {
                            ServerHandler.sendConsoleMessage("&fLog creation failed &8\"&elatest.log&8\"  &c✘");
                        }
                    }
                }

                private boolean saveLogs (File backupFile){
                    ConfigHandler.getLogger().createLog("log", "", "");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    String date = dateFormat.format(new Date());
                    long expiredDay = ConfigHandler.getConfigPath().getCleanExpiryDay();
                    boolean autoClean = ConfigHandler.getConfigPath().isCleanAutoEnable();
                    boolean toZip = ConfigHandler.getConfigPath().isBackupToZip();
                    StringBuilder sb = new StringBuilder();
                    for (String value : expiredTable.rowKeySet()) {
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
                    for (String title : expiredTable.rowKeySet()) {
                        ConfigHandler.getLogger().addLog("log", title + ":" + cleanCustomStatus(title), false);
                        for (String subtitle : expiredTable.rowMap().get(title).keySet()) {
                            ConfigHandler.getLogger().addLog("log", "> " + subtitle + " - " + expiredTable.get(title, subtitle).size(), false);
                        }
                        ConfigHandler.getLogger().addLog("log", "", false);
                    }
                    ConfigHandler.getLogger().addLog("log", "---- Details ----", false);
                    for (String title : expiredTable.rowKeySet()) {
                        ConfigHandler.getLogger().addLog("log", title + ":", false);
                        for (String subtitle : expiredTable.rowMap().get(title).keySet()) {
                            for (String value : expiredTable.get(title, subtitle)) {
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
            }
