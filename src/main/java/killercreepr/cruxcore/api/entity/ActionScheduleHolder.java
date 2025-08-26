package killercreepr.cruxcore.api.entity;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxcore.api.data.ScheduledEntityAction;
import killercreepr.cruxcore.entity.memory.SimpleActionScheduleHolder;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public interface ActionScheduleHolder {
    static ActionScheduleHolder actionScheduler(Entity e){
        return EntityMemory.getOrCreateDataHolder(e, SimpleActionScheduleHolder.class, SimpleActionScheduleHolder::new);
    }
    static ActionScheduleHolder actionScheduler(Entity e, Consumer<ActionScheduleHolder> consumer){
        AtomicBoolean bool = new AtomicBoolean(false);
        ActionScheduleHolder got = EntityMemory.getOrCreateDataHolder(e, SimpleActionScheduleHolder.class, mem ->{
            SimpleActionScheduleHolder holder = new SimpleActionScheduleHolder(mem);
            consumer.accept(holder);
            bool.set(true);
            return holder;
        });
        if(bool.get()){
            return got;
        }
        consumer.accept(got);
        return got;
    }
    static ActionScheduleHolder actionSchedulerIfPresent(Entity e){
        return EntityMemory.getDataHolder(e, SimpleActionScheduleHolder.class);
    }

    void scheduleAction(ScheduledEntityAction action);
    Collection<ScheduledEntityAction> scheduledActions();
    void unscheduleAction(ScheduledEntityAction action);
}
