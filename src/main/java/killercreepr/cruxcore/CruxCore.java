package killercreepr.cruxcore;

import com.google.common.reflect.TypeToken;
import io.papermc.paper.entity.CollarColorable;
import killercreepr.crux.api.block.CruxedBlock;
import killercreepr.crux.api.block.tag.BlockTag;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.entity.tag.EntityTag;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.block.tag.BaseBlockTag;
import killercreepr.crux.core.entity.tag.BaseEntityTag;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.plugin.module.CruxMainModule;
import killercreepr.crux.core.registries.CruxModuleRegistry;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxadvancements.CruxAdvancementsModule;
import killercreepr.cruxattributes.core.CruxAttributesModule;
import killercreepr.cruxblocks.CruxBlocksModule;
import killercreepr.cruxblocks.manager.CruxBlockTicker;
import killercreepr.cruxconfig.CruxConfigsModule;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.loader.*;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.command.CruxCoreCommands;
import killercreepr.cruxcore.command.FAWECommands;
import killercreepr.cruxcore.config.handler.FileDynamicItemUpdater;
import killercreepr.cruxcore.config.handler.FileDynamicUpdater;
import killercreepr.cruxcore.item.updater.DynamicItemUpdater;
import killercreepr.cruxcore.item.updater.DynamicUpdater;
import killercreepr.cruxcore.listener.ItemStackListener;
import killercreepr.cruxcore.listener.PlayerDataListener;
import killercreepr.cruxcore.recipes.CraftingRecipeLoader;
import killercreepr.cruxenchants.CruxEnchantsModule;
import killercreepr.cruxentities.CruxEntitiesModule;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import killercreepr.cruxexternal.CruxExternalModule;
import killercreepr.cruxgeneration.CruxGenerationModule;
import killercreepr.cruxitems.CruxItemsModule;
import killercreepr.cruxitems.registries.CruxItemRegistries;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxpotions.CruxPotionsModule;
import killercreepr.cruxstats.core.CruxStatsModule;
import killercreepr.cruxstats.core.stat.PlayerCruxStatHolder;
import killercreepr.cruxstructures.CruxStructuresModule;
import killercreepr.cruxstructures.manager.StructureManager;
import killercreepr.cruxworlds.CruxWorldsModule;
import killercreepr.cruxworlds.config.loader.NaturalEntityGroupGroupCfgLoader;
import killercreepr.cruxworlds.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.world.manager.SimpleCruxWorldManager;
import net.kyori.adventure.key.Key;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.material.Colorable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.logging.Level;

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
    protected final CruxStatsModule CRUX_STATS = new CruxStatsModule();
    protected final CruxEntitiesModule CRUX_ENTITIES = new CruxEntitiesModule();
    protected final CruxEnchantsModule CRUX_ENCHANTS = new CruxEnchantsModule();
    protected final CruxBlocksModule CRUX_BLOCKS = new CruxBlocksModule(CruxBlockTicker.simple(this));
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
    public CruxStatsModule cruxStats() {
        return CRUX_STATS;
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

        CRUX_STRUCTURES.registerCommands(this, structureManager);
        MODULES.register(
            CRUX_MAIN,
            CRUX_CONFIGS,
            CRUX_ITEMS,
            CRUX_MENUS,
            CRUX_POTIONS,
            CRUX_ATTRIBUTES,
            CRUX_STATS,
            CRUX_ENTITIES,
            CRUX_ENCHANTS,
            CRUX_BLOCKS,
            CRUX_STRUCTURES,
            //CRUX_EXTERNAL,
            CRUX_ADVANCEMENTS,
            CRUX_GENERATION,
            CRUX_WORLDS
        ).load(this);

        loadTags();
        loadBlockSoundGroups();

        new CruxCoreCommands(this).register(this);
        new FAWECommands(this).register(this);

        super.onLoad();
        CfgRegistries.SIMPLE_REGISTRY.forEach(reg ->{
            reg.registerFileHandler(DynamicUpdater.class, new FileDynamicUpdater());
            reg.registerFileHandler(DynamicItemUpdater.class, new FileDynamicItemUpdater());
        });

        EntityMemory.registerFunction(this, (m) ->{
            if(!(m instanceof PlayerMemory mem)) return;
            mem.getDataHolders().register(new PlayerCruxStatHolder(mem));
        });
    }

    @Override
    public void enabled() {
        //enable modules.
        //they will automatically add in their listeners
        MODULES.enable(this);
        CRUX_ITEMS.registerGeneralDisplayFormatter();

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
        loadBuiltInTags();
        new ItemTagLoader().loadConfiguration(
            new CruxFolder(this, "tags/item").file()
        );
        new BlockTagLoader().loadConfiguration(
            new CruxFolder(this, "tags/block").file()
        );
        new EntityTagLoader().loadConfiguration(
            new CruxFolder(this, "tags/entity").file()
        );
    }

    public void loadBuiltInTags(){
        registerEntityTag(new BaseEntityTag(Crux.key("monster")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return entity instanceof Monster;
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("enemy")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return entity instanceof Enemy;
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("animal")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return entity instanceof Animals;
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("colorable")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return entity instanceof Colorable;
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("collar_colorable")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return entity instanceof CollarColorable;
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("boss")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return entity instanceof Boss;
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("crux_mob_custom")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return CruxMob.is(entity);
            }
        });
        for(MobCategory category : CruxEntityRegistries.MOB_CATEGORY){
            registerEntityTag(
                new BaseEntityTag(Key.key(category.key().namespace(), "crux_mob_category/" + category.key().value())) {
                    @Override
                    public boolean isTagged(@NotNull Entity entity) {
                        return CruxMob.isInCategory(entity, category);
                    }
                }
            );
        }
        registerBlockTag(new BaseBlockTag(Crux.key("empty")) {
            @Override
            public boolean isTagged(@NotNull CruxedBlock block) {
                return block.getBlock().isEmpty();
            }
        });
        registerBlockTag(new BaseBlockTag(Crux.key("replaceable")) {
            @Override
            public boolean isTagged(@NotNull CruxedBlock block) {
                return block.getBlock().isReplaceable();
            }
        });
        registerBlockTag(new BaseBlockTag(Crux.key("solid")) {
            @Override
            public boolean isTagged(@NotNull CruxedBlock block) {
                return block.getBlock().isSolid();
            }
        });
        registerBlockTag(new BaseBlockTag(Crux.key("liquid")) {
            @Override
            public boolean isTagged(@NotNull CruxedBlock block) {
                return block.getBlock().isLiquid();
            }
        });
        registerBlockTag(new BaseBlockTag(Crux.key("collidable")) {
            @Override
            public boolean isTagged(@NotNull CruxedBlock block) {
                return block.getBlock().isCollidable();
            }
        });
        registerBlockTag(new BaseBlockTag(Crux.key("burnable")) {
            @Override
            public boolean isTagged(@NotNull CruxedBlock block) {
                return block.getBlock().isBurnable();
            }
        });
    }

    private void registerEntityTag(EntityTag tag){
        CruxRegistries.ENTITY_TAG.register(tag);
        Crux.log(Level.INFO, "Registered built-in entity tag: " + tag.key());
    }

    private void registerBlockTag(BlockTag tag){
        CruxRegistries.BLOCK_TAG.register(tag);
        Crux.log(Level.INFO, "Registered built-in block tag: " + tag.key());
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

        new NumberLootTableLoader().loadConfiguration(
            new CruxFolder(this, "number_loot_tables").file()
        );

        loadBlockSoundGroups();

        new NaturalEntityGroupGroupCfgLoader().loadConfiguration(
            new CruxFolder(this, "entity_spawn_groups").file()
        );

        MODULES.reload(this);
        CruxRegistries.PLUGIN.forEach(plugin ->{
            if(plugin instanceof CruxCore) return;
            plugin.reload(this);
        });

        new LootTableLoader().loadConfiguration(
            new CruxFolder(this, "loot_tables").file()
        );

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
