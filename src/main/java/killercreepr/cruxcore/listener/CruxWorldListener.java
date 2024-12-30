package killercreepr.cruxcore.listener;

import killercreepr.cruxblocks.api.block.registry.CruxBlockRegistry;
import killercreepr.cruxblocks.core.world.module.SimpleCruxBlocksWorldModule;
import killercreepr.cruxworlds.api.event.CruxWorldPreCreateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CruxWorldListener implements Listener {
    protected final CruxBlockRegistry blockRegistry;
    public CruxWorldListener(CruxBlockRegistry blockRegistry) {
        this.blockRegistry = blockRegistry;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCruxWorldPreCreate(CruxWorldPreCreateEvent event) {
        event.getModuleCreators().add(world -> new SimpleCruxBlocksWorldModule(
            world, blockRegistry
        ));
    }
}
