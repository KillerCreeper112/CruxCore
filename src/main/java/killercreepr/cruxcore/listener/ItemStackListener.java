package killercreepr.cruxcore.listener;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxItem;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxcore.CruxCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemStackListener implements Listener {
    protected final Plugin plugin;
    public ItemStackListener(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskTimer(plugin, task ->{
            for(Player p : Bukkit.getOnlinePlayers()){
                ItemStack item = p.getInventory().getItemInMainHand();
                //Bukkit.broadcastMessage(p.getActiveItemUsedTime() + "");
            }
        }, 0L, 1L);
        plugin.getServer().getScheduler().runTaskTimer(plugin, task ->{
            Player p = Bukkit.getPlayer("killercreepr");
            if(p==null) return;
            ItemStack item = p.getActiveItem();
            if(item.isEmpty()){
                if(x==0L) return;
                if(CruxMath.hasOccurredWithin(x, 3)){
                    x = 0L;
                    Bukkit.broadcastMessage("SHOOT");
                }
            }
        }, 0L, 1L);
    }
    long x;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if(item==null) return;
        if(item.getType() != Material.BOW) return;
        x = System.currentTimeMillis();
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(!(event.getEntity() instanceof Player p)) return;
        new BukkitRunnable(){
            @Override
            public void run() {
                Entity e = event.getProjectile();
                if(!e.isValid()) return;
                e.getWorld().getNearbyEntities(e.getBoundingBox()).forEach(hit ->{
                    if(!(hit instanceof LivingEntity d)) return;
                    d.damage(1D, e);
                    e.remove();
                });
            }
        }.runTaskLater(CruxCore.inst(), 3);
    }


    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        ItemStack item = entity.getItemStack();
        entity.setItemStack(Crux.handlers().item().update(item));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareResult(PrepareResultEvent event) {
        ItemStack result = event.getResult();
        if(result==null) return;
        event.setResult(Crux.handlers().item().update(result));
    }

}
