package killercreepr.cruxcore.menu.module;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxitems.core.registries.CruxItemRegistries;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxmenus.api.menu.module.ActiveMenuModule;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.api.menu.module.config.MenuModuleBuilder;
import killercreepr.cruxmenus.core.menu.module.standard.PagedMenuModule;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PluginItemsModuleBuilder extends PagedMenuModule<PluginItem> {

    public PluginItemsModuleBuilder(@NotNull String id, @NotNull NumberProvider indexes,
                                    @Nullable String valueFilter, @Nullable MenuItems valueItems, @Nullable MenuItems emptyItems) {
        super(id, indexes, valueFilter, valueItems, emptyItems);
    }

    @Override
    public @NotNull Holder<List<PluginItem>> getValues(@NotNull Menu menu) {
        return () ->{
            List<PluginItem> list = new ArrayList<>(CruxItemRegistries.ITEMS.values());
            list.sort(Comparator.comparing(Keyed::key));
            return list;
        };
    }

    @Override
    public @Nullable ActiveMenuModule build(@NotNull Menu menu) {
        return new ActivePluginItemsModule(id, this, indexes, valueFilter, getValues(menu));
    }

    @Override
    public @NotNull Key key() {
        return Crux.key("plugin_items");
    }
}
