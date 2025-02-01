package killercreepr.cruxcore.structure.component;

import killercreepr.crux.api.entity.tag.EntityTag;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.component.StructureComponent;
import org.bukkit.entity.Entity;

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
