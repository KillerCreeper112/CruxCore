package killercreepr.cruxcore.listener;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import killercreepr.crux.core.Crux;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemStackListener implements Listener {
    protected final Plugin plugin;
    public ItemStackListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        Player p = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, task ->{
            if(!p.isOnline()) return;
            ItemStack item = p.getInventory().getItem(event.getSlot());
            if(item==null) return;
            Crux.handlers().item().update(item, p);
        });
    }

    /*@EventHandler(ignoreCancelled = true)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack result = inv.getResult();
        if(result==null) return;
        inv.setResult(Crux.handlers().item().update(result));
    }*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        ItemStack item = entity.getItemStack();
        entity.setItemStack(Crux.handlers().item().update(item));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPrepareResult(PrepareResultEvent event) {
        ItemStack result = event.getResult();
        if(result==null) return;
        event.setResult(Crux.handlers().item().update(result));
    }

}
