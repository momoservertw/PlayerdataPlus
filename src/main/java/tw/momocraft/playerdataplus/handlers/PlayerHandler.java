package tw.momocraft.playerdataplus.handlers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerHandler {
    public static Player getPlayerString(String playerName) {
        Player args = null;
        try {
            args = Bukkit.getPlayer(UUID.fromString(playerName));
        } catch (Exception e) {
        }
        if (args == null) {
            return Bukkit.getPlayer(playerName);
        }
        return args;
    }

    public static String getPlayerUUID(Player player) {
        if (player != null) {
            return player.getUniqueId().toString();
        }
        return "";
    }

    public static String getOfflinePlayerID(OfflinePlayer player) {
        if (player != null) {
            return player.getUniqueId().toString();
        }
        return "";
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        List<String> priority = ConfigHandler.getConfigPath().getCleanPriority();
        for (String check : priority) {
            switch (check) {
                case "LuckPerms":
                    if (ConfigHandler.getDepends().LuckPermsEnabled()) {
                        User luckUser = LuckPermsProvider.get().getUserManager().getUser(playerName);
                        if (luckUser != null) {
                            UUID playerUUID = luckUser.getUniqueId();
                            return Bukkit.getOfflinePlayer(playerUUID);
                        }
                    }
                    break;
                case "CMI":
                    if (ConfigHandler.getDepends().CMIEnabled()) {
                        CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(playerName);
                        if (cmiUser != null) {
                            UUID playerUUID = cmiUser.getUniqueId();
                            return Bukkit.getOfflinePlayer(playerUUID);
                        }
                    }
                    break;
                case "Playerdata":
                    Collection<?> playersOnlineNew;
                    OfflinePlayer[] playersOnlineOld;
                    try {
                        if (Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                            playersOnlineNew = ((Collection<?>) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (Object objPlayer : playersOnlineNew) {
                                Player player = ((Player) objPlayer);
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player;
                                }
                            }
                        } else {
                            playersOnlineOld = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (OfflinePlayer player : playersOnlineOld) {
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player;
                                }
                            }
                        }
                    } catch (Exception e) {
                        ServerHandler.sendDebugTrace(e);
                    }
                    break;
                default:
                    try {
                        if (Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                            playersOnlineNew = ((Collection<?>) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (Object objPlayer : playersOnlineNew) {
                                Player player = ((Player) objPlayer);
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player;
                                }
                            }
                        } else {
                            playersOnlineOld = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (OfflinePlayer player : playersOnlineOld) {
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player;
                                }
                            }
                        }
                    } catch (Exception e) {
                        ServerHandler.sendDebugTrace(e);
                    }
                    break;
            }
        }
        return null;
    }

    public static UUID getOfflineUUID(String playerName) {
        List<String> priority = ConfigHandler.getConfigPath().getCleanPriority();
        for (String check : priority) {
            switch (check) {
                case "LuckPerms":
                    if (ConfigHandler.getDepends().LuckPermsEnabled()) {
                        User luckUser = LuckPermsProvider.get().getUserManager().getUser(playerName);
                        if (luckUser != null) {
                            return luckUser.getUniqueId();
                        }
                    }
                    break;
                case "CMI":
                    if (ConfigHandler.getDepends().CMIEnabled()) {
                        CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(playerName);
                        if (cmiUser != null) {
                            return cmiUser.getUniqueId();
                        }
                    }
                    break;
                case "Playerdata":
                    Collection<?> playersOnlineNew;
                    OfflinePlayer[] playersOnlineOld;
                    try {
                        if (Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                            playersOnlineNew = ((Collection<?>) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (Object objPlayer : playersOnlineNew) {
                                Player player = ((Player) objPlayer);
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player.getUniqueId();
                                }
                            }
                        } else {
                            playersOnlineOld = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (OfflinePlayer player : playersOnlineOld) {
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player.getUniqueId();
                                }
                            }
                        }
                    } catch (Exception e) {
                        ServerHandler.sendDebugTrace(e);
                    }
                    break;
                default:
                    try {
                        if (Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                            playersOnlineNew = ((Collection<?>) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (Object objPlayer : playersOnlineNew) {
                                Player player = ((Player) objPlayer);
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player.getUniqueId();
                                }
                            }
                        } else {
                            playersOnlineOld = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                            for (OfflinePlayer player : playersOnlineOld) {
                                if (player.getName().equalsIgnoreCase(playerName)) {
                                    return player.getUniqueId();
                                }
                            }
                        }
                    } catch (Exception e) {
                        ServerHandler.sendDebugTrace(e);
                    }
                    break;
            }
        }
        return null;
    }
}
