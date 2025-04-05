package killercreepr.cruxcore.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import killercreepr.cruxcore.api.event.PlayerLeaveStructureEvent;
import org.jetbrains.annotations.NotNull;

public class StructureLeaveObjective extends GenericEventObjective<PlayerLeaveStructureEvent> {
    public StructureLeaveObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(PlayerLeaveStructureEvent event) {
        return LootContext.builder()
            .looter(event.getPlayer())
            .looted(event.getStructure())
            .build();
    }
}
