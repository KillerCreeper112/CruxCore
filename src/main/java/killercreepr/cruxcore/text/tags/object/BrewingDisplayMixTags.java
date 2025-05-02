package killercreepr.cruxcore.text.tags.object;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringListResolver;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxColor;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcore.recipes.BrewingRecipeLoader;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BrewingDisplayMixTags implements ObjectTag<BrewingRecipeLoader.DisplayMix> {
    @Override
    public @NotNull Class<BrewingRecipeLoader.DisplayMix> getObjectType() {
        return BrewingRecipeLoader.DisplayMix.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("cfg_brew_display_mix_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(BrewingRecipeLoader.@NotNull DisplayMix object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("result", (args, ctx) ->{
                return Crux.handlers().item().getType(object.getMix().getResult()) + "";
            }))
            .add(Tag.string("result_color", (args, ctx) ->{
                var color = CruxItem.wrap(Crux.handlers().item().update(object.getMix().getResult()))
                    .color();
                return color == null ? "none" : CruxColor.colorToHex(color);
            }))
            .add(Tag.string("description", (args, ctx) ->{
                return object.getCruxPotion().getDescription() + "";
            }))
            .add(Tag.string("name", (args, ctx) ->{
                return object.getCruxPotion().getName() + "";
            }))
            .add(Tag.string("ingredient", (args, ctx) ->{
                return Base64.getEncoder().encodeToString(object.getIngredient().serializeAsBytes());
            }))
            .add(Tag.string("input", (args, ctx) ->{
                return Base64.getEncoder().encodeToString(object.getInput().serializeAsBytes());
            }))
            ;
    }

    @Override
    public @Nullable TagContainer<StringListResolver> requestStringLists(BrewingRecipeLoader.@NotNull DisplayMix object, @NotNull TagParser tags) {
        return TagContainer.stringList(tags)
            .add(Tag.stringList("result_lore", (args, ctx) ->{
                ItemMeta meta = Crux.handlers().item().update(object.getMix().getResult()).getItemMeta();
                if(meta == null) return List.of();
                var lore = meta.lore();
                if(lore == null || lore.isEmpty()) return List.of();

                List<String> list = new ArrayList<>();
                for(var c : lore){
                    list.add(Crux.format().serialize(c));
                }
                return list;
            }))
            ;
    }
}
