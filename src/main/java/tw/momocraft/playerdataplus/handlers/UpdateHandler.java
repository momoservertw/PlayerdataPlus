package tw.momocraft.playerdataplus.handlers;
import org.bukkit.command.CommandSender;
import tw.momocraft.playerdataplus.PlayerdataPlus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateHandler {
	
    private boolean updatesAllowed = ConfigHandler.getConfig("config.yml").getBoolean("Check-Updates");
    private final String HOST = "https://www.spigotmc.org/api/general.php";
    private final int PROJECTID = 70592;
    private final String KEY = ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + PROJECTID);
    private String versionExact = PlayerdataPlus.getInstance().getDescription().getVersion();
    private boolean betaVersion = versionExact.contains("-SNAPSHOT") || versionExact.contains("-BETA") || versionExact.contains("-ALPHA");
    private String localeVersionRaw = versionExact.split("-")[0];
    private String latestVersionRaw;
    private double localeVersion = Double.parseDouble(localeVersionRaw.replace(".", ""));
    private double latestVersion;
        
    /**
     * Initializes the UpdateHandler and Checks for Updates upon initialization.
     *
     */
    public UpdateHandler(){
       this.checkUpdates(PlayerdataPlus.getInstance().getServer().getConsoleSender(), false);
    }
    
    /**
     * Checks to see if an update is required, notifying the console window and online op players.
     */
    public void checkUpdates(CommandSender sender, boolean playerJoin) {
    	if (playerJoin) {
			if (this.updateNeeded(sender)) {
				if (this.betaVersion) {
					ServerHandler.sendMessage(sender, "&cThis &bSNAPSHOT &cis outdated and a release version is now available.");
				}
				ServerHandler.sendMessage(sender, "&aNew version is available: " + "&e&lv" + this.latestVersionRaw);
				ServerHandler.sendMessage(sender, "&ehttps://www.spigotmc.org/resources/playerdataplus.70510/history");
			}
			return;
		}
		ServerHandler.sendMessage(sender, "&fChecking for updates...");
    	if (this.updateNeeded(sender) && this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerHandler.sendMessage(sender, "&cThis &bSNAPSHOT &cis outdated and a release version is now available.");
    		}
    		ServerHandler.sendMessage(sender, "&cNew version is available: " + "&e&lv" + this.latestVersionRaw);
    		ServerHandler.sendMessage(sender, "&ahttps://www.spigotmc.org/resources/playerdataplus.70510/history");
    	} else if (this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerHandler.sendMessage(sender, "&fYou are running a SNAPSHOT!");
    			ServerHandler.sendMessage(sender, "&fIf you find any bugs please report them!");
    		}
    		ServerHandler.sendMessage(sender, "&fYou are up to date!");
    	}
    }
    
    /**
     * Directly checks to see if the spigotmc host has an update available.
     */
    private Boolean updateNeeded(CommandSender sender) {
    	if (this.updatesAllowed) {
    		try {
    			HttpURLConnection con = (HttpURLConnection) new URL(this.HOST).openConnection();
    			con.setDoOutput(true);
    			con.setRequestMethod("POST");
    			con.getOutputStream().write(this.KEY.getBytes("UTF-8"));
    			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
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
    			ServerHandler.sendMessage(sender, "&cAn error has occurred when checking the plugin version!");
    			ServerHandler.sendMessage(sender, "&cPlease contact the plugin developer!");
    			ServerHandler.sendDebugTrace(e);
    			return false;
    		}
    	}
    	return false;
    }
}