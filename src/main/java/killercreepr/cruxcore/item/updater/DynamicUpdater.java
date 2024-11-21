package killercreepr.cruxcore.item.updater;

import killercreepr.cruxitems.core.item.CruxedItemUpdateContext;
import org.jetbrains.annotations.NotNull;

public interface DynamicUpdater {
    void apply(@NotNull CruxedItemUpdateContext ctx);
    boolean canUpdate(CruxedItemUpdateContext ctx);
}
