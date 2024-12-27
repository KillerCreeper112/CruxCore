package killercreepr.cruxcore.structure.component;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateRectangle;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ActiveStructureParticleBox implements ManagedTicked {
    protected final ActiveStructure active;
    public ActiveStructureParticleBox(ActiveStructure active) {
        this.active = active;
    }

    @Override
    public void tick() {
        World world = active.getChunk().getWorld();
        ShapeScheduler.builder()
            .shape(CreateRectangle.builder()
                .boundingBox(active.getData().getBoundingBox())
                .spacing(1)
                .build())
            .locationTick(ctx ->{
                CruxPosition pos = ctx.getLocation();
                Location loc = pos.toLocation(world);
                world.getPlayers().forEach(p -> p.spawnParticle(Particle.FLAME, loc, 0));
            })
            .buildCached().schedule(0);
    }
}
