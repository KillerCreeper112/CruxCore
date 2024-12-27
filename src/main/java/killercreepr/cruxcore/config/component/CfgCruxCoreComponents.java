package killercreepr.cruxcore.config.component;

import killercreepr.crux.api.component.TypedDataComponent;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import killercreepr.cruxconfig.config.bukkit.registry.FileDataComponentRegistry;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.component.CruxCoreComponents;
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
    }
}
