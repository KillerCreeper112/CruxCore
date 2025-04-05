package killercreepr.cruxcore.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import killercreepr.cruxcore.api.event.PlayerEnterStructureEvent;
import org.jetbrains.annotations.NotNull;

public class StructureEnterObjective extends GenericEventObjective<PlayerEnterStructureEvent> {
    public StructureEnterObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(PlayerEnterStructureEvent event) {
        return LootContext.builder()
            .looter(event.getPlayer())
            .looted(event.getStructure())
            .build();
    }
}
