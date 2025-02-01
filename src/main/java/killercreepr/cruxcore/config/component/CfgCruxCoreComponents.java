package killercreepr.cruxcore.config.component;

import killercreepr.crux.api.component.TypedDataComponent;
import killercreepr.crux.api.entity.tag.EntityTag;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import killercreepr.cruxconfig.config.bukkit.registry.FileDataComponentRegistry;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.component.CruxCoreComponents;
import killercreepr.cruxcore.structure.component.StructureDenyMobSpawns;
import killercreepr.cruxcore.structure.component.StructureParticleBox;
import killercreepr.cruxcore.structure.component.StructureParticleOuterBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CfgCruxCoreComponents {
    public static void register(@NotNull FileDataComponentRegistry registry){
        registry.register("structure/particle_box", new FileDataComponentType<StructureParticleBox>(){
            @Override
            public @Nullable TypedDataComponent<StructureParticleBox> deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(CruxCoreComponents.STRUCTURE_PARTICLE_BOX, new StructureParticleBox());
            }
        });
        registry.register("structure/particle_outer_box", new FileDataComponentType<StructureParticleOuterBox>(){
            @Override
            public @Nullable TypedDataComponent<StructureParticleOuterBox> deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(CruxCoreComponents.STRUCTURE_PARTICLE_OUTER_BOX, new StructureParticleOuterBox());
            }
        });
        registry.register("structure/deny_mob_spawns", new FileDataComponentType<StructureDenyMobSpawns>(){
            @Override
            public @Nullable TypedDataComponent<StructureDenyMobSpawns> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject o) {
                EntityTag filter = ctx.getRegistry().deserializeFromFile(EntityTag.class, o.get("filter"));
                return TypedDataComponent.create(CruxCoreComponents.STRUCTURE_DENY_MOB_SPAWNS, new StructureDenyMobSpawns(filter));
            }
        });
    }
}
