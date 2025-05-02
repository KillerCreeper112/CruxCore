package killercreepr.cruxcore.recipes;

import io.papermc.paper.potion.PotionMix;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxKey;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.component.PotionComponents;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BrewingRecipeLoader {
    public static final Map<Key, DisplayMix> DISPLAY_POTION_MIXES = new HashMap<>();

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
    }

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
