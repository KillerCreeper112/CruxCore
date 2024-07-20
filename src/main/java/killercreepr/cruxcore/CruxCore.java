package killercreepr.cruxcore;

import killercreepr.crux.Crux;
import killercreepr.crux.CruxMainModule;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registries.CruxModuleRegistry;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.crux.util.CruxString;
import killercreepr.cruxattributes.CruxAttributesModule;
import killercreepr.cruxblocks.CruxBlocksModule;
import killercreepr.cruxconfig.CruxConfigsModule;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.command.CruxCoreCommands;
import killercreepr.cruxcore.listener.PlayerDataListener;
import killercreepr.cruxenchants.CruxEnchantsModule;
import killercreepr.cruxentities.CruxEntitiesModule;
import killercreepr.cruxitems.CruxItemsModule;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxpotions.CruxPotionsModule;
import killercreepr.cruxstructures.CruxStructuresModule;
import killercreepr.cruxstructures.manager.StructureManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class CruxCore extends CruxPlugin implements Listener {
    private static CruxCore instance;
    public static CruxCore inst(){ return instance; }
    protected final CruxModuleRegistry MODULES = CruxRegistries.MODULES;
    protected final CruxMainModule CRUX_CORE = new CruxMainModule();
    protected final CruxItemsModule CRUX_ITEMS = new CruxItemsModule();
    protected final CruxMenusModule CRUX_MENUS = new CruxMenusModule();
    protected final CruxConfigsModule CRUX_CONFIGS = new CruxConfigsModule();
    protected final CruxPotionsModule CRUX_POTIONS = new CruxPotionsModule();
    protected final CruxAttributesModule CRUX_ATTRIBUTES = new CruxAttributesModule();
    protected final CruxEntitiesModule CRUX_ENTITIES = new CruxEntitiesModule();
    protected final CruxEnchantsModule CRUX_ENCHANTS = new CruxEnchantsModule();
    protected final CruxBlocksModule CRUX_BLOCKS = new CruxBlocksModule();
    protected final CruxStructuresModule CRUX_STRUCTURES = new CruxStructuresModule();

    public @NotNull CruxBlocksModule cruxBlocks(){
        return CRUX_BLOCKS;
    }
    public @NotNull CruxMenusModule cruxMenus(){ return CRUX_MENUS; }

    public CruxStructuresModule cruxStructures() {
        return CRUX_STRUCTURES;
    }

    public CruxModuleRegistry modules() {
        return MODULES;
    }

    public CruxMainModule cruxCore() {
        return CRUX_CORE;
    }

    public CruxItemsModule cruxItems() {
        return CRUX_ITEMS;
    }

    public CruxConfigsModule cruxConfigs() {
        return CRUX_CONFIGS;
    }

    public CruxPotionsModule cruxPotions() {
        return CRUX_POTIONS;
    }

    public CruxAttributesModule cruxAttributes() {
        return CRUX_ATTRIBUTES;
    }

    public CruxEntitiesModule cruxEntities() {
        return CRUX_ENTITIES;
    }

    public CruxEnchantsModule cruxEnchants() {
        return CRUX_ENCHANTS;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Bukkit.broadcast(
            Component.text(CruxString.latinFont(event.getMessage()))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, CruxString.latinFont(event.getMessage())))
        );
    }

    protected final StructureManager structureManager = new StructureManager(this);

    public StructureManager structureManager() {
        return structureManager;
    }

    @Override
    public void enabled() {
        instance = this;
        Crux.setMainPlugin(this);

        new CruxCoreCommands(this).register(this);

        //register modules.
        //they will automatically add in their listeners
        CRUX_STRUCTURES.registerCommands(this, structureManager);
        MODULES.register(
            CRUX_CORE,
            CRUX_CONFIGS,
            CRUX_ITEMS,
            CRUX_MENUS,
            CRUX_POTIONS,
            CRUX_ATTRIBUTES,
            CRUX_ENTITIES,
            CRUX_ENCHANTS,
            CRUX_BLOCKS,
            CRUX_STRUCTURES
        ).enable(this);
        CRUX_ITEMS.registerGeneralDisplayFormatter();
        Crux.buildTickRunnable().runTaskTimer(this, 20L, 1L);

        CRUX_BLOCKS.blockTick().runTaskTimer(this, 20L, 1L);

        BukkitCfgHandlers.init(CfgRegistries.JSON);
        BukkitCfgHandlers.init(CfgRegistries.YAML);
        BukkitCfgHandlers.initJson(CfgRegistries.JSON);
        BukkitCfgHandlers.initYaml(CfgRegistries.YAML);

        reload();
        registerListeners(
            this,
            structureManager,
            new PlayerDataListener()
        );
        structureManager.buildRunnable().runTaskTimer(this, 20L, 1L);
    }


    @Override
    public void disabled() {
        super.disabled();
        MODULES.unregisterAll(this);
        structureManager.saveAllWorlds();
    }

    @Override
    public void reload() {
        super.reload();
        //CRUX_CONFIGS.reload(this);
        MODULES.reload(this);

        CruxRegistries.PLUGINS.forEach(plugin ->{
            if(plugin instanceof CruxCore) return;
            plugin.reload(this);
        });

        structureManager.loadConfiguration();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        reload();
        return super.onCommand(sender, command, label, args);
    }
}
