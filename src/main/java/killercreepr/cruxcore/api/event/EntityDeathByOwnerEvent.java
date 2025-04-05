package killercreepr.cruxcore.api.event;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDeathByOwnerEvent extends EntityDamageByOwnerEvent{
    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     *
     * @param damageSource
     * @param damageCause
     * @param victim
     * @param damager
     * @param owner
     * @param damage
     */
    public EntityDeathByOwnerEvent(DamageSource damageSource, EntityDamageEvent.DamageCause damageCause, Entity victim, Entity damager, Entity owner, double damage) {
        super(damageSource, damageCause, victim, damager, owner, damage);
    }
    @Deprecated(since = "Not changeable here")
    public void setDamage(double damage) {
        this.damage = damage;
    }
}
