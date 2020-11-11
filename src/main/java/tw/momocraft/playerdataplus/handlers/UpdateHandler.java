package tw.momocraft.playerdataplus.handlers;

import org.bukkit.command.CommandSender;
import tw.momocraft.playerdataplus.PlayerdataPlus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is part of ItemJoin.
 * https://github.com/RockinChaos/ItemJoin
 */
public class UpdateHandler {

	private final int PROJECTID = 76878;

	private final String HOST = "https://api.spigotmc.org/legacy/update.php?resource=" + this.PROJECTID;
	private String versionExact = PlayerdataPlus.getInstance().getDescription().getVersion();
	private String localeVersion = this.versionExact.split("-")[0];
	private String latestVersion;

	private boolean updatesAllowed = ConfigHandler.getConfig("config.yml").getBoolean("Check-Updates");

	/**
	 * Initializes the UpdateHandler and Checks for Updates upon initialization.
	 */
	public UpdateHandler() {
		this.checkUpdates(PlayerdataPlus.getInstance().getServer().getConsoleSender());
	}

	/**
	 * Checks to see if an update is required, notifying the console window and online op players.
	 *
	 * @param sender  - The executor of the update checking.
	 */
	public void checkUpdates(final CommandSender sender) {
		if (this.updateNeeded(sender) && this.updatesAllowed) {
			ServerHandler.sendMessage(sender, "&aNew version is available: " + "&e&lv" + this.latestVersion);
			ServerHandler.sendMessage(sender, "&ehttps://www.spigotmc.org/resources/entityplus.70510/history");
		} else if (this.updatesAllowed) {
			ServerHandler.sendMessage(sender, "&fYou are up to date!");
		}
	}

	/**
	 * Directly checks to see if the spigotmc host has an update available.
	 *
	 * @param sender  - The executor of the update checking.
	 * @return If an update is needed.
	 */
	private boolean updateNeeded(final CommandSender sender) {
		if (this.updatesAllowed) {
			ServerHandler.sendMessage(sender, "&fChecking for updates...");
			try {
				URLConnection connection = new URL(this.HOST + "?_=" + System.currentTimeMillis()).openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String version = reader.readLine();
				reader.close();
				if (version.length() <= 7) {
					this.latestVersion = version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
					String[] latestSplit = this.latestVersion.split("\\.");
					String[] localeSplit = this.localeVersion.split("\\.");
					if ((Integer.parseInt(latestSplit[0]) > Integer.parseInt(localeSplit[0]) ||
							Integer.parseInt(latestSplit[1]) > Integer.parseInt(localeSplit[1]) || Integer.parseInt(latestSplit[2]) > Integer.parseInt(localeSplit[2]))) {
						return true;
					}
				}
			} catch (Exception e) {
				ServerHandler.sendMessage(sender, "&cFailed to check for updates, connection could not be made.");
				return false;
			}
		}
		return false;
	}
}