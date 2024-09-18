package killercreepr.cruxcore;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.Crux;
import killercreepr.crux.CruxMainModule;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registries.CruxModuleRegistry;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.cruxadvancements.CruxAdvancementsModule;
import killercreepr.cruxattributes.CruxAttributesModule;
import killercreepr.cruxblocks.CruxBlocksModule;
import killercreepr.cruxconfig.CruxConfigsModule;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.loader.*;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.command.CruxCoreCommands;
import killercreepr.cruxcore.config.handler.FileDynamicItemUpdater;
import killercreepr.cruxcore.config.handler.FileDynamicUpdater;
import killercreepr.cruxcore.item.updater.DynamicItemUpdater;
import killercreepr.cruxcore.item.updater.DynamicUpdater;
import killercreepr.cruxcore.listener.ItemStackListener;
import killercreepr.cruxcore.listener.PlayerDataListener;
import killercreepr.cruxcore.recipes.CraftingRecipeLoader;
import killercreepr.cruxenchants.CruxEnchantsModule;
import killercreepr.cruxentities.CruxEntitiesModule;
import killercreepr.cruxexternal.CruxExternalModule;
import killercreepr.cruxgeneration.CruxGenerationModule;
import killercreepr.cruxitems.CruxItemsModule;
import killercreepr.cruxitems.registries.CruxItemRegistries;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxpotions.CruxPotionsModule;
import killercreepr.cruxstructures.CruxStructuresModule;
import killercreepr.cruxstructures.event.StructurePlaceEvent;
import killercreepr.cruxstructures.manager.StructureManager;
import killercreepr.cruxworlds.CruxWorldsModule;
import killercreepr.cruxworlds.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.world.manager.SimpleCruxWorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CruxCore extends CruxPlugin implements Listener {
    private static CruxCore instance;
    public static CruxCore inst(){ return instance; }
    protected final CruxModuleRegistry MODULES = CruxRegistries.MODULES;
    protected final CruxMainModule CRUX_MAIN = new CruxMainModule();
    protected final CruxItemsModule CRUX_ITEMS = new CruxItemsModule();
    protected final CruxMenusModule CRUX_MENUS = new CruxMenusModule();
    protected final CruxConfigsModule CRUX_CONFIGS = new CruxConfigsModule();
    protected final CruxPotionsModule CRUX_POTIONS = new CruxPotionsModule();
    protected final CruxAttributesModule CRUX_ATTRIBUTES = new CruxAttributesModule();
    protected final CruxEntitiesModule CRUX_ENTITIES = new CruxEntitiesModule();
    protected final CruxEnchantsModule CRUX_ENCHANTS = new CruxEnchantsModule();
    protected final CruxBlocksModule CRUX_BLOCKS = new CruxBlocksModule();
    protected final CruxStructuresModule CRUX_STRUCTURES = new CruxStructuresModule();
    protected final CruxExternalModule CRUX_EXTERNAL = new CruxExternalModule();
    protected final CruxAdvancementsModule CRUX_ADVANCEMENTS = new CruxAdvancementsModule();
    protected final CruxGenerationModule CRUX_GENERATION = new CruxGenerationModule();
    protected final CruxWorldsModule CRUX_WORLDS = new CruxWorldsModule();

    public CruxExternalModule cruxExternal(){
        return CRUX_EXTERNAL;
    }
    public CruxWorldsModule cruxWorlds(){
        return CRUX_WORLDS;
    }
    public CruxAdvancementsModule cruxAdvancements(){
        return CRUX_ADVANCEMENTS;
    }
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

    public CruxMainModule cruxMain() {
        return CRUX_MAIN;
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
    public CruxGenerationModule cruxGeneration() {
        return CRUX_GENERATION;
    }

    @EventHandler(ignoreCancelled = true)
    public void onStructurePlace(StructurePlaceEvent event) {
        Location l = event.getLocation();
        Bukkit.broadcast(Component.text(
            "[CruxCore] Structure " + event.getStructure().key() + " spawned at " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ()
        ).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ())));
    }

    protected final StructureManager structureManager = new StructureManager(this);

    public StructureManager structureManager() {
        return structureManager;
    }
    protected final SimpleCruxWorldManager worldManager = new SimpleCruxWorldManager();

    public CruxWorldManager worldManager() {
        return worldManager;
    }

    @Override
    public void onLoad() {
        instance = this;
        Crux.setMainPlugin(this);
        BukkitCfgHandlers.initStandard();

        loadTags();
        loadBlockSoundGroups();

        new CruxCoreCommands(this).register(this);

        CRUX_STRUCTURES.registerCommands(this, structureManager);
        MODULES.register(
            CRUX_MAIN,
            CRUX_CONFIGS,
            CRUX_ITEMS,
            CRUX_MENUS,
            CRUX_POTIONS,
            CRUX_ATTRIBUTES,
            CRUX_ENTITIES,
            CRUX_ENCHANTS,
            CRUX_BLOCKS,
            CRUX_STRUCTURES,
            CRUX_EXTERNAL,
            CRUX_ADVANCEMENTS,
            CRUX_GENERATION,
            CRUX_WORLDS
        ).load(this);

        super.onLoad();

        CfgRegistries.SIMPLE_REGISTRY.forEach(reg ->{
            reg.registerFileHandler(DynamicUpdater.class, new FileDynamicUpdater());
            reg.registerFileHandler(DynamicItemUpdater.class, new FileDynamicItemUpdater());
        });
    }

    @Override
    public void enabled() {
        //enable modules.
        //they will automatically add in their listeners
        MODULES.enable(this);
        CRUX_ITEMS.registerGeneralDisplayFormatter();

        CRUX_BLOCKS.buildBlockTickTask(getServer()).runTaskTimerAsynchronously(this, 20L, 1L);

        reload();
        registerListeners(
            this,
            structureManager,
            new PlayerDataListener(),
            new ItemStackListener(this),
            worldManager
        );
        structureManager.buildRunnable().runTaskTimerAsynchronously(this, 20L, 1L);
    }

    @Override
    public void disabled() {
        super.disabled();
        MODULES.unregisterAll(this);
        structureManager.saveAllWorlds();
    }

    public void loadTags(){
        new ItemTagLoader().loadConfiguration(
            new CruxFolder(this, "tags/item").file()
        );
        new BlockTagLoader().loadConfiguration(
            new CruxFolder(this, "tags/block").file()
        );
    }

    public void loadBlockSoundGroups(){
        new BlockSoundGroupLoader().loadConfiguration(
            new CruxFolder(this, "block/sound_groups").file()
        );
    }

    @Override
    public void reload() {
        super.reload();
        loadTags();
        //CRUX_CONFIGS.reload(this);

        new KeyLootTableLoader().loadConfiguration(
            new CruxFolder(this, "key_loot_tables").file()
        );

        MODULES.reload(this);
        CruxRegistries.PLUGIN.forEach(plugin ->{
            if(plugin instanceof CruxCore) return;
            plugin.reload(this);
        });

        new LootTableLoader().loadConfiguration(
            new CruxFolder(this, "loot_tables").file()
        );

        loadBlockSoundGroups();

        structureManager.loadConfiguration();

        CruxCore.inst().cruxMenus().menuRegistry().loadConfiguration(
            new CruxFolder(this, "menus").file()
        );

        new CraftingRecipeLoader().load(
            new CruxConfig(this, "crafting_recipes"), getServer()
        );

        if(parsedItemUpdaters != null){
            parsedItemUpdaters.forEach(parsed ->{
                CruxItemRegistries.ITEM_UPDATERS.remove(parsed.key());
            });
        }
        DataFile dataFile = BukkitDataFile.parseFromGeneralPath(getDataFolder().getAbsolutePath(), "item_updaters");
        if(dataFile != null){
            parsedItemUpdaters = dataFile.deserialize("values", new TypeToken<Collection<DynamicItemUpdater>>(){}.getType());
        }else parsedItemUpdaters = null;

        if(parsedItemUpdaters != null){
            parsedItemUpdaters.forEach(parsed ->{
                CruxItemRegistries.ITEM_UPDATERS.register(parsed.getPriority(), parsed);
                log("Registered item updater: " + parsed.key() + " with priority, " + parsed.getPriority());
            });
        }
    }

    protected Collection<DynamicItemUpdater> parsedItemUpdaters;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        reload();
        return super.onCommand(sender, command, label, args);
    }
}
