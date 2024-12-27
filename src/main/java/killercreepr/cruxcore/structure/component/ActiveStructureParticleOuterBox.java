package killercreepr.cruxcore.structure.component;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateRectangle;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class ActiveStructureParticleOuterBox implements ManagedTicked {
    protected final ActiveStructure active;
    public ActiveStructureParticleOuterBox(ActiveStructure active) {
        this.active = active;
    }

    @Override
    public void tick() {
        World world = active.getChunk().getWorld();

        BoundingBox outerBox = active.getData().get(StoredStructureComponents.OUTER_BOX);
        ShapeScheduler.builder()
            .shape(CreateRectangle.builder()
                .boundingBox(outerBox)
                .spacing(1)
                .build())
            .locationTick(ctx ->{
                CruxPosition pos = ctx.getLocation();
                Location loc = pos.toLocation(world);
                world.getPlayers().forEach(p -> p.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 0));
            })
            .buildCached().schedule(0);
    }
}
