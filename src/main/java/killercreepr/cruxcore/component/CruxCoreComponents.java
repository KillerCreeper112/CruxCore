package killercreepr.cruxcore.component;

import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxcore.structure.component.*;

import java.util.function.UnaryOperator;

public class CruxCoreComponents {
    public static void register(){}
    public static final DataComponentType<StructureParticleBox> STRUCTURE_PARTICLE_BOX = register("structure/particle_box", builder -> builder);
    public static final DataComponentType<ActiveStructureParticleBox> ACTIVE_STRUCTURE_PARTICLE_BOX = register("structure/active_particle_box", builder -> builder);
    public static final DataComponentType<StructureParticleOuterBox> STRUCTURE_PARTICLE_OUTER_BOX = register("structure/particle_outer_box", builder -> builder);
    public static final DataComponentType<ActiveStructureParticleOuterBox> ACTIVE_STRUCTURE_PARTICLE_OUTER_BOX = register("structure/active_particle_outer_box", builder -> builder);
    public static final DataComponentType<StructureDenyMobSpawns> STRUCTURE_DENY_MOB_SPAWNS = register("structure/deny_mob_spawns", builder -> builder);
    public static final DataComponentType<Communicator> STRUCTURE_ENTER_MESSAGE = register("structure/enter_message", builder -> builder);
    public static final DataComponentType<Communicator> STRUCTURE_LEAVE_MESSAGE = register("structure/leave_message", builder -> builder);
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}
