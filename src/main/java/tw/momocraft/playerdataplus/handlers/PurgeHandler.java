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

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PurgeHandler {

    public void startClean(CommandSender sender) {
        ConfigurationSection cleanConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Clean.Control");
        if (cleanConfig != null) {
            int maxData = ConfigHandler.getConfig("config.yml").getInt("Clean.Settings.Max-Clean-Per-Data");
            Table<String, String, List<String>> cleanTable = HashBasedTable.create();
            String backupPath = DataHandler.getBackupPath();
            File backupFile = new File(backupPath);
            String backupName = backupFile.getName();
            boolean restart = false;
            for (String title : cleanConfig.getKeys(false)) {
                if (!ConfigHandler.isEnable("Clean.Control." + title + ".Enable", true)) {
                    continue;
                }
                String folderTitle;
                File dataPath;
                List<String> dataList;
                List<String> expiredList;
                List<String> unignoredList;
                List<String> cleanedList;
                switch (title) {
                    case "Logs":
                        folderTitle = title.toLowerCase();
                        dataPath = getDataFolder(folderTitle, "");
                        if (dataPath != null) {
                            dataList = getDataList(title, dataPath);
                            if (dataList.isEmpty()) {
                                ServerHandler.debugMessage("Clean", title, "dataList = isEmpty", "continue");
                                continue;
                            }
                        } else {
                            ServerHandler.debugMessage("Clean", title, "dataPath = null", "continue");
                            continue;
                        }
                        expiredList = getExpiredDataList(title, dataList, dataPath);
                        if (expiredList.isEmpty()) {
                            ServerHandler.debugMessage("Clean", title, "expiredList = isEmpty", "break");
                            break;
                        }
                        cleanedList = DataHandler.deleteFiles(title, null, dataPath, expiredList, backupPath);
                        if (!cleanedList.isEmpty()) {
                            cleanTable.put(title, folderTitle, cleanedList);
                        }
                        break;
                    case "Playerdata":
                    case "Advancements":
                    case "Stats":
                        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
                            folderTitle = title.toLowerCase();
                            dataPath = getWorldDataFolder(folderTitle, "world");
                            if (dataPath != null) {
                                dataList = getDataList(title, dataPath);
                                if (dataList.isEmpty()) {
                                    ServerHandler.debugMessage("Clean", title, "dataList = isEmpty", "break");
                                    break;
                                }
                                if (dataList.size() > maxData) {
                                    dataList = dataList.subList(0, maxData);
                                    restart = true;
                                }
                            } else {
                                ServerHandler.debugMessage("Clean", title, "dataPath = null", "continue");
                                break;
                            }
                            expiredList = getExpiredUUIDDataList(title, dataList);
                            if (expiredList.isEmpty()) {
                                ServerHandler.debugMessage("Clean", title, "expiredList = isEmpty", "break");
                                break;
                            }
                            cleanedList = DataHandler.deleteFiles(title, null, dataPath, expiredList, backupPath);
                            if (!cleanedList.isEmpty()) {
                                cleanTable.put(title, folderTitle, cleanedList);
                            }
                        }
                        break;
                    case "Regions":
                        List<String> worldList = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.Regions.Worlds");
                        for (String worldName : worldList) {
                            dataPath = getWorldDataFolder("region", worldName);
                            if (dataPath != null) {
                                dataList = getDataList(title, dataPath);
                                if (dataList.isEmpty()) {
                                    ServerHandler.debugMessage("Clean", title + " " + worldName, "dataList = isEmpty", "continue", "check another world");
                                    continue;
                                }
                                if (dataList.size() > maxData) {
                                    dataList = dataList.subList(0, maxData);
                                    restart = true;
                                }
                            } else {
                                ServerHandler.debugMessage("Clean", title + " " + worldName, "dataPath = isEmpty", "continue", "check another world");
                                continue;
                            }
                            expiredList = getExpiredDataList(title, dataList, dataPath);
                            if (expiredList.isEmpty()) {
                                ServerHandler.debugMessage("Clean", title, "expiredList = isEmpty", "break");
                                break;
                            }
                            unignoredList = getUnignoreRegions(worldName, expiredList);
                            if (unignoredList.isEmpty()) {
                                ServerHandler.debugMessage("Clean", title, "unignoredList = isEmpty", "break");
                                break;
                            }
                            cleanedList = DataHandler.deleteFiles(title, worldName, dataPath, unignoredList, backupPath);
                            if (!cleanedList.isEmpty()) {
                                cleanTable.put(title, worldName, cleanedList);
                            }
                        }
                        break;
                    case "AuthMe":
                        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
                            if (ConfigHandler.getDepends().AuthMeEnabled()) {
                                dataList = AuthMeApi.getInstance().getRegisteredNames();
                                if (dataList.size() > maxData) {
                                    dataList = dataList.subList(0, maxData);
                                }
                                expiredList = getExpiredPlayerList(title, dataList);
                                if (!expiredList.isEmpty()) {
                                    cleanTable.put(title, "users", expiredList);
                                    for (String user : expiredList) {
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "authme unregister " + user);
                                    }
                                }
                            }
                        }
                        break;
                    case "CMI":
                        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
                            if (ConfigHandler.getDepends().CMIEnabled()) {
                                Map<UUID, CMIUser> userMap = CMI.getInstance().getPlayerManager().getAllUsers();
                                List<String> uuidList = new ArrayList<>();
                                for (UUID uuid : userMap.keySet()) {
                                    uuidList.add(uuid.toString());
                                }
                                if (uuidList.size() > maxData) {
                                    uuidList = uuidList.subList(0, maxData);
                                    restart = true;
                                }
                                expiredList = getExpiredUUIDList(title, uuidList);
                                if (!expiredList.isEmpty()) {
                                    cleanTable.put(title, "users", expiredList);
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
                        break;
                    case "DiscordSRV":
                        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
                            if (ConfigHandler.getDepends().DiscordSRVEnabled()) {
                                Map<String, UUID> linkedAccounts = DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccounts();
                                List<String> uuidList = new ArrayList<>();
                                for (String id : linkedAccounts.keySet()) {
                                    uuidList.add(linkedAccounts.get(id).toString());
                                }
                                if (uuidList.size() > maxData) {
                                    uuidList = uuidList.subList(0, maxData);
                                    restart = true;
                                }
                                expiredList = getExpiredUUIDList(title, uuidList);
                                if (!expiredList.isEmpty()) {
                                    cleanTable.put(title, "users", expiredList);
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
                        break;
                    case "MyPet":
                        if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
                            // Can only remove pets and still have player list need to remove.
                            if (ConfigHandler.getDepends().MyPetEnabled()) {
                                MyPet[] myPets = MyPetApi.getMyPetManager().getAllActiveMyPets();
                                List<MyPet> myPetList = new ArrayList<>(Arrays.asList(myPets));
                                if (myPetList.size() > maxData) {
                                    myPetList = myPetList.subList(0, maxData);
                                    restart = true;
                                }
                                List<String> expiredPetList = new ArrayList<>();
                                String uuid;
                                for (MyPet pet : myPetList) {
                                    uuid = pet.getOwner().getPlayerUUID().toString();
                                    if (getExpiredUUID(title, uuid)) {
                                        try {
                                            pet.removePet();
                                        } catch (Exception e) {
                                            ServerHandler.sendDebugTrace(e);
                                        }
                                        expiredPetList.add(uuid);
                                    }
                                }
                                if (!expiredPetList.isEmpty()) {
                                    cleanTable.put(title, "users", expiredPetList);
                                }
                            }
                        }
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
                        break;*/
                    default:
                        break;
                }
            }
            if (!cleanTable.isEmpty()) {
                ServerHandler.sendConsoleMessage("&fData successfully cleaned  &a✔");
                ServerHandler.sendConsoleMessage("");
                ServerHandler.sendConsoleMessage("&f---- Statistics ----");
                String customStatus;
                long customExpiredDay;
                boolean customBackup;
                List<String> backupAvailable = new ArrayList<>(Arrays.asList("Logs", "Playerdata", "Advancements", "Stats", "Regions"));
                for (String title : cleanTable.rowKeySet()) {
                    customExpiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
                    customBackup = ConfigHandler.isEnable("Clean.Control." + title + ".Backup", true);
                    if (!backupAvailable.contains(title)) {
                        customBackup = false;
                    }
                    if (!customBackup || customExpiredDay != 0) {
                        customStatus = " ("
                                + (!customBackup ? "Backup: false, " : "")
                                + (customExpiredDay != 0 ? "Expiry-Day: " + customExpiredDay : "")
                                + ")";
                    } else {
                        customStatus = "";
                    }
                    ServerHandler.sendConsoleMessage(title + ":" + customStatus);
                    for (String subtitle : cleanTable.rowMap().get(title).keySet()) {
                        ServerHandler.sendConsoleMessage("> " + subtitle + " - " + cleanTable.get(title, subtitle).size());
                    }
                    ServerHandler.sendConsoleMessage("");
                }
                if (ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.To-Zip")) {
                    File file = new File(backupPath);
                    if (file.exists()) {
                        ServerHandler.sendConsoleMessage("&6Starting to compression the backup folder...");
                        if (DataHandler.zipFiles(backupPath, null)) {
                            ServerHandler.sendConsoleMessage("&fZip successfully created &8\"&e" + backupName + ".zip&8\"  &a✔");
                        } else {
                            ServerHandler.sendConsoleMessage("&fZip creation failed &8\"&e" + backupName + ".zip&8\"  &c✘");
                        }
                    }
                }
                if (ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Log")) {
                    ServerHandler.sendConsoleMessage("");
                    ServerHandler.sendConsoleMessage("&6Starting to create the log...");
                    if (DataHandler.saveLogs(backupFile, cleanTable)) {
                        ServerHandler.sendConsoleMessage("&fLog successfully created &8\"&elatest.log&8\"  &a✔");
                    } else {
                        ServerHandler.sendConsoleMessage("&fLog creation failed &8\"&elatest.log&8\"  &c✘");
                    }
                }
                if (restart) {
                    ServerHandler.sendConsoleMessage("&eCleanup process has not finished yet!");
                    Bukkit.getServer().dispatchCommand(sender, "playerdataplus clean");
                } else {
                    ServerHandler.sendConsoleMessage("&6Cleanup process has ended.");
                }
                return;
            }
            ServerHandler.sendConsoleMessage("&6There has no any expired data!");
        }
    }

    private List<String> getFileFormats(String title) {
        List<String> fileFormats = new ArrayList<>(ConfigHandler.getConfig("config.yml").getStringList("Clean.Control." + title + ".File-formats"));
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
        long expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
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
        long expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
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
        long expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
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
        long expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
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
        long expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
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
        long expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
        if (expiredDay == 0) {
            expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
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
        List<String> ignoreRegionList = ConfigHandler.getConfig("config.yml").getStringList("Clean.Control.Regions.Ignore-Regions");
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
                                if (ConfigHandler.getConfig("config.yml").getBoolean("Clean.Control.Regions.Residence-Bypass")) {
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
}
