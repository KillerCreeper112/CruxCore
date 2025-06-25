package killercreepr.cruxcore.menu;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxcore.menu.module.ActivePluginItemsModule;
import killercreepr.cruxcore.menu.module.PluginItemsModuleBuilder;
import killercreepr.cruxitems.api.item.CruxedItem;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.cruxmenus.core.menu.ConfigMenu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginItemsMenu extends ConfigMenu {
    public PluginItemsMenu(@NotNull MenuHolder holder, @NotNull DataExchange info) {
        super(holder, info);
    }

    public PluginItemsMenu(@NotNull MenuHolder holder, @NotNull DataExchange info, @Nullable MergedTagContainer tags) {
        super(holder, info, tags);
    }

    protected ActivePluginItemsModule pluginItems;
    @Override
    public void load() {
        pluginItems = (ActivePluginItemsModule) new PluginItemsModuleBuilder(
            "plugin_items", NumberProvider.uniform(0, 44),
            null, null, null
        ).build(this);
        getModules().register(pluginItems);
        super.load();
    }

    @Override
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        super.onMenuClick(event);
        if(CruxItem.isEmpty(event.getCurrentItem())){
            int x;
            if(event.getClick().isRightClick()){
                x = -1;
            }else x = 1;
            pluginItems.addPage(x);
            refresh();
            return;
        }
        CruxedItem cruxed = CruxedItem.cruxed(event.getCurrentItem());
        PluginItem item = cruxed.getPluginItem();
        if(item != null){
            event.getWhoClicked().getInventory().addItem(item.buildItem());
            return;
        }
    }
}
