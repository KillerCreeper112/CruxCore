package killercreepr.cruxcore.listener;

import killercreepr.crux.data.entity.PlayerMemory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerDataListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        PlayerMemory.getOrCreate(p);
    }
}
