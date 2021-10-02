package tw.momocraft.playerdataplus.playerstatus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.playerstatus.PlayerStatusControl;

public class PlayerChangedWorld implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        if (!ConfigHandler.getConfigPath().isPlayerStatus())
            return;
        if (!ConfigHandler.getConfigPath().isPsCheckWorldChange())
            return;
        PlayerStatusControl.check(e.getPlayer());
    }
}
