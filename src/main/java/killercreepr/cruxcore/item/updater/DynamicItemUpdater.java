package killercreepr.cruxcore.item.updater;

import killercreepr.cruxitems.api.item.CruxedItemUpdater;
import killercreepr.cruxitems.core.item.CruxedItemUpdateContext;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DynamicItemUpdater implements CruxedItemUpdater {
    protected final @NotNull Key key;
    protected final @NotNull List<DynamicUpdater> updaters;
    protected final int priority;
    public DynamicItemUpdater(@NotNull Key key, @NotNull List<DynamicUpdater> updaters, int priority) {
        this.key = key;
        this.updaters = updaters;
        this.priority = priority;
    }

    public @NotNull Key getKey() {
        return key;
    }

    public @NotNull List<DynamicUpdater> getUpdaters() {
        return updaters;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public void onUpdate(@NotNull CruxedItemUpdateContext ctx) {
        DynamicUpdater updater = find(ctx);
        if(updater==null) return;
        updater.apply(ctx);
    }

    public DynamicUpdater find(CruxedItemUpdateContext ctx){
        for(DynamicUpdater updater : updaters){
            if(updater.canUpdate(ctx)) return updater;
        }
        return null;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}
