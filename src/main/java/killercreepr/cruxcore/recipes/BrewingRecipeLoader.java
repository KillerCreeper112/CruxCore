package killercreepr.cruxcore.recipes;

import io.papermc.paper.potion.PotionMix;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxKey;
import killercreepr.crux.paper.ItemHolder;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import killercreepr.cruxpotions.core.component.PotionComponents;
import killercreepr.cruxpotions.core.potions.DescribedPotion;
import killercreepr.cruxpotions.core.registries.CruxPotionRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BrewingRecipeLoader {
    public static final Map<Key, DisplayMix> DISPLAY_POTION_MIXES = new HashMap<>();
    public static final Map<Key, DisplayMix> NON_BREW_DISPLAY_POTION_MIXES = new HashMap<>();
    public static final Map<Key, PotionMix> SPLASH_POTION_MIXES = new HashMap<>();

    public void load(@NotNull DataFile cfg, @NotNull Server server){
        if(!(cfg.getRoot() instanceof FileObject root)) return;
        root.forEach((key, element) ->{
            if(!(element instanceof FileObject o)) return;
            Key input = cfg.fileRegistry().deserializeFromFile(Key.class, o.get("input"));
            Key ingredient = cfg.fileRegistry().deserializeFromFile(Key.class, o.get("ingredient"));
            Key basePotionType = cfg.fileRegistry().deserializeFromFile(Key.class, o.get("base_potion_type"));
            boolean display = o.getOrDefaultObject(Boolean.class, "display", false);

            DynamicItem result = cfg.fileRegistry().deserializeFromFile(DynamicItem.class, o.get("result"));
            if(result==null) return;
            TextParserContext ctx = TextParserContext.empty();
            ItemStack resultItem = result.buildItem(ctx);
            if(resultItem == null) return;

            Key recipeKey = Crux.key(key);

            server.getPotionBrewer().removePotionMix(CruxKey.key(recipeKey));
            PotionMix mix = new PotionMix(CruxKey.key(recipeKey), resultItem,
                PotionMix.createPredicateChoice(item ->{
                    if(basePotionType != null){
                        if(!input.equals(Crux.handlers().item().getType(item))) return false;
                        if(!(item.getItemMeta() instanceof PotionMeta meta)) return false;
                        return meta.getBasePotionType() != null && meta.getBasePotionType().key().equals(basePotionType);
                    }

                return input.equals(Crux.handlers().item().getType(item));
            }), PotionMix.createPredicateChoice(item ->{
                return ingredient.equals(Crux.handlers().item().getType(item));
            }));
            server.getPotionBrewer().addPotionMix(mix);

            //SPLASH
            if(resultItem.getType() == Material.POTION){
                Key itemKey = Crux.handlers().item().getType(resultItem);
                ItemHolder holder = Crux.handlers().item().getItem(
                    Crux.key(
                        itemKey.namespace() + ":" + itemKey.value().replace("_potion", "_splash_potion")
                    )
                );
                if(holder != null){
                    recipeKey = Crux.key(holder.key().key().value());
                    server.getPotionBrewer().removePotionMix(CruxKey.key(recipeKey));
                    PotionMix mixSplash = new PotionMix(CruxKey.key(recipeKey), holder.value(),
                        PotionMix.createPredicateChoice(item ->{
                            return itemKey.equals(Crux.handlers().item().getType(item));
                        }), PotionMix.createPredicateChoice(item ->{
                        return Key.key("gunpowder").equals(Crux.handlers().item().getType(item));
                    }));
                    server.getPotionBrewer().addPotionMix(mixSplash);
                    SPLASH_POTION_MIXES.put(mixSplash.key(), mixSplash);
                }
            }
            //SPLASH

            if(display){
                var potList = CruxItem.wrap(resultItem).getOrDefaultData(PotionComponents.STORED_CRUX_POTIONS);
                if(potList != null && !potList.isEmpty()){
                    CruxPotion cruxPotion = null;
                    for(var pot : potList){
                        cruxPotion = pot.getPotion();
                        break;
                    }
                    ItemStack displayInput = Crux.handlers().item().getItem(input).value();
                    ItemStack displayIngredient = Crux.handlers().item().getItem(ingredient).value();

                    if(basePotionType != null && displayInput.getItemMeta() instanceof PotionMeta meta){
                        meta.setBasePotionType(Registry.POTION.get(basePotionType));
                        displayInput.setItemMeta(meta);
                    }

                    DisplayMix displayMix = new DisplayMix(
                        mix, displayInput, displayIngredient, cruxPotion
                    );

                    DISPLAY_POTION_MIXES.put(mix.key(), displayMix);
                }
            }

            Crux.log(Level.INFO, "Registered custom brewing recipe: " + recipeKey);
        });

        for(CruxPotion potion : CruxPotionRegistries.POTION){
            if(!(potion instanceof DescribedPotion)) continue;
            if(DISPLAY_POTION_MIXES.containsKey(potion.key())) continue;
            DisplayMix displayMix = new DisplayMix(
                new PotionMix(
                    CruxKey.key(potion.key()),
                    CruxItem.create(Material.POTION)
                        .customName("Potion of " + potion.getName())
                        .editThis(c ->{
                            c.set(PotionComponents.STORED_CRUX_POTIONS, List.of(StoredPotion.storedPotion(potion, 1200, 0)));
                            Crux.handlers().item().update(c.item());
                        })
                        .item(),
                    RecipeChoice.empty(), RecipeChoice.empty()
                ),NOT_BREWABLE, NOT_BREWABLE, potion
            );
            NON_BREW_DISPLAY_POTION_MIXES.put(potion.key(), displayMix);
        }
    }

    protected final static ItemStack NOT_BREWABLE = CruxItem.create(Material.BARRIER)
        .itemName("<red>This potion is not brewable")
        .item();
    public static class DisplayMix{
        protected final PotionMix mix;
        protected final ItemStack input;
        protected final ItemStack ingredient;
        protected final CruxPotion cruxPotion;

        public DisplayMix(PotionMix mix, ItemStack input, ItemStack ingredient, CruxPotion cruxPotion) {
            this.mix = mix;
            this.input = input;
            this.ingredient = ingredient;
            this.cruxPotion = cruxPotion;
        }

        public CruxPotion getCruxPotion() {
            return cruxPotion;
        }

        public PotionMix getMix() {
            return mix;
        }

        public ItemStack getInput() {
            return input;
        }

        public ItemStack getIngredient() {
            return ingredient;
        }
    }
}
