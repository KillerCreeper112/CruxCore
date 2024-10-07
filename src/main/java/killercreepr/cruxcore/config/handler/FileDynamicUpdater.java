package killercreepr.cruxcore.config.handler;

import killercreepr.crux.item.dynamic.DynamicItem;
import killercreepr.crux.item.predicate.ItemPredicate;
import killercreepr.crux.util.CruxObjects;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxcore.item.updater.CfgDynamicUpdater;
import killercreepr.cruxcore.item.updater.DynamicUpdater;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileDynamicUpdater implements FileObjectHandler<DynamicUpdater> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull DynamicUpdater updater) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable DynamicUpdater deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        FileRegistry registry = ctx.getRegistry();
        ItemPredicate predicate = registry.deserializeFromFile(ItemPredicate.class, o.get("predicate"));
        DynamicItem dynamicItem = registry.deserializeFromFile(DynamicItem.class, o.get("item"));
        if(CruxObjects.checkNull(predicate, dynamicItem)) return null;
        return new CfgDynamicUpdater(predicate, dynamicItem);
    }
}
