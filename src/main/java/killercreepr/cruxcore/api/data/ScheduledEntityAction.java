package killercreepr.cruxcore.api.data;

import org.bukkit.entity.Entity;

public interface ScheduledEntityAction {
    void action(Entity e);
    boolean isReady(Entity e);

    default void onRemoved(Entity e){}
}
