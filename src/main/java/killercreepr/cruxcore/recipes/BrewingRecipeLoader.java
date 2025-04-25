package killercreepr.cruxcore.recipes;

import io.papermc.paper.potion.PotionMix;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxKey;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import net.kyori.adventure.key.Key;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class BrewingRecipeLoader {
    public void load(@NotNull DataFile cfg, @NotNull Server server){
        if(!(cfg.getRoot() instanceof FileObject root)) return;
        root.forEach((key, element) ->{
            if(!(element instanceof FileObject o)) return;
            Key input = cfg.fileRegistry().deserializeFromFile(Key.class, o.get("input"));
            Key ingredient = cfg.fileRegistry().deserializeFromFile(Key.class, o.get("ingredient"));
            Key basePotionType = cfg.fileRegistry().deserializeFromFile(Key.class, o.get("base_potion_type"));

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
            Crux.log(Level.INFO, "Registered custom brewing recipe: " + recipeKey);
        });
    }
}
