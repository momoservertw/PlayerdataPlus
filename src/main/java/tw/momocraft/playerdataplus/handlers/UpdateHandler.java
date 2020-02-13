package tw.momocraft.playerdataplus.handlers;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.PlayerdataPlus;

import java.io.*;
import java.net.URL;
import java.util.Collection;

public class UpdateHandler {

	private boolean updatesAllowed = ConfigHandler.getConfig("config.yml").getBoolean("Check-Updates");
	private final int PROJECTID = 75169;
	private final String HOST = "https://api.spigotmc.org/legacy/update.php?resource=" + PROJECTID;
	private String versionExact = PlayerdataPlus.getInstance().getDescription().getVersion();
	private boolean betaVersion = versionExact.contains("-SNAPSHOT") || versionExact.contains("-BETA") || versionExact.contains("-ALPHA");
	private String localeVersionRaw = versionExact.split("-")[0];
	private String latestVersionRaw;
	private double localeVersion = Double.parseDouble(localeVersionRaw.replace(".", ""));
	private double latestVersion;

	UpdateHandler(){
		this.checkUpdates(PlayerdataPlus.getInstance().getServer().getConsoleSender());
	}

	public void checkUpdates(CommandSender sender) {
		if (this.updateNeeded(sender) && this.updatesAllowed) {
			ServerHandler.sendMessage(sender, "&aNew version is available &8- &6v" + this.localeVersionRaw + " &f-> &ev" + this.latestVersionRaw);
			ServerHandler.sendMessage(sender, "&ehttps://www.spigotmc.org/resources/playerdataplus.75169/history");
			this.sendNotifications();
		} else if (this.updatesAllowed) {
			ServerHandler.sendMessage(sender, "&fYou are up to date!");
		}
	}

	private Boolean updateNeeded(CommandSender sender) {
		if (this.updatesAllowed) {
			ServerHandler.sendMessage(sender, "&fChecking for updates...");
			try {
				InputStream input = (InputStream) new URL(this.HOST).openStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String version = reader.readLine();
				reader.close();
				if (version.length() <= 7) {
					this.latestVersionRaw = version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
					this.latestVersion = Double.parseDouble(this.latestVersionRaw.replace(".", ""));
					if (this.latestVersion == this.localeVersion && this.betaVersion || this.localeVersion > this.latestVersion && !this.betaVersion || this.latestVersion > this.localeVersion) {
						return true;
					}
				}
			} catch (Exception e) {
				ServerHandler.sendMessage(sender, "&cThere is an error occurred while checking the updates.");
				ServerHandler.sendDebugTrace(e);
				return false;
			}
		}
		return false;
	}

	private void sendNotifications() {
		try {
			Collection< ? > playersOnline = null;
			Player[] playersOnlineOld = null;
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
					playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
					for (Object objPlayer: playersOnline) {
						if (((Player) objPlayer).isOp()) {
							ServerHandler.sendPlayerMessage(((Player) objPlayer), "&aNew version is available &8- &ev" + this.localeVersionRaw + " -> v" + this.latestVersionRaw);
						}
					}
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player objPlayer: playersOnlineOld) {
					if (objPlayer.isOp()) {
						ServerHandler.sendPlayerMessage(objPlayer, "&aNew version is available &8- &ev" + this.localeVersionRaw + " -> v" + this.latestVersionRaw);
					}
				}
			}
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
	}
}