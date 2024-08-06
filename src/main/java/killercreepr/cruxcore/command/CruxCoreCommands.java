package killercreepr.cruxcore.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxcore.CruxCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CruxCoreCommands {
    protected final @NotNull CruxCore plugin;

    public CruxCoreCommands(@NotNull CruxCore plugin) {
        this.plugin = plugin;
    }

    public void register(@NotNull CruxPlugin plugin){
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->{
            final Commands commands = event.registrar();
            LiteralCommandNode<CommandSourceStack> cmd = build(Commands.literal("cruxcore")
                .requires(source -> source.getSender().hasPermission("cruxcore.cmds.cruxcore.use")),
                plugin.getLifecycleManager());
            commands.register(cmd, List.of("ccore"));
            commands.register(
                Commands.literal("flightspeed")
                    .then(
                        Commands.argument("value", DoubleArgumentType.doubleArg())
                            .executes(ctx ->{
                                if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                                double value = ctx.getArgument("value", Double.class);
                                p.setFlySpeed((float) CruxMath.clamp(value, -1D, 1D));
                                p.sendMessage("Set fly speed to " + value);
                                return 1;
                            })
                    )
                    .build(), List.of("fs")
            );
        });
    }

    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                                  LifecycleEventManager<?> manager){
        //cruxclaim view (player)
        dispatcher.then(
            Commands.literal("reload")
                .executes(ctx ->{
                    CommandSender sender = getExecutor(ctx.getSource());
                    sender.sendMessage("Reloading Crux and Crux Modules.");
                    plugin.reload();
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
