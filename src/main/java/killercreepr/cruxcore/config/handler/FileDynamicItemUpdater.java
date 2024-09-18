package killercreepr.cruxcore.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.util.CruxObjects;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxcore.item.updater.DynamicItemUpdater;
import killercreepr.cruxcore.item.updater.DynamicUpdater;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FileDynamicItemUpdater implements FileObjectHandler<DynamicItemUpdater> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull DynamicItemUpdater updater) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable DynamicItemUpdater deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        FileRegistry registry = ctx.getRegistry();

        Key key = registry.deserializeFromFile(Key.class, o.get("key"));
        int priority = o.getOrDefaultObject(Integer.class, "priority", 0);
        List<DynamicUpdater> dynamicUpdaters = registry.deserializeFromFile(
            new TypeToken<List<DynamicUpdater>>(){}.getType(), o.get("updaters")
        );

        if(CruxObjects.checkNull(key, dynamicUpdaters)) return null;

        return new DynamicItemUpdater(
            key, dynamicUpdaters, priority
        );
    }
}
