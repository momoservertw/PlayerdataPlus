package tw.momocraft.playerdataplus.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.utils.file.maps.FileMap;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.utils.ConfigPath;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private static final Map<String, YamlConfiguration> configMap = new HashMap<>();
    private static final Map<String, FileMap> configInfoMap = new HashMap<>();

    private static ConfigPath configPath;

    public static void generateData(boolean reload) {
        // Config
        setConfigFile();
        loadConfig("config.yml");
        loadConfig("message.yml");
        checkConfigVer("config.yml");
        checkConfigVer("message.yml");

        // Others
        setConfigPath(new ConfigPath());
        UtilsHandler.setup(reload);

        logConfigMsg();
    }

    private static void logConfigMsg() {
        CorePlusAPI.getMsg().sendConsoleMsg(
                getPluginPrefix() + "Load configurations: " + configMap.keySet());
    }

    private static void setConfigFile() {
        FileMap fileMap;
        String filePath;
        String fileName;
        // config.yml
        fileMap = new FileMap();
        filePath = PlayerdataPlus.getInstance().getDataFolder().getPath();
        fileName = "config.yml";
        fileMap.setFile(new File(filePath, fileName));
        fileMap.setFileName(fileName);
        fileMap.setFileType("yaml");
        fileMap.setVersion(4);
        configInfoMap.put(fileName, fileMap);
        // message.yml
        fileMap = new FileMap();
        filePath = PlayerdataPlus.getInstance().getDataFolder().getPath();
        fileName = "message.yml";
        fileMap.setFile(new File(filePath, fileName));
        fileMap.setFileName(fileName);
        fileMap.setFileType("yaml");
        fileMap.setVersion(1);
        configInfoMap.put(fileName, fileMap);
    }

    private static void loadConfig(String fileName) {
        File file = configInfoMap.get(fileName).getFile();
        checkResource(file, fileName);
        configMap.put(fileName, YamlConfiguration.loadConfiguration(file));
    }

    private static void checkResource(File file, String resource) {
        if (!(file).exists()) {
            try {
                PlayerdataPlus.getInstance().saveResource(resource, false);
            } catch (Exception e) {
                CorePlusAPI.getMsg().sendErrorMsg(getPluginName(),
                        "Cannot save " + resource + " to disk!");
            }
        }
    }

    public static FileConfiguration getConfig(String fileName) {
        if (configMap.get(fileName) == null)
            loadConfig(fileName);
        return configMap.get(fileName);
    }

    private static void checkConfigVer(String fileName) {
        String[] fileNameSlit = fileName.split("\\.(?=[^.]+$)");
        FileMap fileMap = configInfoMap.get(fileName);
        String filePath = fileMap.getFilePath();
        int version = fileMap.getVersion();

        loadConfig(fileName);
        File file = new File(filePath, fileName);
        if (file.exists() && getConfig(fileName).getInt("Config-Version") != version) {
            if (PlayerdataPlus.getInstance().getResource(fileName) != null) {
                LocalDateTime currentDate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                String currentTime = currentDate.format(formatter);
                String newGen = fileNameSlit[0] + " " + currentTime + "." + fileNameSlit[0];
                File newFile = new File(filePath, newGen);
                if (!newFile.exists()) {
                    file.renameTo(newFile);
                    File configFile = new File(filePath, fileName);
                    configFile.delete();
                    loadConfig(fileName);
                    CorePlusAPI.getMsg().sendConsoleMsg(getPrefix(),
                            "&4The file \"" + fileName + "\" is out of date, generating a new one!");
                }
            }
        }
        getConfig(fileName).options().copyDefaults(false);
    }

    private static void setConfigPath(ConfigPath configPaths) {
        configPath = configPaths;
    }

    public static ConfigPath getConfigPath() {
        return configPath;
    }

    public static String getPluginName() {
        return PlayerdataPlus.getInstance().getDescription().getName();
    }

    public static String getPluginPrefix() {
        return "[" + PlayerdataPlus.getInstance().getDescription().getName() + "] ";
    }

    public static String getPrefix() {
        return getConfig("message.yml").getString("Message.prefix");
    }

    public static boolean isDebug() {
        return ConfigHandler.getConfig("config.yml").getBoolean("Debugging");
    }

    public static boolean isCheckUpdates() {
        return ConfigHandler.getConfig("config.yml").getBoolean("Check-Updates");
    }
}