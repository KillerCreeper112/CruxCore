package killercreepr.cruxcore.config;

import killercreepr.crux.core.Crux;
import killercreepr.cruxadvancements.api.advancement.objective.AdvancementObjective;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.config.handler.FileAdvancementObjective;
import killercreepr.cruxadvancements.core.config.handler.FileSimpleAdvanceObjective;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.advancement.objective.DamageByProxyObjective;
import killercreepr.cruxcore.advancement.objective.KillByProxyObjective;
import killercreepr.cruxcore.advancement.objective.StructureEnterObjective;
import killercreepr.cruxcore.advancement.objective.StructureLeaveObjective;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancementObjectiveCfg {
    public static void registerObjectives(FileAdvancementObjective file){
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("structure_enter")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new StructureEnterObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("structure_leave")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new StructureLeaveObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("kill_by_proxy")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new KillByProxyObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("damage_by_proxy")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new DamageByProxyObjective(data, maxProgress);
            }
        });
    }

    private static Key key(String id){
        return Crux.key(id);
    }
}
