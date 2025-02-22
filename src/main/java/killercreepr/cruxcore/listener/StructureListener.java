package killercreepr.cruxcore.listener;

import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.api.event.CruxBlockPlaceEvent;
import killercreepr.cruxblocks.api.mining.user.Miner;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcore.api.event.PlayerEnterStructureEvent;
import killercreepr.cruxcore.api.event.PlayerLeaveStructureEvent;
import killercreepr.cruxcore.component.CruxCoreComponents;
import killercreepr.cruxcore.entity.memory.StructureWalkerHolder;
import killercreepr.cruxcore.structure.component.StructureDenyMobSpawns;
import killercreepr.cruxstructures.api.component.BlockManipulatorComponent;
import killercreepr.cruxstructures.api.component.StoredBlocks;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.structure.Structure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.registries.StructureRegistries;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxstructures.core.structure.component.StructureComponents;
import killercreepr.cruxstructures.core.structure.component.StructureOuterBoxComponent;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class StructureListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity e = event.getEntity();
        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().key());
        if(crux == null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module == null) return;
        Vector pos = e.getLocation().toVector();
        StoredStructure stored = CruxCollection.getFirst(module.getStored(check ->{
            StructureOuterBoxComponent outerBox = (StructureOuterBoxComponent) check.getParent().get(StructureComponents.OUTER_BOX);
            if(outerBox == null || !outerBox.disableBlockPlace()) return false;
            BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
            return box.contains(pos);
        }));
        if(stored == null) return;
        Structure structure = StructureRegistries.STRUCTURES.get(stored.getStructureKey());
        if(structure == null) return;
        StructureDenyMobSpawns deny = structure.get(CruxCoreComponents.STRUCTURE_DENY_MOB_SPAWNS);
        if(deny == null) return;
        if(!deny.test(e)) return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().hasPermission("cruxcore.structure.block.place.bypass")) return;
        Block b = event.getBlock();
        blockPlace(b, event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCruxBlockPlace(CruxBlockPlaceEvent event) {
        Miner miner = event.getContext().getMiner();
        if(!(miner instanceof EntityMiner entityMiner)) return;
        Entity e = entityMiner.getEntity();
        if(e.hasPermission("cruxcore.structure.block.place.bypass")) return;
        Block b = event.getContext().getBlock();
        blockPlace(b, event);
    }

    public void blockPlace(Block b, Cancellable event){
        CruxWorld crux = CruxCore.core().worldManager().getWorld(b.getWorld().key());
        if(crux == null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module == null) return;
        Vector pos = b.getLocation().toVector();
        StoredStructure stored = CruxCollection.getFirst(module.getStored(check ->{
            StructureOuterBoxComponent outerBox = (StructureOuterBoxComponent) check.getParent().get(StructureComponents.OUTER_BOX);
            if(outerBox == null || !outerBox.disableBlockPlace()) return false;
            BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
            return box.contains(pos);
        }));
        if (stored == null) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCruxBlockBreak(CruxBlockBreakEvent event) {
        Miner miner = event.getContext().getMiner();
        if(!(miner instanceof EntityMiner entityMiner)) return;
        Entity e = entityMiner.getEntity();
        if(e.hasPermission("cruxcore.structure.block.break.bypass")) return;
        Block b = event.getContext().getBlock();
        blockBreak(b, event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().hasPermission("cruxcore.structure.block.break.bypass")) return;
        Block b = event.getBlock();
        blockBreak(b, event);
    }

    public void blockBreak(Block b, Cancellable event){
        if(b.getType() == Material.DECORATED_POT) return;
        CruxWorld crux = CruxCore.core().worldManager().getWorld(b.getWorld().key());
        if(crux == null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module == null) return;
        Vector pos = b.getLocation().toVector();
        StoredStructure stored = CruxCollection.getFirst(module.getStored(check ->{
            StructureOuterBoxComponent outerBox = (StructureOuterBoxComponent) check.getParent().get(StructureComponents.OUTER_BOX);
            BoundingBox box;
            //only check outer box if it has disable block break, otherwise if the structure has
            //store blocks, it's going to be looping through all of those blocks for no reason at times
            if(outerBox != null && outerBox.disableBlockBreak()){
                box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
            }else box = check.getBoundingBox();
            return box.contains(pos);
        }));
        if (stored == null) return;
        StructureOuterBoxComponent outerBox = (StructureOuterBoxComponent) stored.getParent().get(StructureComponents.OUTER_BOX);
        if(outerBox != null && outerBox.disableBlockBreak()){
            event.setCancelled(true);
            return;
        }
        if(stored.get(StoredStructureComponents.STORE_BLOCKS) instanceof BlockManipulatorComponent blocks && blocks.disableBlockBreak()){
            StoredBlocks storedBlocks = (StoredBlocks) blocks;
            CruxPosition blockPos = CruxPosition.block(b);

            CruxPosition structurePos = stored.fromWorldToStructurePos(blockPos);

            for(CruxPosition check : storedBlocks.getStoredBlocks()){
                if(check.rotateAroundY(stored.getParent().originPos(), stored.getRotation()).equals(structurePos)){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEnterStructure(PlayerEnterStructureEvent event) {
        Structure structure = getStructure(event.getStructure());
        if(structure == null) return;
        Communicator msg = structure.get(CruxCoreComponents.STRUCTURE_ENTER_MESSAGE);
        if(msg == null) return;
        Player p = event.getPlayer();
        msg.use(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeaveStructure(PlayerLeaveStructureEvent event) {
        Structure structure = getStructure(event.getStructure());
        if(structure == null) return;
        Communicator msg = structure.get(CruxCoreComponents.STRUCTURE_LEAVE_MESSAGE);
        if(msg == null) return;
        Player p = event.getPlayer();
        msg.use(p);
    }


    public Structure getStructure(StoredStructure stored){
        if(stored == null) return null;
        return StructureRegistries.STRUCTURES.get(stored.getStructureKey());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.hasChangedBlock()) return;
        Player p = event.getPlayer();
        StructureWalkerHolder holder = EntityMemory.getOrCreateDataHolder(p, StructureWalkerHolder.class,
            mem -> new StructureWalkerHolder((PlayerMemory) mem));
        if(holder == null) return;
        if(CruxMath.hasOccurredWithin(holder.getLastTicked(), 40)) return;
        holder.setLastTicked(System.currentTimeMillis());
        CruxWorld world = CruxCore.core().worldManager().getWorld(event.getTo().getWorld().key());
        if(world == null) return;
        StructureWorldModule module = world.getModule(StructureWorldModule.class);
        if(module == null) return;
        Vector toVec = event.getTo().toVector();
        ActiveStructure to = CruxCollection.getFirst(module.getActive(active ->{
            StoredStructure check = active.getData();
            BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
            return box.contains(toVec);
        }));
        StoredStructure from = holder.getLastStructure();

        Structure strucTo = getStructure(to == null ? null : to.getData());
        Structure strucFrom = getStructure(from);
        if(strucTo != null && strucFrom != null && strucFrom.compare(strucTo)) return;

        if(to != null){
            PlayerEnterStructureEvent enter = new PlayerEnterStructureEvent(p, to.getData());
            enter.callEvent();
        }
        if(from != null){
            PlayerLeaveStructureEvent enter = new PlayerLeaveStructureEvent(p, from);
            enter.callEvent();
        }
        holder.setLastStructure(to == null ? null : to.getData());
    }

}
