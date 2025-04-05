package killercreepr.cruxcore.advancement.objective;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import killercreepr.cruxcore.api.event.EntityDeathByOwnerEvent;
import org.jetbrains.annotations.NotNull;

public class KillByProxyObjective extends GenericEventObjective<EntityDeathByOwnerEvent> {
    public KillByProxyObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(EntityDeathByOwnerEvent event) {
        return LootContext.builder()
            .info(
                DataExchange.builder()
                    .putAll(event.getDamage(), "damage")
                    .putAll(event.getDamageSource(), "damage_source")
                    .putAll(event.getCause(), "cause")
                    .putAll(event.getVictim(), "victim")
                    .putAll(event.getDamager(), "damager")
                    .putAll(event.getOwner(), "owner")
                    .build()
            )
            .looter(event.getOwner())
            .looted(event.getVictim())
            .build();
    }
}
