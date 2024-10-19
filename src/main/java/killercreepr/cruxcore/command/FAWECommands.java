package killercreepr.cruxcore.command;

import com.fastasyncworldedit.core.FaweAPI;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.world.World;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.Crux;
import killercreepr.crux.command.argument.CruxCmdArguments;
import killercreepr.crux.module.CruxModule;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.tags.container.TagContainer;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxString;
import killercreepr.cruxcore.CruxCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.Clip;
import java.util.List;
import java.util.Objects;

public class FAWECommands {
    protected final @NotNull CruxCore plugin;

    public FAWECommands(@NotNull CruxCore plugin) {
        this.plugin = plugin;
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
                        clipboard.getWidth(), 0, clipboard.getLength()
                    );
                    BlockVector3 pos2 = placement.add(
                        clipboard.getWidth() * -1, clipboard.getHeight(), clipboard.getLength() * -1
                    );
                    Region region = new CuboidRegion(world, pos1, pos2);
                    try(EditSession editSession = WorldEdit.getInstance().newEditSession(world)){
                        Clipboard newClipboard = editSession.lazyCopy(region);
                        session.setClipboard(new ClipboardHolder(newClipboard));
                    }
                    sender.sendMessage("Done");
                    return 1;
                })
        )
        ;
        return dispatcher.build();
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source) {
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

}
