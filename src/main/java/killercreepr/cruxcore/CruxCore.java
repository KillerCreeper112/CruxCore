package killercreepr.cruxcore;

import com.destroystokyo.paper.MaterialTags;
import com.google.common.reflect.TypeToken;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.entity.CollarColorable;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.block.CruxedBlock;
import killercreepr.crux.api.block.tag.BlockTag;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.data.AutoSavable;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.data.Reloadable;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.entity.tag.EntityTag;
import killercreepr.crux.api.event.ServerShutDownEvent;
import killercreepr.crux.api.item.tag.ItemTag;
import killercreepr.crux.api.text.resolver.StringListResolver;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.block.tag.BaseBlockTag;
import killercreepr.crux.core.communication.lang.SimpleCreateLang;
import killercreepr.crux.core.entity.memory.standard.PlayerBossBarHolder;
import killercreepr.crux.core.entity.tag.BaseEntityTag;
import killercreepr.crux.core.item.tag.BaseItemTag;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.plugin.module.CruxMainModule;
import killercreepr.crux.core.registries.CruxModuleRegistry;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxWorldUtil;
import killercreepr.cruxadvancements.core.CruxAdvancementsModule;
import killercreepr.cruxadvancements.core.config.CruxAdvanceCfgData;
import killercreepr.cruxattributes.core.CruxAttributesModule;
import killercreepr.cruxblocks.core.CruxBlocksModule;
import killercreepr.cruxblocks.core.block.manager.SimpleCruxBlockManager;
import killercreepr.cruxconfig.CruxConfigsModule;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.loader.*;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.command.CruxCoreCommands;
import killercreepr.cruxcore.command.DevCommands;
import killercreepr.cruxcore.command.FAWECommands;
import killercreepr.cruxcore.component.CruxCoreComponents;
import killercreepr.cruxcore.config.AdvancementObjectiveCfg;
import killercreepr.cruxcore.config.CruxCoreConfig;
import killercreepr.cruxcore.config.component.CfgCruxCoreComponents;
import killercreepr.cruxcore.config.data.CfgCmdMenu;
import killercreepr.cruxcore.config.handler.FileDynamicItemUpdater;
import killercreepr.cruxcore.config.handler.FileDynamicUpdater;
import killercreepr.cruxcore.item.updater.DynamicItemUpdater;
import killercreepr.cruxcore.item.updater.DynamicUpdater;
import killercreepr.cruxcore.listener.*;
import killercreepr.cruxcore.menu.StandardCraftingMenuHolder;
import killercreepr.cruxcore.menu.StandardCraftingRecipeListHolder;
import killercreepr.cruxcore.recipes.BrewingRecipeLoader;
import killercreepr.cruxcore.recipes.CraftingRecipeLoader;
import killercreepr.cruxcore.text.tags.StringListResolverHolder;
import killercreepr.cruxcore.text.tags.StringResolverHolder;
import killercreepr.cruxcore.text.tags.object.BrewingDisplayMixTags;
import killercreepr.cruxcrafting.api.crafting.CruxCraftingRecipeManager;
import killercreepr.cruxcrafting.core.CruxCraftingModule;
import killercreepr.cruxcrafting.core.config.CruxCraftingCfg;
import killercreepr.cruxcrafting.core.config.loader.CruxCraftingIngredientLoader;
import killercreepr.cruxcrafting.core.config.loader.CruxCraftingRecipeLoader;
import killercreepr.cruxcrafting.core.crafting.LimitedCraftingRecipeManager;
import killercreepr.cruxcrafting.core.entity.memory.RecipesHolder;
import killercreepr.cruxcrafting.core.listener.CraftingListener;
import killercreepr.cruxcrafting.core.registries.CruxCraftingRegistries;
import killercreepr.cruxenchants.core.CruxEnchantsModule;
import killercreepr.cruxentities.CruxEntitiesModule;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import killercreepr.cruxexternal.CruxExternalModule;
import killercreepr.cruxform.core.CruxFormModule;
import killercreepr.cruxgeneration.CruxGenerationModule;
import killercreepr.cruxitems.core.CruxItemsModule;
import killercreepr.cruxitems.core.registries.CruxItemRegistries;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxpotions.core.CruxPotionsModule;
import killercreepr.cruxstatistics.core.CruxStatisticsModule;
import killercreepr.cruxstatistics.core.statistic.PlayerCruxStatisticHolder;
import killercreepr.cruxstats.core.CruxStatsModule;
import killercreepr.cruxstats.core.stat.PlayerCruxStatHolder;
import killercreepr.cruxstructures.core.CruxStructuresModule;
import killercreepr.cruxstructures.core.manager.StructureManager;
import killercreepr.cruxtickables.core.CruxTickablesModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.core.CruxWorldsModule;
import killercreepr.cruxworlds.core.command.CruxWorldsCommands;
import killercreepr.cruxworlds.core.config.loader.NaturalEntityGroupGroupCfgLoader;
import killercreepr.cruxworlds.core.world.manager.SimpleCruxWorldManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.LeavesBlock;
import org.bukkit.EntityEffect;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.block.data.type.CraftLeaves;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class CruxCore extends CruxPlugin implements Listener, LangProvider {
    private static CruxCore instance;
    public static CruxCore inst(){ return instance; }
    public static CruxCore core(){
        return instance;
    }

    protected final SimpleCruxWorldManager worldManager = new SimpleCruxWorldManager(this);

    public CruxWorldManager worldManager() {
        return worldManager;
    }

    protected final StructureManager structureManager = new StructureManager(this, worldManager);

    public StructureManager structureManager() {
        return structureManager;
    }

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
    protected final CruxBlocksModule CRUX_BLOCKS = new CruxBlocksModule(worldManager);
    protected final CruxStructuresModule CRUX_STRUCTURES = new CruxStructuresModule();
    protected final CruxExternalModule CRUX_EXTERNAL = new CruxExternalModule();
    protected final CruxAdvancementsModule CRUX_ADVANCEMENTS = new CruxAdvancementsModule();
    protected final CruxGenerationModule CRUX_GENERATION = new CruxGenerationModule();
    protected final CruxWorldsModule CRUX_WORLDS = new CruxWorldsModule();
    protected final CruxFormModule CRUX_FORM = new CruxFormModule();
    protected final CruxStatisticsModule CRUX_STATISTICS = new CruxStatisticsModule();
    protected final CruxTickablesModule CRUX_TICKABLES = new CruxTickablesModule();
    protected final CruxCraftingModule CRUX_CRAFTING = new CruxCraftingModule();

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
    public CruxCraftingModule cruxCrafting() {
        return CRUX_CRAFTING;
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
    public CruxFormModule cruxForm() {
        return CRUX_FORM;
    }
    public CruxStatisticsModule cruxStatistics() {
        return CRUX_STATISTICS;
    }
    public CruxTickablesModule cruxTickables() {
        return CRUX_TICKABLES;
    }

    public CreateLang LANG = new SimpleCreateLang();

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
            CRUX_EXTERNAL,
            CRUX_ADVANCEMENTS,
            CRUX_GENERATION,
            CRUX_WORLDS,
            CRUX_FORM,
            CRUX_STATISTICS,
            CRUX_TICKABLES,
            CRUX_CRAFTING
        ).load(this);

        loadTags();
        loadBlockSoundGroups();

        new CruxCoreCommands(this).register(this);
        new FAWECommands(this).register(this);
        new DevCommands(this).register(this);
        new CruxWorldsCommands("cruxworld", "crux.cmds.cruxworld.use", List.of("cworld"), worldManager).register(this);

        super.onLoad();
        CfgRegistries.SIMPLE_REGISTRY.forEach(reg ->{
            reg.registerFileHandler(DynamicUpdater.class, new FileDynamicUpdater());
            reg.registerFileHandler(DynamicItemUpdater.class, new FileDynamicItemUpdater());
            reg.registerFileHandler(new AutoFileHandler<>(CfgCmdMenu.class));
        });

        EntityMemory.registerFunction(this, (m) ->{
            if(!(m instanceof PlayerMemory mem)) return;
            mem.getDataHolders().register(new PlayerCruxStatHolder(mem));
            mem.getDataHolders().register(new PlayerBossBarHolder(mem));
            mem.getDataHolders().register(new PlayerCruxStatisticHolder(mem));
            mem.getDataHolders().register(new RecipesHolder(((LimitedCraftingRecipeManager) craftingManager).getMemoryHolderKey(), mem, craftingManager));
        });

        Crux.tags().register(
            new BrewingDisplayMixTags()
        );
        CruxCoreComponents.register();
        CfgCruxCoreComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

        CruxMenusModule menus = cruxMenus();
        menus.menuRegistry().menuHolders().register(new StandardCraftingMenuHolder(
            Crux.key("crafting/standard"),
            "<white><crux_space:-8><font:\"crux:abyss\">2<reset><crux_space:-145>Crafting",
            NumberProvider.constant(27),
            MenuItems.items(new TreeMap<>()), DataExchange.empty(), Set.of()
        ));
        menus.menuRegistry().menuHolders().register(new StandardCraftingRecipeListHolder(
            Crux.key("crafting/standard/recipe_list"),
            "<white><crux_space:-8><font:\"crux:crafting\">1<reset><crux_space:-145>Custom Recipes",
            NumberProvider.constant(45),
            MenuItems.items(new TreeMap<>()), DataExchange.single("crafting_recipe_manager", () -> craftingManager), Set.of()
        ));

        AdvancementObjectiveCfg.registerObjectives(CruxAdvanceCfgData.fileAdvancementObjective());
    }
    //protected LangProvider langProvider;
    protected CruxCoreConfig cfg;
    protected final CruxCraftingRecipeManager craftingManager = CruxCraftingRegistries.RECIPE_MANAGER.register(new LimitedCraftingRecipeManager(Crux.key("standard")));

    public CruxCraftingRecipeManager craftingManager(){
        return craftingManager;
    }
    @Override
    public void enabled() {
        //enable modules.
        //they will automatically add in their listeners
        MODULES.enable(this);
        CRUX_ITEMS.registerGeneralDisplayFormatter();

        cfg = new CruxCoreConfig(this, "config");

        reload();
        registerListeners(
            this,
            structureManager,
            new PlayerDataListener(),
            new ItemStackListener(this),
            worldManager,
            new StructureListener(),
            new CruxWorldListener(cruxBlocks().getBlockRegistry()),
            new SimpleCruxBlockManager(worldManager),

            new CraftingListener(this, craftingManager),
            new CruxCoreLimitedRecipeListener(craftingManager),
            new CustomEventListener(),
            new CustomObjectiveListener()
        );
        worldManager.buildRunnable().runTaskTimerAsynchronously(this, 1L, 1L);
        getServer().getScheduler().runTaskLater(this, () ->{
            cfg.AUTO_LOAD_WORLDS.valueOr(List.of()).forEach(CruxWorldUtil::getOrLoadWorld);
        }, 5L);

        long period = 1200 * 10;
        getServer().getScheduler().runTaskTimer(this, () ->{
            getLogger().info("Saving all player data!");
            for (Player p : getServer().getOnlinePlayers()) {
                PlayerMemory data = PlayerMemory.get(p);
                if(data == null) continue;
                data.getDataHolders().forEach(holder ->{
                    if(holder instanceof Loadable l) l.save();
                });
            }
            for(var plugin : CruxRegistries.PLUGIN){
                if(plugin instanceof AutoSavable auto) auto.save();
            }
        }, period, period);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, (reg) ->{
            cfg.CMD_MENUS.valueOr(List.of()).forEach(cmd ->{
                var builder = Commands.literal(cmd.cmd())
                    .executes(ctx ->{
                        if(!(CruxCoreCommands.getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        var holder = cruxMenus().menuRegistry().menuHolders().get(cmd.menu());
                        if(holder == null){
                            p.sendMessage("Menu " + cmd.menu() + " not found. Contact a developer.");
                            return 0;
                        }
                        holder.open(p);
                        return 1;
                    });
                if(cmd.permission() != null) builder.requires(ctx -> ctx.getSender().hasPermission(cmd.permission()));
                if(cmd.aliases() != null && !cmd.aliases().isEmpty()){
                    reg.registrar().register(builder.build(), cmd.aliases());
                }else reg.registrar().register(builder.build());
            });
        });

        //LANG = new SimpleCreateLang();
        //langProvider = new SimpleLangConfig(this, "lang", this::lang, Object.class);
        //structureManager.buildRunnable().runTaskTimerAsynchronously(this, 20L, 1L);
    }


    @EventHandler(ignoreCancelled = true)
    public void onServerShutDown(ServerShutDownEvent event) {
        for(CruxWorld world : worldManager.getWorlds()){
            try{
                world.onUnload(true);
            }catch (Exception ignored){
                getLogger().severe("ERROR WHILE UNLOADING WORLD: " + world.key());
                ignored.printStackTrace();
            }
        }
    }


    @Override
    public void disabled() {
        super.disabled();
        MODULES.unregisterAll(this);

        //structureManager.saveAllWorlds();
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
        new KeyTagLoader().loadConfiguration(
            new CruxFolder(this, "tags/key").file()
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
        registerEntityTag(new BaseEntityTag(Crux.key("crux_monster")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return CruxMob.isInCategory(entity, MobCategory.MONSTER);
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("crux_enemy")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return CruxMob.isInCategory(entity, MobCategory.ENEMY);
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("crux_neutral")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return CruxMob.isInCategory(entity, MobCategory.NEUTRAL);
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("crux_animal")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return CruxMob.isInCategory(entity, MobCategory.ANIMAL);
            }
        });
        registerEntityTag(new BaseEntityTag(Crux.key("mob")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                if(!(entity instanceof Mob)) return false;
                return !CruxMob.isInCategory(entity, MobCategory.OBJECT, MobCategory.COSMETIC);
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

        registerItemTag(new BaseItemTag(Key.key("pickaxes")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.PICKAXES.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("axes")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.AXES.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("shovels")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.SHOVELS.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("swords")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.SWORDS.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("hoes")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.HOES.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("helmets")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.HELMETS.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("chestplates")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.CHESTPLATES.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("leggings")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.LEGGINGS.isTagged(item);
            }
        });
        registerItemTag(new BaseItemTag(Key.key("boots")) {
            @Override
            public boolean isTagged(@NotNull ItemStack item) {
                return MaterialTags.BOOTS.isTagged(item);
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

    private void registerItemTag(ItemTag tag){
        CruxRegistries.ITEM_TAG.register(tag);
        Crux.log(Level.INFO, "Registered built-in item tag: " + tag.key());
    }

    public void loadBlockSoundGroups(){
        new BlockSoundGroupLoader().loadConfiguration(
            new CruxFolder(this, "block/sound_groups").file()
        );
    }

    public void reloadCfg(){
        cfg.setup();
        Crux.debug = cfg.DEBUG.value().value().shortValue();
        cfg.GLOBAL_STRING_TAGS.valueOr(Map.of()).keySet().forEach(id ->{
            StringResolver resolver = new StringResolverHolder(id, () -> cfg.GLOBAL_STRING_TAGS.valueOr(Map.of()).get(id));
            Crux.format().globalStringResolvers().register(resolver);
        });
        cfg.GLOBAL_STRING_LIST_TAGS.valueOr(Map.of()).keySet().forEach(id ->{
            StringListResolver resolver = new StringListResolverHolder(id, () -> cfg.GLOBAL_STRING_LIST_TAGS.valueOr(Map.of()).get(id));
            Crux.format().globalStringListResolvers().register(resolver);
        });
    }

    @Override
    public void reload() {
        super.reload();
        reloadCfg();
        loadTags();
        //langProvider.reload(this);

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

        structureManager.reload(this);
        for(CruxWorld world : worldManager.getWorlds()){
            if(world instanceof Reloadable r){
                r.reload(this);
            }
        }

        //structureManager.loadConfiguration();

        CruxCore.inst().cruxMenus().menuRegistry().loadConfiguration(
            new CruxFolder(this, "menus").file()
        );

        new CraftingRecipeLoader().load(
            new CruxConfig(this, "crafting_recipes"), getServer()
        );
        Crux.scheduler().runTaskLater(() ->{
            new BrewingRecipeLoader().load(
                new CruxConfig(this, "brewing_recipes"), getServer()
            );
        }, 100L);

        new CruxCraftingIngredientLoader(CruxCraftingCfg.FILE_CRUX_RECIPE_INGREDIENT, ingredient ->{
            CruxCraftingRegistries.RECIPE_INGREDIENT.register(ingredient);
            Crux.log(Level.INFO, "Crux recipe ingredient registered: " + ((Keyed) ingredient).key());
        }).loadFromSingleFile(
            new CruxConfig(this, "crafting/ingredient/crux_ingredients")
        ).loadConfiguration(
            new CruxFolder(this, "crafting/ingredient/crux").file()
        );

        new CruxCraftingRecipeLoader(CruxCraftingCfg.FILE_CRUX_CRAFTING_RECIPE, recipe ->{
            craftingManager.addRecipe(recipe);
            Crux.log(Level.INFO, "Crux crafting recipe registered: " + ((Keyed) recipe).key());
        }).loadConfiguration(
            new CruxFolder(this, "crafting/recipe/global").file()
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
        getServer().updateRecipes();
    }

    protected Collection<DynamicItemUpdater> parsedItemUpdaters;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        reload();
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public @NotNull CreateLang lang() {
        return LANG;
    }
}
