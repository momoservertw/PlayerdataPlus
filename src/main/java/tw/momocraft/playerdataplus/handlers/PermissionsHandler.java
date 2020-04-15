package tw.momocraft.playerdataplus.handlers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PermissionsHandler {

    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || sender.hasPermission("playerdataplus.*") || sender.isOp() || (sender instanceof ConsoleCommandSender);
    }

    public static boolean hasPermission(CommandSender sender, String permission, boolean isOp) {
        if (isOp) {
            return sender.hasPermission(permission) || sender.hasPermission("playerdataplus.*") || sender.isOp() || (sender instanceof ConsoleCommandSender);
        }
        return sender.hasPermission(permission) || sender.hasPermission("playerdataplus.*") || (sender instanceof ConsoleCommandSender);
    }

    public static boolean hasPermissionOffline(OfflinePlayer offlinePlayer, String permission) {
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            return player.hasPermission(permission) || player.hasPermission("playerdataplus.*") || player.isOp() || (player instanceof ConsoleCommandSender);
        }
        return ConfigHandler.getDepends().getVault().getPermissions().playerHas(Bukkit.getWorlds().get(0).getName(), offlinePlayer, permission) || offlinePlayer.isOp() || (offlinePlayer instanceof ConsoleCommandSender);
    }
}