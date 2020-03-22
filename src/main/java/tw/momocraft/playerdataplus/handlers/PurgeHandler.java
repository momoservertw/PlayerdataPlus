package tw.momocraft.playerdataplus.handlers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import fr.xephi.authme.api.v3.AuthMeApi;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Location;
import tw.momocraft.playerdataplus.PlayerdataPlus;

import java.io.*;
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

public class PurgeHandler {
    private boolean restart;

    public void start(CommandSender sender, String title) {
        ConfigurationSection cleanConfig = ConfigHandler.getPlayerdataConfig().getCleanConfig();
        if (cleanConfig != null) {
            int timeoutTime = ConfigHandler.getPlayerdataConfig().getTimeoutTime();
            if (ConfigHandler.getPlayerdataConfig().isTimeoutWarning() && timeoutTime < 180) {
                ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                return;
            }
            Table<String, String, List<String>> cleanTable = HashBasedTable.create();
            String backupPath = getBackupPath();
            try {
                cleanManager(cleanTable, title, backupPath);
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            if (!cleanTable.isEmpty()) {
                try {
                    backupManager(cleanTable, backupPath);
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
                if (restart) {
                    ServerHandler.sendConsoleMessage("");
                    ServerHandler.sendConsoleMessage("&eCleanup process has not finished yet!");
                    Bukkit.getServer().dispatchCommand(sender, "playerdataplus clean " + title);
                    return;
                }
                ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                return;
            }
        }
        ServerHandler.sendConsoleMessage("&6There has no any expired data!");
        ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
    }

    public void start(CommandSender sender) {
        ConfigurationSection cleanConfig = ConfigHandler.getPlayerdataConfig().getCleanConfig();
        if (cleanConfig != null) {
            int timeoutTime = ConfigHandler.getPlayerdataConfig().getTimeoutTime();
            if (ConfigHandler.getPlayerdataConfig().isTimeoutWarning() && timeoutTime < 180) {
                ServerHandler.sendConsoleMessage("&cIf your \"timeout-time\" setting in spigot.yml is too low, it may cause the server to restart in the middle of cleaning.");
                ServerHandler.sendConsoleMessage("&cPlease set a higher number of seconds based on the number of server players, especially for the first time.");
                ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                return;
            }
            Table<String, String, List<String>> cleanTable = HashBasedTable.create();
            String backupPath = getBackupPath();
            for (String title : ConfigHandler.getPlayerdataConfig().getCleanList()) {
                try {
                    cleanManager(cleanTable, title, backupPath);
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
                if (restart) {
                    break;
                }
            }
            if (!cleanTable.isEmpty()) {
                try {
                    backupManager(cleanTable, backupPath);
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
                if (restart) {
                    ServerHandler.sendConsoleMessage("");
                    ServerHandler.sendConsoleMessage("&eCleanup process has not finished yet!");
                    Bukkit.getServer().dispatchCommand(sender, "playerdataplus clean");
                    return;
                }
                ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                return;
            }
        }
        ServerHandler.sendConsoleMessage("&6There has no any expired data!");
        ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
    }

    private void cleanManager(Table<String, String, List<String>> cleanTable, String title, String backupPath) {
        title = title.toLowerCase();
        switch (title) {
            case "logs":
                cleanLogs(cleanTable, backupPath);
                return;
            case "playerdata":
                cleanPlayerdata(cleanTable, "Playerdata", backupPath);
                return;
            case "advancements":
                cleanPlayerdata(cleanTable, "Advancements", backupPath);
                return;
            case "stats":
                cleanPlayerdata(cleanTable, "Stats", backupPath);
                return;
            case "regions":
                cleanRegions(cleanTable, backupPath);
                return;
            case "authMe":
                cleanAuthMe(cleanTable);
                return;
            case "cmi":
                cleanCMI(cleanTable);
                return;
            case "discordsrv":
                cleanDiscordSRV(cleanTable);
                return;
            case "mypet":
                cleanMyPet(cleanTable);
                return;
            default:
                break;
                /*
            case "LuckPerms":
                if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
                    if (ConfigHandler.getDepends().SkinsRestorerEnabled()) {
                        Set<UUID> userList;
                        try {
                            userList = LuckPermsProvider.get().getUserManager().getUniqueUsers().get();
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
                                LuckPermsProvider.get().getUserManager().getUser(UUID.fromString(uuid));
                            }
                        }
                    }
                }
                break;
                 */
        }
    }

    private void backupManager(Table<String, String, List<String>> cleanTable, String backupPath) {
        ServerHandler.sendConsoleMessage("&fData successfully cleaned  &a✔");
        ServerHandler.sendConsoleMessage("");
        ServerHandler.sendConsoleMessage("&f---- Statistics ----");
        File backupFile = new File(backupPath);
        String backupName = backupFile.getName();
        for (String title : cleanTable.rowKeySet()) {
            ServerHandler.sendConsoleMessage(title + ":" + cleanCustomStatus(title));
            for (String subtitle : cleanTable.rowMap().get(title).keySet()) {
                ServerHandler.sendConsoleMessage("> " + subtitle + " - " + cleanTable.get(title, subtitle).size());
            }
            ServerHandler.sendConsoleMessage("");
        }
        if (ConfigHandler.getPlayerdataConfig().isBackupToZip()) {
            File file = new File(backupPath);
            if (file.exists()) {
                ServerHandler.sendConsoleMessage("&6Starting to compression the backup folder...");
                if (zipFiles(backupPath, null)) {
                    ServerHandler.sendConsoleMessage("&fZip successfully created &8\"&e" + backupName + ".zip&8\"  &a✔");
                } else {
                    ServerHandler.sendConsoleMessage("&fZip creation failed &8\"&e" + backupName + ".zip&8\"  &c✘");
                }
            }
        }
        if (ConfigHandler.getPlayerdataConfig().isCleanLogEnable()) {
            ServerHandler.sendConsoleMessage("");
            ServerHandler.sendConsoleMessage("&6Starting to create the log...");
            if (saveLogs(backupFile, cleanTable)) {
                ServerHandler.sendConsoleMessage("&fLog successfully created &8\"&elatest.log&8\"  &a✔");
            } else {
                ServerHandler.sendConsoleMessage("&fLog creation failed &8\"&elatest.log&8\"  &c✘");
            }
        }
    }

    private String cleanCustomStatus(String title) {
        long customExpiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpireTimeMap().get(title);
        boolean customBackup = ConfigHandler.getPlayerdataConfig().getBackupList().contains(title);
        if (!customBackup || customExpiredDay != 0) {
            return " ("
                    + (!customBackup ? "Backup: false, " : "")
                    + (customExpiredDay != 0 ? "Expiry-Day: " + customExpiredDay : "")
                    + ")";
        } else {
            return "";
        }
    }

    private void cleanLogs(Table<String, String, List<String>> cleanTable, String backupPath) {
        String folderTitle = "logs";
        File dataPath = getDataFolder(folderTitle, "");
        List<String> dataList;
        if (dataPath != null) {
            dataList = getDataList("Logs", dataPath);
            if (dataList.isEmpty()) {
                ServerHandler.debugMessage("Clean", "Logs", "dataList = isEmpty", "continue");
                return;
            }
        } else {
            ServerHandler.debugMessage("Clean", "Logs", "dataPath = null", "continue");
            return;
        }
        List<String> expiredList = getExpiredDataList("Logs", dataList, dataPath);
        if (expiredList.isEmpty()) {
            ServerHandler.debugMessage("Clean", "Logs", "expiredList = isEmpty", "break");
            return;
        }
        List<String> cleanedList = deleteFiles("Logs", null, dataPath, expiredList, backupPath);
        if (!cleanedList.isEmpty()) {
            cleanTable.put("Logs", folderTitle, cleanedList);
        }
    }

    private void cleanPlayerdata(Table<String, String, List<String>> cleanTable, String title, String backupPath) {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            String folderTitle = title.toLowerCase();
            File dataPath = getWorldDataFolder(folderTitle, "world");
            List<String> dataList;
            if (dataPath != null) {
                dataList = getDataList(title, dataPath);
                if (dataList.isEmpty()) {
                    ServerHandler.debugMessage("Clean", title, "dataList = isEmpty", "break");
                    return;
                }
                int maxData = ConfigHandler.getPlayerdataConfig().getCleanMaxData();
                if (dataList.size() > maxData) {
                    dataList = dataList.subList(0, maxData);
                    restart = true;
                }
            } else {
                ServerHandler.debugMessage("Clean", title, "dataPath = null", "continue");
                return;
            }
            List<String> expiredList = getExpiredUUIDDataList(title, dataList);
            if (expiredList.isEmpty()) {
                ServerHandler.debugMessage("Clean", title, "expiredList = isEmpty", "break");
                return;
            }
            List<String> cleanedList = deleteFiles(title, null, dataPath, expiredList, backupPath);
            if (!cleanedList.isEmpty()) {
                cleanTable.put(title, folderTitle, cleanedList);
            }
        }
    }

    private void cleanRegions(Table<String, String, List<String>> cleanTable, String backupPath) {
        List<String> dataList;
        List<String> expiredList;
        List<String> cleanedList;
        File dataPath;
        for (String worldName : ConfigHandler.getPlayerdataConfig().getCleanRegionWorlds()) {
            dataPath = getWorldDataFolder("region", worldName);
            if (dataPath != null) {
                dataList = getDataList("Regions", dataPath);
                if (dataList.isEmpty()) {
                    ServerHandler.debugMessage("Clean", "Regions" + " " + worldName, "dataList = isEmpty", "continue", "check another world");
                    continue;
                }
                int maxData = ConfigHandler.getPlayerdataConfig().getCleanMaxData();
                if (dataList.size() > maxData) {
                    dataList = dataList.subList(0, maxData);
                    restart = true;
                }
            } else {
                ServerHandler.debugMessage("Clean", "Regions" + " " + worldName, "dataPath = isEmpty", "continue", "check another world");
                continue;
            }
            expiredList = getExpiredDataList("Regions", dataList, dataPath);
            if (expiredList.isEmpty()) {
                ServerHandler.debugMessage("Clean", "Regions", "expiredList = isEmpty", "break");
                break;
            }
            List<String> unignoredList = getUnignoreRegions(worldName, expiredList);
            if (unignoredList.isEmpty()) {
                ServerHandler.debugMessage("Clean", "Regions", "unignoredList = isEmpty", "break");
                break;
            }
            cleanedList = deleteFiles("Regions", worldName, dataPath, unignoredList, backupPath);
            if (!cleanedList.isEmpty()) {
                cleanTable.put("Regions", worldName, cleanedList);
            }
        }
    }

    private void cleanCMI(Table<String, String, List<String>> cleanTable) {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            if (ConfigHandler.getDepends().CMIEnabled()) {
                Map<UUID, CMIUser> userMap = CMI.getInstance().getPlayerManager().getAllUsers();
                List<String> uuidList = new ArrayList<>();
                for (UUID uuid : userMap.keySet()) {
                    uuidList.add(uuid.toString());
                }
                int maxData = ConfigHandler.getPlayerdataConfig().getCleanMaxData();
                if (uuidList.size() > maxData) {
                    uuidList = uuidList.subList(0, maxData);
                    restart = true;
                }
                List<String> expiredList = getExpiredUUIDList("CMI", uuidList);
                if (!expiredList.isEmpty()) {
                    cleanTable.put("CMI", "users", expiredList);
                    for (String uuid : expiredList) {
                        try {
                            CMI.getInstance().getPlayerManager().removeUser(userMap.get(UUID.fromString(uuid)));
                        } catch (Exception e) {
                            ServerHandler.sendDebugTrace(e);
                        }
                    }
                }
            }
        }
    }

    private void cleanAuthMe(Table<String, String, List<String>> cleanTable) {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            if (ConfigHandler.getDepends().AuthMeEnabled()) {
                List<String> dataList = AuthMeApi.getInstance().getRegisteredNames();
                int maxData = ConfigHandler.getPlayerdataConfig().getCleanMaxData();
                if (dataList.size() > maxData) {
                    dataList = dataList.subList(0, maxData);
                }
                List<String> expiredList = getExpiredPlayerList("AuthMe", dataList);
                if (!expiredList.isEmpty()) {
                    cleanTable.put("AuthMee", "users", expiredList);
                    for (String user : expiredList) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "authme unregister " + user);
                    }
                }
            }
        }
    }

    private void cleanDiscordSRV(Table<String, String, List<String>> cleanTable) {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            if (ConfigHandler.getDepends().DiscordSRVEnabled()) {
                Map<String, UUID> linkedAccounts = DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts();
                List<String> uuidList = new ArrayList<>();
                for (String id : linkedAccounts.keySet()) {
                    uuidList.add(linkedAccounts.get(id).toString());
                }
                int maxData = ConfigHandler.getPlayerdataConfig().getCleanMaxData();
                if (uuidList.size() > maxData) {
                    uuidList = uuidList.subList(0, maxData);
                    restart = true;
                }
                List<String> expiredList = getExpiredUUIDList("DiscordSRV", uuidList);
                if (!expiredList.isEmpty()) {
                    cleanTable.put("DiscordSRV", "users", expiredList);
                    for (String uuid : expiredList) {
                        try {
                            DiscordSRV.getPlugin().getAccountLinkManager().unlink(UUID.fromString(uuid));
                        } catch (Exception e) {
                            ServerHandler.sendDebugTrace(e);
                        }
                    }
                }
            }
        }
    }

    private void cleanMyPet(Table<String, String, List<String>> cleanTable) {
        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
            // Can only remove pets and still have player list need to remove.
            if (ConfigHandler.getDepends().MyPetEnabled()) {
                MyPet[] myPets = MyPetApi.getMyPetManager().getAllActiveMyPets();
                List<MyPet> myPetList = new ArrayList<>(Arrays.asList(myPets));
                int maxData = ConfigHandler.getPlayerdataConfig().getCleanMaxData();
                if (myPetList.size() > maxData) {
                    myPetList = myPetList.subList(0, maxData);
                    restart = true;
                }
                List<String> expiredPetList = new ArrayList<>();
                String uuid;
                for (MyPet pet : myPetList) {
                    uuid = pet.getOwner().getPlayerUUID().toString();
                    if (getExpiredUUID("MyPet", uuid)) {
                        try {
                            pet.removePet();
                        } catch (Exception e) {
                            ServerHandler.sendDebugTrace(e);
                        }
                        expiredPetList.add(uuid);
                    }
                }
                if (!expiredPetList.isEmpty()) {
                    cleanTable.put("MyPet", "users", expiredPetList);
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
        long expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpiryDay();
        }
        File file;
        Date lastDate;
        for (String data : dataList) {
            file = new File(path + "\\" + data);
            lastDate = new Date(file.lastModified());
            diffDays = TimeUnit.DAYS.convert(Math.abs(currentDate.getTime() - lastDate.getTime()), TimeUnit.MILLISECONDS);
            if (diffDays > expiredDay) {
                expiredData.add(data);
                ServerHandler.debugMessage("Clean", title, "ExpiredDataList", "remove", data);
            } else {
                ServerHandler.debugMessage("Clean", title, "ExpiredDataList", "bypass", data + "&f not expired");
            }
        }
        return expiredData;
    }

    private List<String> getExpiredUUIDDataList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>();
        Map<String, String> expiredMap = new HashMap<>();
        long expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpiryDay();
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
            UUID playerUUID;
            try {
                playerUUID = UUID.fromString(data);
            } catch (IllegalArgumentException e) {
                ServerHandler.debugMessage("Clean", title, "ExpiredUUIDDataList", "bypass", data + "&f not UUID");
                expiredList.remove(expiredMap.get(data));
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
                        ServerHandler.debugMessage("Clean", title, "ExpiredUUIDDataList", "remove", data);
                        continue;
                    }
                    ServerHandler.debugMessage("Clean", title, "ExpiredUUIDDataList", "bypass", data + "&f not expired");
                } else {
                    ServerHandler.debugMessage("Clean", title, "ExpiredUUIDDataList", "bypass", data + "&f has bypass permission");
                }
                expiredList.remove(expiredMap.get(data));
            }
        }
        return expiredList;
    }

    private List<String> getExpiredPlayerDataList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>();
        Map<String, String> expiredMap = new HashMap<>();
        long expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpiryDay();
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
                            lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                        }
                    } else {
                        lastTime = new Date(offlinePlayer.getLastPlayed()).getTime();
                    }
                    diffDays = TimeUnit.DAYS.convert(Math.abs(lastTime - currentTime), TimeUnit.MILLISECONDS);
                    if (diffDays > expiredDay) {
                        ServerHandler.debugMessage("Clean", title, "ExpiredPlayerDataList", "remove", data);
                        continue;
                    }
                    ServerHandler.debugMessage("Clean", title, "ExpiredPlayerDataList", "bypass", data + "&f not expired");
                } else {
                    ServerHandler.debugMessage("Clean", title, "ExpiredPlayerDataList", "bypass", data + "&f has bypass permission");
                }
                expiredList.remove(expiredMap.get(data));
            }
        }
        return expiredList;
    }

    private List<String> getExpiredUUIDList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>(dataList);
        long expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpiryDay();
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
                ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "bypass", data + "&f not UUID");
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
                        ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "remove", data);
                        continue;
                    }
                    ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "bypass", data + "&f not expired");
                } else {
                    ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "bypass", data + "&f has bypass permission");
                }
                expiredList.remove(data);
            }
        }
        return expiredList;
    }

    private List<String> getExpiredPlayerList(String title, List<String> dataList) {
        List<String> expiredList = new ArrayList<>(dataList);
        long expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpiryDay();
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
                        ServerHandler.debugMessage("Clean", title, "ExpiredPlayerList", "remove", data);
                        continue;
                    }
                    ServerHandler.debugMessage("Clean", title, "ExpiredPlayerList", "bypass", data + "&f not expired");
                } else {
                    ServerHandler.debugMessage("Clean", title, "ExpiredPlayerList", "bypass", data + "&f has bypass permission");
                }
                expiredList.remove(data);
            }
        }
        return expiredList;
    }

    private boolean getExpiredUUID(String title, String data) {
        long expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpireTimeMap().get(title);
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpiryDay();
        }
        long diffDays;
        long currentTime = new Date().getTime();
        long lastTime;
        OfflinePlayer offlinePlayer;
        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(data);
        } catch (IllegalArgumentException e) {
            ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "bypass", data + "&f not UUID");
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
                    ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "remove", data);
                    return true;
                }
                ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "bypass", data + "&f not expired");
            } else {
                ServerHandler.debugMessage("Clean", title, "ExpiredUUIDList", "bypass", data + "&f has bypass permission");
            }
            return false;
        }
        return true;
    }

    private List<String> getUnignoreRegions(String worldName, List<String> expiredList) {
        List<String> ignoreRegionList = ConfigHandler.getPlayerdataConfig().getCleanIgnoreRegions();
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
                    ServerHandler.debugMessage("Clean", "Regions " + worldName + " r." + region + ".mac", "ignore-regions", "bypass");
                    continue;
                }
            }
            if (ConfigHandler.getDepends().ResidenceEnabled()) {
                for (ClaimedResidence residence : Residence.getInstance().getResidenceManager().getResidences().values()) {
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
                                if (ConfigHandler.getPlayerdataConfig().isCleaanRegionBypassRes()) {
                                    ServerHandler.debugMessage("Clean - Regions", worldName + " r." + region + ".mac", "has-residence \"" + resHighX + "." + resHighY + "." + resHighZ + "\"", "bypass");
                                    i.remove();
                                    continue back;
                                } else {
                                    loc = new Location(world, resHighX, resHighY, resHighZ);
                                    ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(loc);
                                    if (res != null) {
                                        ResidencePermissions perms = res.getPermissions();
                                        if (perms.has("bypassclean", false)) {
                                            ServerHandler.debugMessage("Clean - Regions", worldName + " r." + region + ".mac", "has-residence-bypass-flag \"" + resHighX + "." + resHighY + "." + resHighZ + "\"", "bypass");
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

    public static String getBackupPath() {
        String backupTimeName = getBackupTimeName();
        String backupMode = ConfigHandler.getPlayerdataConfig().getBackupMode();
        String backupFolderName = ConfigHandler.getPlayerdataConfig().getBackupFolderName();
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
                backupCustomPath = ConfigHandler.getPlayerdataConfig().getBackupCustomPath();
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

        String backupName = backupPath;
        File zipFile = new File(backupName + ".zip");
        int number = 1;
        while (zipFile.exists()) {
            backupName = backupPath + "-" + number;
            zipFile = new File(backupName + ".zip");
            number++;
        }
        File backupFolder = new File(backupName);
        while (backupFolder.exists()) {
            backupName = backupPath + "-" + number;
            backupFolder = new File(backupName);
            number++;
        }
        return backupName;
    }

    private boolean saveLogs(File backupFile, Table<String, String, List<String>> cleanTable) {
        ConfigHandler.getLogger().createLog();
        DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm");
        String date = dateFormat.format(new Date());
        long expiredDay = ConfigHandler.getPlayerdataConfig().getCleanExpiryDay();
        boolean autoClean = ConfigHandler.getPlayerdataConfig().isCleanAuto();
        boolean toZip = ConfigHandler.getPlayerdataConfig().isBackupToZip();
        StringBuilder sb = new StringBuilder();
        for (String value : cleanTable.rowKeySet()) {
            sb.append(value);
        }
        String controlList = sb.toString();
        ConfigHandler.getLogger().sendLog("---- PlayerdataPlus Clean Log ----", false);
        ConfigHandler.getLogger().sendLog("", false);
        ConfigHandler.getLogger().sendLog("Time: " + date, false);
        if (backupFile.exists()) {
            if (toZip) {
                ConfigHandler.getLogger().sendLog("Backup: " + backupFile.getPath() + ".zip", false);
            } else {
                ConfigHandler.getLogger().sendLog("Backup: " + backupFile.getPath(), false);
            }
        } else {
            ConfigHandler.getLogger().sendLog("Backup: false", false);
        }
        ConfigHandler.getLogger().sendLog("Control-List: " + controlList, false);
        ConfigHandler.getLogger().sendLog("Expiry-Days: " + expiredDay, false);
        ConfigHandler.getLogger().sendLog("Auto-Clean: " + autoClean, false);
        ConfigHandler.getLogger().sendLog("", false);
        ConfigHandler.getLogger().sendLog("---- Statistics ----", false);
        for (String title : cleanTable.rowKeySet()) {
            ConfigHandler.getLogger().sendLog(title + ":" + cleanCustomStatus(title), false);
            for (String subtitle : cleanTable.rowMap().get(title).keySet()) {
                ConfigHandler.getLogger().sendLog("> " + subtitle + " - " + cleanTable.get(title, subtitle).size(), false);
            }
            ConfigHandler.getLogger().sendLog("", false);
        }
        ConfigHandler.getLogger().sendLog("---- Details ----", false);
        for (String title : cleanTable.rowKeySet()) {
            ConfigHandler.getLogger().sendLog(title + ":", false);
            for (String subtitle : cleanTable.rowMap().get(title).keySet()) {
                for (String value : cleanTable.get(title, subtitle)) {
                    ConfigHandler.getLogger().sendLog(" - " + value, false);
                }
            }
            ConfigHandler.getLogger().sendLog("", false);
        }
        /*
        if (!zipFiles(ConfigHandler.getLogger().getFile().getPath(), backupFile.getName())) {
            ServerHandler.sendConsoleMessage("&Log: &Compression the log &8\"&e" + backupFile.getName() + "&8\"  &c✘");
        }
        */
        return true;
    }

    private static List<String> deleteFiles(String title, String subtitle, File dataPath, List<String> expiredList, String backupPath) {
        List<String> cleanedList = new ArrayList<>();
        String titleName;
        if (subtitle != null) {
            titleName = title + "\\" + subtitle;
        } else {
            titleName = title;
        }
        if (!ConfigHandler.getPlayerdataConfig().isBackupEnable() || !ConfigHandler.getPlayerdataConfig().isBackupEnable(title)) {
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
        File parentFolder = new File(backupPath);
        File titleFolder = new File(backupTitlePath);
        File subtitleFolder = null;
        if (subtitle != null) {
            subtitleFolder = new File(backupSubtitlePath);
        }

        // Create all parent, "Backup", and "time" folder like "C:\\Server\\Playerdata_Backup\\Backup\\2020-12-16".
        if (!parentFolder.exists()) {
            try {
                Path pathToFile = Paths.get(parentFolder.getPath());
                Files.createDirectories(pathToFile);
                if (!parentFolder.exists()) {
                    ServerHandler.sendConsoleMessage("&6Backup: &fcreate folder &8\"" + parentFolder.getName() + "&8\"  &c✘");
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
    private static boolean zipFiles(String backupPath, String zipName) {
        File backupFile = new File(backupPath);
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
