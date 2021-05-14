package tw.momocraft.playerdataplus.utils.clean;

import com.Zrips.CMI.CMI;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import fr.xephi.authme.api.v3.AuthMeApi;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.handlers.UtilsHandler;
import tw.momocraft.coreplus.utils.file.MySQLMap;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Clean {
    private Map<UUID, Long> playerTimeMap;
    private Map<String, UUID> playerUUIDMap;
    private Map<UUID, String> playerNameMap;
    private Map<String, List<String>> residenceRegionMap;

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
            CleanMap cleanMap = cleanProp.get(groupName);
            if (cleanMap != null) {
                scanData(cleanMap);
                if (cleanMap.getGroupName().equals("Region"))
                    setResidenceRegionMap();
            }
        } else {
            for (CleanMap cleanMap : cleanProp.values())
                if (cleanMap != null) {
                    scanData(cleanMap);
                    if (cleanMap.getGroupName().equals("Region"))
                        setResidenceRegionMap();
                }
        }
        // Adding the Logs title.
        logTitle();
        // Purging the data.
        if (expiredTable.isEmpty()) {
            logDataEmpty();
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    ConfigHandler.getConfigPath().getMsgCleanEmpty(), sender);
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
                    logData(cell.getRowKey(), cell.getColumnKey(), cellValue.subList(0, cellValue.size()));
                    purgeData(cell.getRowKey(), cell.getColumnKey(), cellValue.subList(0, cellValue.size()));
                    expiredTable.remove(cell.getRowKey(), cell.getColumnKey());
                } else {
                    logData(cell.getRowKey(), cell.getColumnKey(), cellValue.subList(0, cellValue.size()));
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

    private void setResidenceRegionMap() {
        if (CorePlusAPI.getDepend().ResidenceEnabled())
            return;
        String worldName;
        int regionHighX;
        int regionHighZ;
        int regionLowX;
        int regionLowZ;
        Map<String, Map<Integer, Integer>> resRegionMap = new HashMap<>();
        for (ClaimedResidence residence : Residence.getInstance().getResidenceManager().getResidences().values()) {
            if (residence.isSubzone())
                continue;
            worldName = residence.getWorld();
            for (CuboidArea area : residence.getAreaArray()) {
                regionHighX = area.getHighLoc().getChunk().getX() >> 5;
                regionHighZ = area.getHighLoc().getChunk().getZ() >> 5;
                regionLowX = area.getLowLoc().getChunk().getX() >> 5;
                regionLowZ = area.getLowLoc().getChunk().getZ() >> 5;
                for (int x = regionLowX; x <= regionHighX; x++)
                    for (int z = regionLowZ; z <= regionHighZ; z++)
                        try {
                            resRegionMap.get(worldName).put(x, z);
                        } catch (Exception ex) {
                            resRegionMap.put(worldName, new HashMap<>());
                            resRegionMap.get(worldName).put(x, z);
                        }
            }
        }
        Map<Integer, Integer> xzMap;
        for (String world : resRegionMap.keySet()) {
            xzMap = resRegionMap.get(world);
            for (int x : xzMap.keySet())
                try {
                    // r.-1.0.mca
                    residenceRegionMap.get(world).add("r." + x + "." + xzMap.get(x) + ".mca");
                } catch (Exception ex) {
                    residenceRegionMap.put(world, new ArrayList<>());
                    residenceRegionMap.get(world).add("r." + x + "." + xzMap.get(x) + ".mca");
                }
        }
    }

    private void scanData(CleanMap cleanMap) {
        String groupName = cleanMap.getGroupName();
        switch (groupName) {
            case "Log":
                addFiles(groupName, cleanMap);
                break;
            case "Region":
                addRegions(groupName, cleanMap);
                break;
            case "Playerdata":
            case "Statistic":
            case "Advancement":
                addPlayerdata(groupName, cleanMap);
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

    // ░░░░░░░░░░ Clean Logs - 2021/05/20 18:00 ░░░░░░░░░░
    // ➤ Region
    // Expiration: 90, Backup: true, Residence-Bypass: true
    // Ignore-List: world(8.8), world(1.-1)
    // ➤ Playerdata
    // Expiration: 90, Backup: true
    //
    // 【 Region - Data 】
    // - r.-1.0.mca
    // - r.0.-1.mca
    private void logTitle() {
        if (!ConfigHandler.getConfigPath().isCleanLog())
            return;
        Map<String, CleanMap> cleanProp = ConfigHandler.getConfigPath().getCleanProp();
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                "PlayerdataPlus", "░░░░░░░░░░ Clean Logs - " + date + " ░░░░░░░░░░");

        CleanMap cleanMap;
        for (String dataType : expiredTable.rowKeySet()) {
            cleanMap = cleanProp.get(dataType);

            CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                    "PlayerdataPlus", "➤ " + dataType);
            CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                    "PlayerdataPlus",
                    "Expiration: " + cleanMap.getExpiration() + ", " +
                            "Backup: " + cleanMap.isBackup());
            CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                    "PlayerdataPlus", "Ignore-List: " + cleanMap.getIgnoreList());

            if (dataType.equals("Region")) {
                CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                        "PlayerdataPlus",
                        "Residence-Bypass: " + ConfigHandler.getConfigPath().isCleanResBypass());
            }
            if (dataType.equals("MyCommand")) {
                CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                        "PlayerdataPlus",
                        "Playerdata-List: " + ConfigHandler.getConfigPath().getCleanMycmdList());
                CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                        "PlayerdataPlus",
                        "Playerdata-Ignore-List: " + ConfigHandler.getConfigPath().getCleanMycmdIgnoreList());
            }
        }
    }

    private void logDataEmpty() {
        if (!ConfigHandler.getConfigPath().isCleanLog())
            return;
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                "PlayerdataPlus", "【 No Data 】");
    }

    private void logData(String dataType, String group, List<String> list) {
        if (!ConfigHandler.getConfigPath().isCleanLog())
            return;
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                "PlayerdataPlus", "【 " + dataType + " - " + group + " 】");
        for (String value : list)
            CorePlusAPI.getFile().getLog().add(ConfigHandler.getPlugin(),
                    "PlayerdataPlus", " - " + value);
    }

    private void purgeData(String dataType, String group, List<String> list) {
        switch (dataType) {
            case "Log":
            case "Playerdata":
            case "Statistic":
            case "Advancement":
            case "Region":
                purgeFile(dataType, group, list);
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
            int expiredTime = cleanMap.getExpiration();
            String uuid;
            for (String playerName : AuthMeApi.getInstance().getRegisteredNames()) {
                uuid = playerUUIDMap.get(playerName).toString();
                if (isPlayerExpired("authme", uuid, expiredTime))
                    list.add(uuid);
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
            int expiredTime = cleanMap.getExpiration();
            for (UUID uuid : CMI.getInstance().getPlayerManager().getAllUsers().keySet()) {
                if (isPlayerExpired("mypet", uuid.toString(), expiredTime))
                    list.add(uuid.toString());
            }
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
            int expiredTime = cleanMap.getExpiration();
            String uuidString;
            for (MyPetPlayer petPlayer : MyPetApi.getPlayerManager().getMyPetPlayers()) {
                uuidString = petPlayer.getPlayerUUID().toString();
                if (isPlayerExpired("mypet", uuidString, expiredTime))
                    list.add(uuidString);
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
        int expiredTime = cleanMap.getExpiration();
        for (UUID uuid : DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts().values())
            if (isPlayerExpired("mycommand", uuid.toString(), expiredTime))
                list.add(uuid.toString());
        expiredTable.put("Discord", "player", list);
    }

    private void purgeDiscordSRV(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    "discord unlink " + value);
    }

    // MyCommand
    private void addMyCommand(CleanMap cleanMap) {
        MySQLMap mySQLMap = CorePlusAPI.getFile().getMySQL().getMySQLProp().get("MyCommand");
        String database = mySQLMap.getDatabase();
        List<String> list = new ArrayList<>();
        int time = cleanMap.getExpiration();
        for (String uuid : CorePlusAPI.getFile().getMySQL().getValueList(ConfigHandler.getPlugin(),
                database, "PLAYERDATA", "PLAYER"))
            if (isPlayerExpired("mycommand", uuid, time))
                list.add(uuid);
        expiredTable.put("MyCommand", "player", list);
    }

    private void purgeMyCommand(List<String> list) {
        for (String value : list)
            CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPlugin(),
                    " unlink " + value);
    }

    // Files
    private void addPlayerdata(String dataType, CleanMap cleanMap) {
        String dataFolderName;
        switch (dataType) {
            case "Playerdata":
                dataFolderName = "playerdata";
                break;
            case "Statistic":
                dataFolderName = "stats";
                break;
            case "Advancement":
                dataFolderName = "advancements";
                break;
            default:
                return;
        }
        List<String> list = new ArrayList<>();
        int time = cleanMap.getExpiration();
        List<String> dataList;
        File file;
        for (World world : Bukkit.getServer().getWorlds()) {
            file = CorePlusAPI.getFile().getData().getWorldDataFolder(dataFolderName, world.getName());
            dataList = getWorldDataList(dataFolderName, file);
            if (dataList == null)
                continue;
            for (String dataName : dataList)
                if (isPlayerExpired(dataType, dataName.substring(0, dataName.lastIndexOf(".")), time))
                    list.add(dataName);
            expiredTable.put(dataType, file.getAbsolutePath(), list);
        }
    }

    private void addFiles(String dataType, CleanMap cleanMap) {
        String dataFolderName;
        switch (dataType) {
            case "Log":
                dataFolderName = "logs";
                break;
            default:
                return;
        }
        List<String> list = new ArrayList<>();
        int time = cleanMap.getExpiration();
        List<String> dataList;
        File file;
        for (World world : Bukkit.getServer().getWorlds()) {
            file = CorePlusAPI.getFile().getData().getWorldDataFolder(dataFolderName, world.getName());
            dataList = getWorldDataList(dataFolderName, file);
            if (dataList == null)
                continue;
            for (String dataName : dataList)
                if (isDataExpired(new File(file, dataName), time))
                    list.add(dataName);
            expiredTable.put(dataType, file.getAbsolutePath(), list);
        }
    }

    private void addRegions(String dataType, CleanMap cleanMap) {
        List<String> list = new ArrayList<>();
        int time = cleanMap.getExpiration();
        List<String> dataList;
        File file;
        for (World world : Bukkit.getServer().getWorlds()) {
            file = CorePlusAPI.getFile().getData().getWorldDataFolder("region", world.getName());
            dataList = getWorldDataList("region", file);
            if (dataList == null)
                continue;
            for (String dataName : dataList)
                if (isDataExpired(new File(file, dataName), time))
                    if (!residenceRegionMap.get(world.getName()).contains(dataName))
                        list.add(dataName);
            expiredTable.put(dataType, file.getAbsolutePath(), list);
        }
    }

    private void purgeFile(String dataType, String group, List<String> list) {
        for (String value : list) {
            CorePlusAPI.getFile().getData().delete(ConfigHandler.getPlugin(),
                    new File(group, value));
        }
    }

    private List<String> getWorldDataList(String type, File path) {
        List<String> fileExtensions = new ArrayList<>();
        switch (type) {
            case "region":
                fileExtensions.add("mca");
                break;
            case "playerdata":
                fileExtensions.add("dat");
                break;
            case "advancements":
            case "stats":
                fileExtensions.add("json");
                break;
            case "logs":
                fileExtensions.add("log");
                fileExtensions.add("gz");
                break;
            default:
                return null;
        }
        List<String> dataList = new ArrayList<>();
        String[] fileNames;
        for (String extension : fileExtensions) {
            fileNames = path.list((dir, name) -> name.endsWith("." + extension));
            if (fileNames != null)
                Collections.addAll(dataList, fileNames);
        }
        return dataList;
    }

    private boolean isPlayerExpired(String group, String uuid, int time) {
        return playerTimeMap.get(UUID.fromString(uuid)) < time ||
                CorePlusAPI.getPlayer().hasPerm(CorePlusAPI.getPlayer().getOfflinePlayer(uuid),
                        "playerdataplus.bypass.clean." + group);
    }

    private boolean isDataExpired(File file, int time) {
        return TimeUnit.DAYS.convert(
                Math.abs(new Date().getTime() - new Date(file.lastModified()).getTime())
                , TimeUnit.MILLISECONDS)
                > time;
    }
}
