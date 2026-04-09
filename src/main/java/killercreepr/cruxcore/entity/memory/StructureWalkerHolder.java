package killercreepr.cruxcore.entity.memory;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerDataHolder;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StructureWalkerHolder extends PlayerDataHolder {
    public static final Key KEY = Crux.key("structure_walker");
    public StructureWalkerHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }

    public StructureWalkerHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    protected long lastTicked;
    //protected StoredStructure lastStructure;
    protected final Set<StoredStructure> lastStructures = new HashSet<>();

    public Set<StoredStructure> getLastStructures() {
        return lastStructures;
    }

    public long getLastTicked() {
        return lastTicked;
    }

    public void setLastTicked(long lastTicked) {
        this.lastTicked = lastTicked;
    }

    /*public StoredStructure getLastStructure() {
        return lastStructure;
    }

    public void setLastStructure(StoredStructure lastStructure) {
        this.lastStructure = lastStructure;
    }*/
}
