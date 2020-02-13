package tw.momocraft.playerdataplus.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import tw.momocraft.playerdataplus.PlayerdataPlus;
import tw.momocraft.playerdataplus.handlers.ServerHandler;

public class BungeeCord implements PluginMessageListener {

	public static void SwitchServers(Player player, String server) {
		Messenger messenger = PlayerdataPlus.getInstance().getServer().getMessenger();
		if (!messenger.isOutgoingChannelRegistered(PlayerdataPlus.getInstance(), "BungeeCord")) {
			messenger.registerOutgoingPluginChannel(PlayerdataPlus.getInstance(), "BungeeCord");
		}
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		player.sendPluginMessage(PlayerdataPlus.getInstance(), "BungeeCord", out.toByteArray());
	}
	
	public static void ExecuteCommand(Player player, String cmd) {
		Messenger messenger = PlayerdataPlus.getInstance().getServer().getMessenger();
		if (!messenger.isOutgoingChannelRegistered(PlayerdataPlus.getInstance(), "BungeeCord")) {
			messenger.registerOutgoingPluginChannel(PlayerdataPlus.getInstance(), "BungeeCord");
		}
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		try {
			out.writeUTF("Subchannel");
			out.writeUTF("Argument");
			out.writeUTF(cmd);
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		player.sendPluginMessage(PlayerdataPlus.getInstance(), "BungeeCord", out.toByteArray());
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) { return; }
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in .readUTF();
		if (!subchannel.contains("PlayerCount")) {
			player.sendMessage(subchannel + " " + in .readByte());
		}
	} 
}