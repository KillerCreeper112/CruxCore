package killercreepr.cruxcore.structure.component;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxcore.component.CruxCoreComponents;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.component.StructureComponent;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.jetbrains.annotations.NotNull;

public class StructureParticleOuterBox implements StructureComponent, StoredStructureComponent {
    @Override
    public void onActiveCreated(@NotNull ActiveStructure structure) {
        structure.set(CruxCoreComponents.ACTIVE_STRUCTURE_PARTICLE_OUTER_BOX, new ActiveStructureParticleOuterBox(structure));
    }

    @Override
    public void onCreated(@NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation, @NotNull StoredStructure stored) {
        stored.set(CruxCoreComponents.STRUCTURE_PARTICLE_OUTER_BOX, this);
    }
}
