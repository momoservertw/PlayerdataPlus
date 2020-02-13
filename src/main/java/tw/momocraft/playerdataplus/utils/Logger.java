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
    private static File file;

    public Logger() {
        file = new File(PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\Logs" + "\\latest.log");
    }

    public void createLog() {
        File logFolder = new File(PlayerdataPlus.getInstance().getDataFolder().getPath() + "\\Logs");
        if (!logFolder.exists()) {
            try {
                if (!logFolder.mkdir()) {
                    ServerHandler.sendConsoleMessage("&6Log: &fcreate folder &8\"&e" + logFolder.getName() + "&8\"  &c✘");
                }
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
        }
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

    private static void addLog(String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.append(message);
            bw.close();
        } catch (IOException e) {
            ServerHandler.sendDebugTrace(e);
        }
    }


    public void sendLog(String message, boolean time) {
        if (time) {
            DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
            String date = dateFormat.format(new Date());
            message = "[" + date + "]: " + message;
        }
        addLog(message + "\n");
    }
}
