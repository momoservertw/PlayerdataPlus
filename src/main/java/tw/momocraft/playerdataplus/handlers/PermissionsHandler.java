package tw.momocraft.playerdataplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class PermissionsHandler {

	public static boolean hasPermission(CommandSender sender, String permission) {
		return sender.hasPermission(permission) || sender.hasPermission("playerdataplus.*") || sender.isOp() || (sender instanceof ConsoleCommandSender);
	}

	public static boolean hasPermissionOffline(OfflinePlayer player, String permission) {
		return ConfigHandler.getDepends().getVault().getPermissions().playerHas(Bukkit.getWorlds().get(0).getName(), player, permission) || player.isOp() || (player instanceof ConsoleCommandSender);
	}
}