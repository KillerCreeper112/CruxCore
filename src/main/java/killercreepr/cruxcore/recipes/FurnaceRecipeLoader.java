package killercreepr.cruxcore.recipes;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxKey;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import net.kyori.adventure.key.Key;
import org.bukkit.Server;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class FurnaceRecipeLoader {
    public void load(@NotNull DataFile cfg, @NotNull Server server){
        if(!(cfg.getRoot() instanceof FileObject root)) return;
        root.forEach((key, element) ->{
            if(!(element instanceof FileObject o)) return;
            Collection<DynamicItem> ingredients = cfg.fileRegistry().deserializeFromFile(
                new TypeToken<Collection<DynamicItem>>(){}.getType(), o.get("ingredients")
            );
            if(ingredients==null) return;
            DynamicItem result = cfg.fileRegistry().deserializeFromFile(DynamicItem.class, o.get("result"));
            if(result==null) return;
            TextParserContext ctx = TextParserContext.empty();
            ItemStack resultItem = result.buildItem(ctx);
            if(resultItem == null) return;

            Key recipeKey = Crux.key(key);
            List<ItemStack> ingredientList = new ArrayList<>();
            ingredients.forEach((ingredient) ->{
                ItemStack ingredientItem = ingredient.buildItem(ctx);
                if(ingredientItem==null){
                    Crux.log(Level.SEVERE, "Cannot built ingredient, " + ingredient + "!");
                    return;
                }
                ingredientList.add(ingredientItem);
            });
            FurnaceRecipe recipe = new FurnaceRecipe(
                CruxKey.key(recipeKey), resultItem, new RecipeChoice.ExactChoice(ingredientList),
                o.get("experience").getAsFloat(), o.get("cooking_time").getAsInt()
            );

            Recipe r = server.getRecipe(CruxKey.key(recipeKey));
            if(r != null) server.removeRecipe(CruxKey.key(recipeKey), false);
            server.addRecipe(recipe, false);
            Crux.log(Level.INFO, "Registered furnace recipe: " + recipeKey);

            if(o.has("blasting_experience")){
                BlastingRecipe blastingRecipe = new BlastingRecipe(
                    CruxKey.key(Crux.key("blasting/" + key)), resultItem, new RecipeChoice.ExactChoice(ingredientList),
                    o.get("blasting_experience").getAsFloat(), o.get("blasting_cooking_time").getAsInt()
                );
                r = server.getRecipe(blastingRecipe.getKey());
                if(r != null) server.removeRecipe(blastingRecipe.getKey(), false);
                server.addRecipe(blastingRecipe, false);
                Crux.log(Level.INFO, "Registered blasting recipe: " + blastingRecipe.key());
            }
            if(o.has("smoking_experience")){
                SmokingRecipe blastingRecipe = new SmokingRecipe(
                    CruxKey.key(Crux.key("smoking/" + key)), resultItem, new RecipeChoice.ExactChoice(ingredientList),
                    o.get("smoking_experience").getAsFloat(), o.get("smoking_cooking_time").getAsInt()
                );
                r = server.getRecipe(blastingRecipe.getKey());
                if(r != null) server.removeRecipe(blastingRecipe.getKey(), false);
                server.addRecipe(blastingRecipe, false);
                Crux.log(Level.INFO, "Registered smoking recipe: " + blastingRecipe.key());
            }
        });
    }
}
