package killercreepr.cruxcore.menu.module;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.core.menu.module.standard.ActivePagedMenuModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActivePluginItemsModule extends ActivePagedMenuModule<PluginItem> {
    public ActivePluginItemsModule(@NotNull String id, @NotNull MenuModule module, @NotNull NumberProvider indexes, @Nullable String valueFilter, @NotNull Holder<List<PluginItem>> values) {
        super(id, module, indexes, valueFilter, values);
    }

    @Override
    public MergedTagContainer buildTags(PluginItem pluginItem) {
        return null;
    }

    @Override
    public void setPagedItem(@NotNull Menu menu, int slot, int index, int listIndex, @NotNull PluginItem value) {
        menu.setItem(slot, value.buildItem());
    }

    @Override
    public void setEmptyItem(@NotNull Menu menu, int slot, int index) {
        menu.setItem(slot, null);
    }
}
