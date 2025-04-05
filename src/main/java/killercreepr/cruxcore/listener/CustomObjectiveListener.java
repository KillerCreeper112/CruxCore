package killercreepr.cruxcore.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxcore.advancement.objective.StructureEnterObjective;
import killercreepr.cruxcore.advancement.objective.StructureLeaveObjective;
import killercreepr.cruxcore.api.event.PlayerEnterStructureEvent;
import killercreepr.cruxcore.api.event.PlayerLeaveStructureEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CustomObjectiveListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEnterStructure(PlayerEnterStructureEvent event) {
        var p = event.getPlayer();
        AdvancementHolder holder = holder(p);
        if(holder==null) return;

        holder.getAdvancementTracker().apply(StructureEnterObjective.class, (manager,
                                                                             advancement,
                                                                             objective) -> {
            objective.trigger(p.getUniqueId(), manager, advancement, event);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeaveStructure(PlayerLeaveStructureEvent event) {
        var p = event.getPlayer();
        AdvancementHolder holder = holder(p);
        if(holder==null) return;

        holder.getAdvancementTracker().apply(StructureLeaveObjective.class, (manager,
                                                                             advancement,
                                                                             objective) -> {
            objective.trigger(p.getUniqueId(), manager, advancement, event);
        });
    }

    public AdvancementHolder holder(Entity e){
        return EntityMemory.getOrCreateDataHolder(e, AdvancementHolder.class);
    }
}
