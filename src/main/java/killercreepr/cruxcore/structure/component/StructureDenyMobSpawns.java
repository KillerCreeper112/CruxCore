package killercreepr.cruxcore.structure.component;

import killercreepr.crux.api.entity.tag.EntityTag;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxcore.component.CruxCoreComponents;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.component.StructureComponent;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class StructureDenyMobSpawns implements StructureComponent, StoredStructureComponent {
    protected final EntityTag filter;

    public StructureDenyMobSpawns(EntityTag filter) {
        this.filter = filter;
    }

    public boolean test(Entity e){
        if(filter == null) return true;
        return filter.isTagged(e);
    }
}
