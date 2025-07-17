package killercreepr.cruxcore.listener;

import killercreepr.cruxcore.config.CruxCoreConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DebugListener implements Listener {
    protected final CruxCoreConfig cfg;

    public DebugListener(CruxCoreConfig cfg) {
        this.cfg = cfg;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if(cfg.DEBUG.getInt() != -1) return;
        event.getWhoClicked().sendMessage("slot= "+ event.getSlot() + ", raw_slot=" + event.getRawSlot());
    }

}
