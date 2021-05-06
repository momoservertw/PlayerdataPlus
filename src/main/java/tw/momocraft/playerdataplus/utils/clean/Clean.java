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

    public void start(CommandSender sender, String title) {
        starting = true;
        setPlayersLoginTime();
        expiredTable = HashBasedTable.create();
        // Scanning the data.
        if (title != null) {
            CleanMap cleanMap = ConfigHandler.getConfigPath().getCleanProp().get(title);
            if (cleanMap != null)
                scanData(cleanMap);
        } else {
            for (CleanMap cleanMap : ConfigHandler.getConfigPath().getCleanProp().values())
                scanData(cleanMap);
        }
        // Purging the data.
        if (expiredTable.isEmpty()) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgCleanEnd(), sender);
            return;
        }
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
        String title = cleanMap.getGroupName();
        switch (title) {
            case "logs":
                expiredMap = addAuthMe(cleanMap);
                break;
            case "playerdata":
                expiredMap = addAuthMe(cleanMap);
                break;
            case "advancements":
                expiredMap = addAuthMe(cleanMap);
                break;
            case "stats":
                expiredMap = addAuthMe(cleanMap);
                break;
            case "regions":
                expiredMap = addAuthMe(cleanMap);
                break;
            case "authme":
                expiredMap = addAuthMe(cleanMap);
                break;
            case "cmi":
                expiredMap = addCMI(cleanMap);
                break;
            case "discordsrv":
                expiredMap = addDiscordSRV(cleanMap);
                break;
            case "mypet":
                expiredMap = addMyPet(cleanMap);
                break;
            case "mycommand":
                expiredMap = addMyCommand(cleanMap);
                break;
            default:
                CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPlugin(),
                        "Can not clean the clean group: " + title);
                return;
        }
    }

    private void purgeData(String title, String group, List<String> list) {
        title = title.toLowerCase();
        switch (title) {
            case "logs":
            case "playerdata":
            case "stats":
            case "advancements":
            case "regions":
                purgeFile(title, list);
                return;
            case "authme":
                purgeAuthMe(list);
                return;
            case "cmi":
                purgeCMI(list);
                return;
            case "discordsrv":
                purgeDiscordSRV(list);
                return;
            case "mypet":
                purgeMyPet(list);
                return;
            case "mycommand":
                purgeMyCommand(group, list);
        }
    }

    // AuthMe
    private void addAuthMe(CleanMap cleanMap) {
        List<String> list = new ArrayList<>();
        long time = cleanMap.getExpiration();
        for (String name : AuthMeApi.getInstance().getRegisteredNames()) {
            if (playerTimeMap.get(playerUUIDMap.get(name)) >= time)
                list.add(name);
        }
        expiredTable.put("AuthMe", "player", list);
    }

    private void purgeAuthMe(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "authme unregister " + value);
    }

    // CMI
    private void addCMI(CleanMap cleanMap) {
        List<String> list = new ArrayList<>();
        long time = cleanMap.getExpiration();
        for (UUID uuid : CMI.getInstance().getPlayerManager().getAllUsers().keySet()) {
            if (playerTimeMap.get(uuid) >= time)
                list.add(uuid.toString());
        }
        expiredTable.put("CMI", "player", list);
    }

    private void purgeCMI(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "authme unregister " + value);
    }

    // MyPet
    private void addMyPet(CleanMap cleanMap) {
        if (CorePlusAPI.getDepend().MyPetEnabled()) {
            List<String> list = new ArrayList<>();
            long time = cleanMap.getExpiration();
            UUID uuid;
            for (MyPetPlayer myPetPlayers : MyPetApi.getPlayerManager().getMyPetPlayers()) {
                uuid = myPetPlayers.getPlayerUUID();
                if (playerTimeMap.get(uuid) >= time)
                    list.add(uuid.toString());
            }
            expiredTable.put("MyPet", "player", list);
        }
    }

    private void purgeMyPet(List<String> list) {
        MySQLMap mySQLMap = CorePlusAPI.getFile().getMySQLProp().get("mypet");
        String database = mySQLMap.getDatabase();
        String playersTable = mySQLMap.getTables().get("players");
        String petsTable = mySQLMap.getTables().get("pets");
        for (String uuid : list) {
            CorePlusAPI.getFile().removeMySQLValue(ConfigHandler.getPlugin(),
                    database, petsTable, "owner_uuid", uuid);
            CorePlusAPI.getFile().removeMySQLValue(ConfigHandler.getPlugin(),
                    database, playersTable, "name", playerNameMap.get(uuid));
        }
    }

    // DiscordSRV
    private List<UUID> addDiscordSRV() {
        List<String> list = new ArrayList<>(DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts().values()));
        expiredTable.put("MyPet", "player", list);
    }

    private void purgeDiscordSRV(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "discord unlink " + value);
    }

    // MyCommand
    private List<UUID> addMyCommand() {
        return new ArrayList<>(DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts().values());
    }

    private void purgeMyCommand(String group, List<String> list) {
        String value;
        boolean uuidSet;
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

    private void purgeLogs(List<String> list) {
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
            expiredMap.put("Logs", "logs", cleanedList);
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
                expiredMap.put(title, folderTitle, cleanedList);
            }
        } else {
            ServerHandler.sendConsoleMessage("&cYou need Vault to check the offline player's permission");
        }
    }

    private void purgeRegions(List<String> list) {
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
                expiredMap.put("Regions", worldName, cleanedList);
            }
        }
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

    private List<String> getDataList(String type, File path) {
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
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredDataList", "continue", data,
                        new Throwable().getStackTrace()[0]);
            } else {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredDataList", "bypass", data,
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
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "expiredUUIDDataList", "bypass", data + "&f not UUID",
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
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(), "Clean", title, "ExpiredUUIDList", "bypass", data + "&f not UUID",
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

}
