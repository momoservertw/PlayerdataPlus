package tw.momocraft.playerdataplus.playerstatus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.playerstatus.PlayerStatusControl;

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!ConfigHandler.getConfigPath().isPlayerStatus())
            return;
        if (!ConfigHandler.getConfigPath().isPsCheckLogin())
            return;
        PlayerStatusControl.check(e.getPlayer());
    }
}
