package killercreepr.cruxcore.command;

import com.fastasyncworldedit.core.extent.clipboard.ReadOnlyClipboard;
import com.fastasyncworldedit.core.extent.clipboard.WorldCopyClipboard;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.cruxcore.CruxCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FAWECommands {
    protected final @NotNull CruxCore plugin;
    protected final WorldEdit worldEdit;

    public FAWECommands(@NotNull CruxCore plugin) {
        this.plugin = plugin;
        this.worldEdit = WorldEdit.getInstance();
    }

    public void register(@NotNull CruxPlugin plugin){
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->{
            final Commands commands = event.registrar();
            LiteralCommandNode<CommandSourceStack> cmd = build(Commands.literal("cfawe")
                .requires(source -> source.getSender().hasPermission("cruxcore.cmds.cfawe.use")),
                plugin.getLifecycleManager());
            commands.register(cmd);
        });
    }

    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                                  LifecycleEventManager<?> manager){
        //cruxclaim view (player)
        dispatcher.then(
            Commands.literal("paste")
                .executes(ctx ->{
                    CommandSender sender = getExecutor(ctx.getSource());
                    if(!(sender instanceof Player p)) return 0;
                    Actor actor = BukkitAdapter.adapt(p);
                    LocalSession session = WorldEdit.getInstance().getSessionManager().getIfPresent(actor);
                    if(session == null){
                        sender.sendMessage("Session not present.");
                        return 0;
                    }
                    ClipboardHolder holder = session.getClipboard();
                    Clipboard clipboard = holder.getClipboards().getFirst();
                    World world = BukkitAdapter.adapt(p.getWorld());
                    BlockVector3 placement = BukkitAdapter.asBlockVector(p.getLocation());
                    clipboard.paste(world, placement);

                    BlockVector3 pos1 = placement.add(
                        clipboard.getWidth()/2, 0, clipboard.getLength()/2
                    );
                    BlockVector3 pos2 = placement.add(
                        (clipboard.getWidth()/2) * -1, clipboard.getHeight(), (clipboard.getLength()/2) * -1
                    );
                    session.getRegionSelector(world).selectPrimary(pos1, ActorSelectorLimits.forActor(actor));
                    session.getRegionSelector(world).selectSecondary(pos2, ActorSelectorLimits.forActor(actor));
                    sender.sendMessage("Done");
                    return 1;
                })
        ).then(
            Commands.literal("save")
                .then(
                    Commands.argument("name", StringArgumentType.string())
                        .executes(ctx ->{
                            CommandSender sender = getExecutor(ctx.getSource());
                            if(!(sender instanceof Player p)) return 0;
                            String name = ctx.getArgument("name", String.class);
                            Actor actor = BukkitAdapter.adapt(p);
                            LocalSession session = WorldEdit.getInstance().getSessionManager().getIfPresent(actor);
                            if(session == null){
                                sender.sendMessage("Session not present.");
                                return 0;
                            }
                            World world = BukkitAdapter.adapt(p.getWorld());
                            try(Clipboard clipboard = ReadOnlyClipboard.of(
                                WorldEdit.getInstance().newEditSession(world),
                                new CuboidRegion(session.getRegionSelector(world).getVertices().get(0),session.getRegionSelector(world).getVertices().get(1))
                            )){
                                Region region = clipboard.getRegion();
                                if(!(region instanceof CuboidRegion cuboid)){
                                    sender.sendMessage("Region not a cuboid.");
                                    return 0;
                                }
                                BlockVector3 origin = getCenterPoint(cuboid.getPos1(), cuboid.getPos2());
                                int y = Math.min(cuboid.getPos1().y(), cuboid.getPos2().y());
                                origin = origin.withY(y);
                                WorldCopyClipboard faweClipboard = new WorldCopyClipboard(() -> clipboard, region);
                                faweClipboard.setOrigin(origin);

                                File f = WorldEdit.getInstance().getSchematicsFolderPath().resolve( name + ".schem").toFile();
                                try(ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(f))) {
                                    writer.writeRotation(BukkitAdapter.adapt(p.getEyeLocation()));
                                    writer.write(faweClipboard);
                                    sender.sendMessage("Saved schematic to " + f.getAbsolutePath());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            return 1;
                        })
                )
        )
        ;
        return dispatcher.build();
    }

    public static BlockVector3 getCenterPoint(BlockVector3 pos1, BlockVector3 pos2) {
        int centerX = (pos1.x() + pos2.x()) / 2;
        int centerY = (pos1.y() + pos2.y()) / 2;
        int centerZ = (pos1.z() + pos2.z()) / 2;

        return BlockVector3.at(centerX, centerY, centerZ);
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source) {
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

}
