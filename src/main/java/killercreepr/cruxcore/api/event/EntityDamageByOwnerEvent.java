package killercreepr.cruxcore.api.event;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageByOwnerEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    protected final DamageSource damageSource;
    protected final EntityDamageEvent.DamageCause cause;
    protected final Entity victim;
    protected final Entity damager;
    protected final Entity owner;
    protected double damage;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public EntityDamageByOwnerEvent(DamageSource damageSource, EntityDamageEvent.DamageCause damageCause, Entity victim, Entity damager, Entity owner, double damage) {
        this.damageSource = damageSource;
        this.cause = damageCause;
        this.victim = victim;
        this.damager = damager;
        this.owner = owner;
        this.damage = damage;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    public Entity getVictim() {
        return victim;
    }

    public Entity getDamager() {
        return damager;
    }

    public Entity getOwner() {
        return owner;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
