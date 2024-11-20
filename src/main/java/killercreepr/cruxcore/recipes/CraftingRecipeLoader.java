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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CraftingRecipeLoader {
    public void load(@NotNull DataFile cfg, @NotNull Server server){
        if(!(cfg.getRoot() instanceof FileObject root)) return;
        root.forEach((key, element) ->{
            if(!(element instanceof FileObject o)) return;
            List<String> shape = cfg.fileRegistry().deserializeFromFile(
                new TypeToken<List<String>>(){}.getType(), o.get("shape")
            );
            if(shape == null) return;
            Map<String, DynamicItem> ingredients = cfg.fileRegistry().deserializeFromFile(
                new TypeToken<Map<String, DynamicItem>>(){}.getType(), o.get("ingredients")
            );
            if(ingredients==null) return;
            DynamicItem result = cfg.fileRegistry().deserializeFromFile(DynamicItem.class, o.get("result"));
            if(result==null) return;
            TextParserContext ctx = TextParserContext.empty();
            ItemStack resultItem = result.buildItem(ctx);
            if(resultItem == null) return;

            Key recipeKey = Crux.key(key);

            ShapedRecipe recipe = new ShapedRecipe(CruxKey.key(recipeKey), resultItem);
            recipe.shape(shape.toArray(new String[0]));
            ingredients.forEach((id, ingredient) ->{
                ItemStack ingredientItem = ingredient.buildItem(ctx);
                if(ingredientItem==null) return;
                recipe.setIngredient(id.charAt(0), ingredientItem);
            });

            Recipe r = server.getRecipe(CruxKey.key(recipeKey));
            if(r != null) server.removeRecipe(CruxKey.key(recipeKey), false);
            server.addRecipe(recipe, false);
            Crux.log(Level.INFO, "Registered crafting recipe: " + recipeKey);
        });
        //server.updateRecipes();
    }
}
