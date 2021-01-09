package tw.momocraft.playerdataplus.utils;

import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static File logFile;
    private static File file;

    public void createLog(String type, String path, String name) {
        if (type.equals("log")) {
            path = PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\Logs";
            name = "latest.yml";
            logFile = new File(path + "\\" + name);
        } else {
            file = new File(path + "\\" + name);
        }
        File folder = new File(path);
        if (!folder.exists()) {
            try {
                if (!folder.mkdir()) {
                    ServerHandler.sendConsoleMessage("&6Log: &fcreate folder &8\"&e" + folder.getName() + "&8\"  &c✘");
                }
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
        }
        if (type.equals("log")) {
            if (!logFile.exists()) {
                try {
                    if (!logFile.createNewFile()) {
                        ServerHandler.sendConsoleMessage("&6Log: &fcreate log &8\"&e" + logFile.getName() + ".log&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
            } else {
                Date lastModified = new Date(logFile.lastModified());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String logPath = logFile.getParentFile().getPath() + "\\" + format.format(lastModified);
                File renameFile = new File(logPath + ".log");
                String logName;
                int number = 1;
                while (renameFile.exists()) {
                    logName = logPath + "-" + number;
                    renameFile = new File(logName + ".log");
                    number++;
                }
                try {
                    if (!logFile.renameTo(renameFile)) {
                        ServerHandler.sendConsoleMessage("&6Log: &frename log &8\"&e" + renameFile.getName() + "&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
                try {
                    if (!logFile.createNewFile()) {
                        ServerHandler.sendConsoleMessage("&6Log: &fcreate log &8\"&e" + logFile.getName() + ".log&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
            }
        } else {
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) {
                        ServerHandler.sendConsoleMessage("&6Log: &fcreate log &8\"&e" + file.getName() + ".log&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
            } else {
                Date lastModified = new Date(file.lastModified());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String logPath = file.getParentFile().getPath() + "\\" + format.format(lastModified);
                File renameFile = new File(logPath + ".log");
                String logName;
                int number = 1;
                while (renameFile.exists()) {
                    logName = logPath + "-" + number;
                    renameFile = new File(logName + ".log");
                    number++;
                }
                try {
                    if (!file.renameTo(renameFile)) {
                        ServerHandler.sendConsoleMessage("&6Log: &frename log &8\"&e" + renameFile.getName() + "&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
                try {
                    if (!file.createNewFile()) {
                        ServerHandler.sendConsoleMessage("&6Log: &fcreate log &8\"&e" + file.getName() + ".log&8\"  &c✘");
                    }
                } catch (Exception e) {
                    ServerHandler.sendDebugTrace(e);
                }
            }
        }
    }

    public void addLog(String type, String message, boolean time) {
        message = message + "\n";
        if (time) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String date = dateFormat.format(new Date());
            message = "[" + date + "]: " + message;
        }
        if (type.equals("log")) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
                bw.append(message);
                bw.close();
            } catch (IOException e) {
                ServerHandler.sendDebugTrace(e);
            }
        } else {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.append(message);
                bw.close();
            } catch (IOException e) {
                ServerHandler.sendDebugTrace(e);
            }
        }
    }
}
