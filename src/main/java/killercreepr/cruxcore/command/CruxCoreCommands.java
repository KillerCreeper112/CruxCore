package killercreepr.cruxcore.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.plugin.module.CruxModule;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.command.argument.CruxCmdArguments;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxString;
import killercreepr.cruxcore.CruxCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
            commands.register(cmd, List.of("ccore", "cc"));
            commands.register(
                Commands.literal("flightspeed")
                    .requires(source -> source.getSender().hasPermission("cruxcore.cmds.flightspeed.use"))
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
                .then(
                    Commands.argument("plugin", CruxCmdArguments.CRUX_PLUGIN)
                        .executes(ctx ->{
                            CruxPlugin cruxPlugin = ctx.getArgument("plugin", CruxPlugin.class);
                            getExecutor(ctx.getSource()).sendMessage("Reloading CruxPlugin: " + cruxPlugin.getName());
                            cruxPlugin.reload(plugin);
                            return 1;
                        })
                )
        ).then(
            Commands.literal("module")
                .then(
                    Commands.literal("reload")
                        .then(
                            Commands.argument("module", CruxCmdArguments.CRUX_MODULE)
                                .executes(ctx ->{
                                    CruxModule module = ctx.getArgument("module", CruxModule.class);
                                    getExecutor(ctx.getSource()).sendMessage("Reloading CruxModule: " + module.name());
                                    module.reload(plugin);
                                    return 1;
                                })
                        )
                )
        ).then(
            Commands.literal("latinfont")
                .then(
                    Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx ->{
                            String text = ctx.getArgument("text", String.class);
                            getExecutor(ctx.getSource()).sendMessage(
                                Component.text(CruxString.latinFont(text))
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, CruxString.latinFont(text)))
                                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                            );
                            return 1;
                        })
                )
        ).then(
            Commands.literal("evaluate")
                .then(
                    Commands.argument("equation", StringArgumentType.greedyString())
                        .executes(ctx ->{
                            String equation = ctx.getArgument("equation", String.class);
                            double output = CruxMath.evaluate(Crux.format().deserializeString(equation));
                            getExecutor(ctx.getSource()).sendMessage(Component.text("Output: " + output)
                                .clickEvent(ClickEvent.copyToClipboard(output + ""))
                                .hoverEvent(HoverEvent.showText(Component.text("Click to copy"))));
                            return 1;
                        })
                )
        ).then(
            Commands.literal("format")
                .then(
                    Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx ->{
                            String text = ctx.getArgument("text", String.class);
                            Component output = Crux.format().deserialize(text, TagContainer.merged()
                                .hook(getExecutor(ctx.getSource())));
                            getExecutor(ctx.getSource()).sendMessage(Component.text("Output: ").append(output)
                                .clickEvent(ClickEvent.copyToClipboard(PlainTextComponentSerializer.plainText().serialize(output)))
                                .hoverEvent(HoverEvent.showText(Component.text("Click to copy"))));
                            return 1;
                        })
                )
        ).then(
            Commands.literal("actionbar")
                .then(
                    Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx ->{
                            String text = ctx.getArgument("text", String.class);
                            Component output = Crux.format().deserialize(text, TagContainer.merged()
                                .hook(getExecutor(ctx.getSource())));
                            getExecutor(ctx.getSource()).sendActionBar(output);
                            return 1;
                        })
                )
        ).then(
            Commands.literal("msg")
                .then(
                    Commands.argument("targets", ArgumentTypes.players())
                        .then(
                            Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx ->{
                                    Collection<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                        .resolve(ctx.getSource());
                                    String text = ctx.getArgument("text", String.class);

                                    for(Player p : targets){
                                        Component output = Crux.format().deserialize(text, TagContainer.merged()
                                            .hook(getExecutor(ctx.getSource()))
                                            .hook(p));
                                        p.sendMessage(output);
                                    }
                                    Component output = Crux.format().deserialize(text, TagContainer.merged()
                                        .hook(getExecutor(ctx.getSource())));

                                    getExecutor(ctx.getSource()).sendMessage(
                                        Component.text("Sent message to " + targets.size() + " players: ")
                                            .append(output)
                                    );

                                    return 1;
                                })
                        )
                )
        ).then(
            Commands.literal("exp")
                .then(
                    Commands.literal("points")
                            .then(
                                Commands.argument("target", ArgumentTypes.player())
                                    .then(
                                        Commands.literal("set")
                                            .then(
                                                Commands.argument("points", StringArgumentType.greedyString())
                                                    .executes(ctx ->{
                                                        CommandSender sender = getExecutor(ctx.getSource());
                                                        Player p = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource()).getFirst();
                                                        double amount = CruxMath.evaluate(Crux.format().deserializeString(ctx.getArgument("points", String.class)));
                                                        int x = (int) amount;
                                                        p.setExperienceLevelAndProgress(x);
                                                        sender.sendMessage("Set experience and level based on points " + p.getName() + ": " + x);
                                                        return 1;
                                                    })
                                            )
                                    ).then(
                                        Commands.literal("get")
                                            .executes(ctx ->{
                                                CommandSender sender = getExecutor(ctx.getSource());
                                                Player p = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource()).getFirst();
                                                int total = p.calculateTotalExperiencePoints();
                                                sender.sendMessage(p.getName() + " has " + total + " total experience points.");
                                                return 1;
                                            })
                                    )
                            )
                )
        )
        ;
        return dispatcher.build();
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source) {
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

}
