package killercreepr.cruxcore.data;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxcore.api.data.ScheduledEntityAction;
import org.bukkit.entity.Entity;

public abstract class TickedScheduledEntityAction implements ScheduledEntityAction {
    protected final int ticks;
    protected final long time;

    public TickedScheduledEntityAction(int ticks, long time) {
        this.ticks = ticks;
        this.time = time;
    }

    @Override
    public boolean isReady(Entity e) {
        return !CruxMath.hasOccurredWithin(time, ticks);
    }
}
