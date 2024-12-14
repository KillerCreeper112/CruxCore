package killercreepr.cruxcore.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxcore.CruxCore;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    protected final Map<UUID, LastPasted> lastPasted = new HashMap<>();
    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                                  LifecycleEventManager<?> manager){
        //cruxclaim view (player)
        dispatcher.then(
            Commands.literal("paste")
                .then(
                    Commands.argument("schematic", StringArgumentType.string())
                        .executes(ctx ->{
                            CommandSender sender = getExecutor(ctx.getSource());
                            if(!(sender instanceof Player p)) return 0;
                            String id = ctx.getArgument("schematic", String.class);

                            p.performCommand("/schem load " + id);
                            Crux.scheduler().runTaskLater(() ->{
                                p.performCommand("cfawe paste");
                                LastPasted data = lastPasted.get(p.getUniqueId());
                                if(data == null){
                                    sender.sendMessage("Failed.");
                                    return;
                                }
                                data.name = id;
                            }, 15L);
                            return 0;
                        })
                        .suggests((ctx, builder) ->{
                            File[] files = WorldEdit.getInstance().getSchematicsFolderPath().toFile().listFiles();
                            if(files != null){
                                for(File f : files){
                                    builder.suggest(CruxFolder.withoutFileExtension(f.getName()));
                                }
                            }
                            return builder.buildFuture();
                        })
                )
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
                    if(!(clipboard.getRegion() instanceof CuboidRegion region)){
                        sender.sendMessage("Clipboard must be a cuboid region.");
                        return 0;
                    }
                    World world = BukkitAdapter.adapt(p.getWorld());
                    BlockVector3 to = BukkitAdapter.asBlockVector(p.getLocation());
                    BlockVector3 origin = clipboard.getOrigin();
                    EditSession editSession = clipboard.paste(world, to);

                    int relX = to.x() - origin.x();
                    int relY = to.y() - origin.y();
                    int relZ = to.z() - origin.z();

                    /*BlockVector3 center = clipboard.getRegion().getCenter().toBlockPoint();
                    BlockVector3 difference = placement.subtract(clipboard.getOrigin());
                    BlockVector3 result = center.add(difference);

                    boolean even = clipboard.getHeight() % 2 == 0;
                    int heightMin = even ? Math.min((clipboard.getHeight()/2) - 1, 0) : clipboard.getHeight()/2;

                    BlockVector3 pos1 = result.add(
                        clipboard.getWidth()/2, clipboard.getHeight()/2, clipboard.getLength()/2
                    );
                    BlockVector3 pos2 = result.add(
                        (clipboard.getWidth()/2) * -1, (heightMin) * -1, (clipboard.getLength()/2) * -1
                    );*/
                    BlockVector3 pos1 = region.getPos1().add(relX, relY, relZ);
                    BlockVector3 pos2 = region.getPos2().add(relX, relY, relZ);

                    session.getRegionSelector(world).selectPrimary(pos1, ActorSelectorLimits.forActor(actor));
                    session.getRegionSelector(world).selectSecondary(pos2, ActorSelectorLimits.forActor(actor));
                    sender.sendMessage("Done");

                    LastPasted data = new LastPasted(origin, to);
                    lastPasted.put(p.getUniqueId(), data);
                    return 1;
                })
        ).then(
            Commands.literal("save")
                .executes(ctx ->{
                    CommandSender sender = getExecutor(ctx.getSource());
                    if(!(sender instanceof Player p)) return 0;
                    return save(p, null, false);
                })
                .then(
                    Commands.argument("name", StringArgumentType.string())
                        .suggests((ctx, builder) ->{
                            if(ctx.getSource().getSender() instanceof Player p){
                                LastPasted data = lastPasted.get(p.getUniqueId());
                                if(data != null && data.name != null) builder.suggest(data.name);
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx ->{
                            CommandSender sender = getExecutor(ctx.getSource());
                            if(!(sender instanceof Player p)) return 0;
                            String name = ctx.getArgument("name", String.class);
                            return save(p, name, false);
                        }).then(
                            Commands.argument("override_origin", BoolArgumentType.bool())
                                .executes(ctx ->{
                                    CommandSender sender = getExecutor(ctx.getSource());
                                    if(!(sender instanceof Player p)) return 0;
                                    String name = ctx.getArgument("name", String.class);
                                    return save(p, name, ctx.getArgument("override_origin", Boolean.class));
                                })
                        )
                )
        ).then(
            Commands.literal("tporigin")
                .executes(ctx ->{
                    CommandSender sender = getExecutor(ctx.getSource());
                    if(!(sender instanceof Player p)) return 0;
                    LastPasted data = lastPasted.get(p.getUniqueId());
                    if(data == null){
                        sender.sendMessage("No pasted data.");
                        return 0;
                    }
                    BlockVector3 relative = data.to.subtract(data.origin);
                    BlockVector3 origin = data.origin.add(relative);
                    Location l = new Location(p.getWorld(), origin.x(), origin.y(), origin.z()).toCenterLocation();
                    p.teleport(l);
                    sender.sendMessage("Teleported to origin.");
                    return 1;
                })
        )
        ;
        return dispatcher.build();
    }

    public int save(Player p, @Nullable String name, boolean overrideOrigin){
        CommandSender sender = p;
        Actor actor = BukkitAdapter.adapt(p);
        LocalSession session = WorldEdit.getInstance().getSessionManager().getIfPresent(actor);
        if(session == null){
            sender.sendMessage("Session not present.");
            return 0;
        }
        LastPasted data = lastPasted.get(p.getUniqueId());
        if(data == null){
            sender.sendMessage("No last pasted data.");
            return 0;
        }
        if(name == null) name = data.name;

        BlockVector3 relative = data.to.subtract(data.origin);
        BlockVector3 origin = overrideOrigin ? BukkitAdapter.asBlockVector(p.getLocation()) : data.origin.add(relative);

        World world = BukkitAdapter.adapt(p.getWorld());
        CuboidRegion region = new CuboidRegion(session.getRegionSelector(world).getVertices().get(0),
            session.getRegionSelector(world).getVertices().get(1));
        try(Clipboard clipboard = new BlockArrayClipboard(region)){
            EditSession editSession = WorldEdit.getInstance().newEditSession(world);
            ForwardExtentCopy copy = new ForwardExtentCopy(
                editSession, region, clipboard, region.getMinimumPoint()
            );
            clipboard.setOrigin(origin);
            Operations.complete(copy);

            File f = WorldEdit.getInstance().getSchematicsFolderPath().resolve( name + ".schem").toFile();
            try(ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(f))) {
                writer.writeRotation(BukkitAdapter.adapt(p.getEyeLocation()));
                writer.write(clipboard);
                sender.sendMessage("Saved schematic to " + f.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return 1;
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

    public static class LastPasted{
        public String name;
        public final BlockVector3 origin, to;

        public LastPasted(BlockVector3 origin, BlockVector3 to) {
            this.origin = origin;
            this.to = to;
        }
    }
}
