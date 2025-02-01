package killercreepr.cruxcore.api.event;

import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveStructureEvent extends Event {
    public static final HandlerList HANDLER_LIST = new HandlerList();
    protected final Player player;
    protected final StoredStructure structure;

    public PlayerLeaveStructureEvent(Player player, StoredStructure structure) {
        this.player = player;
        this.structure = structure;
    }

    public Player getPlayer() {
        return player;
    }

    public StoredStructure getStructure() {
        return structure;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
