package killercreepr.cruxcore.listener;

import killercreepr.cruxcore.api.event.EntityDamageByOwnerEvent;
import killercreepr.cruxcore.api.event.EntityDeathByOwnerEvent;
import killercreepr.cruxentities.api.combat.EntityDamager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CustomEventListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        if(!(victim instanceof LivingEntity vic)) return;
        Entity dmger = event.getDamager();
        Entity dmgerOwner = EntityDamager.getOwner(dmger);
        if(dmgerOwner == null || dmger.equals(dmgerOwner)) return;

        EntityDamageByOwnerEvent ownerDmg = new EntityDamageByOwnerEvent(
            event.getDamageSource(), event.getCause(), victim, dmger, dmgerOwner, event.getDamage()
        );
        if(!ownerDmg.callEvent()){
            event.setCancelled(true);
            return;
        }
        double newDmg = ownerDmg.getDamage();
        if(newDmg != event.getDamage()){
            event.setDamage(newDmg);
        }

        double finalDmg = event.getFinalDamage();
        double health = vic.getHealth() - event.getFinalDamage();
        if(health > 0D) return;

        EntityDeathByOwnerEvent deathEvent = new EntityDeathByOwnerEvent(
            ownerDmg.getDamageSource(), ownerDmg.getCause(), ownerDmg.getVictim(), ownerDmg.getDamager(),
            ownerDmg.getOwner(), finalDmg
        );
        if(!deathEvent.callEvent()){
            event.setCancelled(true);
        }
    }

}
