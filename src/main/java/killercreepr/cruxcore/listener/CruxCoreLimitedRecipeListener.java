package killercreepr.cruxcore.listener;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxadvancements.core.util.AdvanceFrame;
import killercreepr.cruxadvancements.core.util.AdvancementMsg;
import killercreepr.cruxcrafting.api.crafting.CruxCraftingRecipeManager;
import killercreepr.cruxcrafting.api.crafting.recipe.CruxCraftingRecipe;
import killercreepr.cruxcrafting.api.event.EntityDiscoverRecipeEvent;
import killercreepr.cruxcrafting.core.listener.LimitedAccessRecipeListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CruxCoreLimitedRecipeListener extends LimitedAccessRecipeListener implements Listener {
    public CruxCoreLimitedRecipeListener(CruxCraftingRecipeManager recipeManager) {
        super(recipeManager);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastRecipeUnlock.remove(event.getPlayer().getUniqueId());
    }

    protected final Map<UUID, Long> lastRecipeUnlock = new HashMap<>();
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDiscoverRecipe(EntityDiscoverRecipeEvent event) {
        if(!event.getRecipeManager().equals(recipeManager)) return;
        if(!(event.getEntity() instanceof Player p)) return;
        Long time = lastRecipeUnlock.get(p.getUniqueId());
        if(time != null && CruxMath.hasOccurredWithin(time, 5)) return;
        lastRecipeUnlock.put(p.getUniqueId(), System.currentTimeMillis());

        ItemStack icon;
        if(event.getRecipe() instanceof CruxCraftingRecipe r){
            icon = r.getDisplayedResultItems().getFirst();
        }else{
            icon = new ItemStack(Material.KNOWLEDGE_BOOK);
        }

        new AdvancementMsg()
            .icon(icon)
            .frame(AdvanceFrame.TASK)
            .title("New Custom Recipe Unlocked!")
            .displayToast(p);
    }

}
