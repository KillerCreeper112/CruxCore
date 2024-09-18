package killercreepr.cruxcore.item.updater;

import killercreepr.crux.context.TextParserContext;
import killercreepr.crux.item.dynamic.DynamicItem;
import killercreepr.crux.item.predicate.ItemPredicate;
import killercreepr.crux.util.CruxItem;
import killercreepr.cruxitems.item.CruxedItemUpdateContext;
import org.jetbrains.annotations.NotNull;

public class CfgDynamicUpdater implements DynamicUpdater{
    protected final @NotNull ItemPredicate predicate;
    protected final @NotNull DynamicItem dynamicItem;
    public CfgDynamicUpdater(@NotNull ItemPredicate predicate, @NotNull DynamicItem dynamicItem) {
        this.predicate = predicate;
        this.dynamicItem = dynamicItem;
    }

    @Override
    public void apply(@NotNull CruxedItemUpdateContext ctx) {
        CruxItem item = ctx.getItem();
        dynamicItem.applyComponents(item, TextParserContext.empty());
    }

    @Override
    public boolean canUpdate(CruxedItemUpdateContext ctx) {
        return predicate.test(ctx.getItem().item());
    }
}
