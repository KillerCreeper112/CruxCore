package killercreepr.cruxcore.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.cruxcore.api.data.ScheduledEntityAction;
import killercreepr.cruxcore.api.entity.ActionScheduleHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleActionScheduleHolder extends EntityTickedDataHolder implements ActionScheduleHolder {
    public static final Key KEY = Crux.key("action_scheduler");
    public SimpleActionScheduleHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public SimpleActionScheduleHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected final Set<ScheduledEntityAction> scheduledActions = ConcurrentHashMap.newKeySet();

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        for (ScheduledEntityAction action : scheduledActions) {
            action.onRemoved(e);
        }
    }

    @Override
    public void tick(@NotNull Entity entity) {
        for (Iterator<ScheduledEntityAction> it = scheduledActions.iterator(); it.hasNext();) {
            ScheduledEntityAction action = it.next();
            if (action.isReady(entity)) {
                action.action(entity);
                it.remove();
            }
        }
    }


    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        return super.shouldRemoveFromMemory(e) || scheduledActions.isEmpty();
    }

    @Override
    public void scheduleAction(ScheduledEntityAction action) {
        scheduledActions.add(action);
    }

    @Override
    public Collection<ScheduledEntityAction> scheduledActions() {
        return scheduledActions;
    }

    @Override
    public void unscheduleAction(ScheduledEntityAction action) {
        scheduledActions.remove(action);
    }
}
