package tw.momocraft.playerdataplus.handlers;

import com.google.common.collect.Table;
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
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DataHandler {

    static boolean saveLogs(File backupFile, Table<String, String, List<String>> cleanTable) {
        ConfigHandler.getLogger().createLog();
        DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm");
        String date = dateFormat.format(new Date());
        long expiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Settings.Expiry-Days");
        boolean autoClean = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Auto-Clean.Enable");
        boolean toZip = ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.To-Zip");
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
        String customStatus;
        long customExpiredDay;
        boolean customBackup;
        List<String> backupAvailable = new ArrayList<>(Arrays.asList("Logs", "Playerdata", "Advancements", "Stats", "Regions"));
        for (String title : cleanTable.rowKeySet()) {
            customExpiredDay = ConfigHandler.getConfig("config.yml").getLong("Clean.Control." + title + ".Expiry-Days");
            customBackup = ConfigHandler.getEnable("Clean.Control." + title + ".Backup", true);
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
            ConfigHandler.getLogger().sendLog(title + ":" + customStatus, false);
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

    static List<String> deleteFiles(String title, String subtitle, File dataPath, List<String> expiredList, String backupPath) {
        List<String> cleanedList = new ArrayList<>();
        String titleName;
        if (subtitle != null) {
            titleName = title + "\\" + subtitle;
        } else {
            titleName = title;
        }
        if (!ConfigHandler.getConfig("config.yml").getBoolean("Clean.Settings.Backup.Enable") || !ConfigHandler.getEnable("Clean.Control." + title + ".Backup", true)) {
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
    static boolean zipFiles(String backupPath, String zipName) {
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
            for (String file: fileList) {
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
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
            for (String filename: subNote) {
                generateFileList(new File(node, filename), fileList, SOURCE_FOLDER);
            }
        }
    }

    private static String generateZipEntry(String file, String SOURCE_FOLDER) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }

    private static String getBackupTimeName() {
        String timeFormat = "yyyy-MM-dd";
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        return currentDate.format(formatter);
    }

    static String getBackupPath() {
        String backupTimeName = getBackupTimeName();
        String backupMode = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Mode");
        String backupFolderName = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Folder-Name");
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
                backupCustomPath = ConfigHandler.getConfig("config.yml").getString("Clean.Settings.Backup.Custom-Path");
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
}
