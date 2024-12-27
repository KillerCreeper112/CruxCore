package killercreepr.cruxcore.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxcore.structure.component.ActiveStructureParticleBox;
import killercreepr.cruxcore.structure.component.ActiveStructureParticleOuterBox;
import killercreepr.cruxcore.structure.component.StructureParticleBox;
import killercreepr.cruxcore.structure.component.StructureParticleOuterBox;

import java.util.function.UnaryOperator;

public class CruxCoreComponents {
    public static void register(){}
    public static final DataComponentType<StructureParticleBox> STRUCTURE_PARTICLE_BOX = register("structure/particle_box", builder -> builder);
    public static final DataComponentType<ActiveStructureParticleBox> ACTIVE_STRUCTURE_PARTICLE_BOX = register("structure/active_particle_box", builder -> builder);
    public static final DataComponentType<StructureParticleOuterBox> STRUCTURE_PARTICLE_OUTER_BOX = register("structure/particle_outer_box", builder -> builder);
    public static final DataComponentType<ActiveStructureParticleOuterBox> ACTIVE_STRUCTURE_PARTICLE_OUTER_BOX = register("structure/active_particle_outer_box", builder -> builder);
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}
