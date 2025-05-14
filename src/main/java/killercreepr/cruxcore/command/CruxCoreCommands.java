package killercreepr.cruxcore.command;

import com.destroystokyo.paper.entity.ai.Goal;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.api.plugin.module.CruxModule;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.command.argument.CruxCmdArguments;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxString;
import killercreepr.cruxcore.CruxCore;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
                .then(
                    Commands.literal("config.yml")
                        .executes(ctx ->{
                            plugin.reloadCfg();
                            getExecutor(ctx.getSource()).sendMessage("Reloaded CruxCore config.");
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
            Commands.literal("mob")
                .then(
                    Commands.literal("goals")
                        .then(
                            Commands.argument("target", ArgumentTypes.entity())
                                .executes(ctx ->{
                                    var sender = getExecutor(ctx.getSource());
                                    for(Entity e : ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                        .resolve(ctx.getSource())){
                                        if(!(e instanceof Mob mob)) continue;

                                        int index = 0;
                                        for (Goal<Mob> goal : plugin.getServer().getMobGoals().getAllGoals(mob)) {
                                            index++;
                                            sender.sendMessage("#" + index + " - " + goal.getKey().getNamespacedKey());
                                        }
                                    }
                                    return 1;
                                })
                        )
                )
        ).then(
            Commands.literal("uuid")
                .then(
                    Commands.argument("name", StringArgumentType.greedyString())
                        .executes(ctx ->{
                            var sender = getExecutor(ctx.getSource());
                            var name = ctx.getArgument("name", String.class);
                            UUID uuid = plugin.getServer().getOfflinePlayer(name).getUniqueId();
                            sender.sendMessage(Component.text("UUID for " + name + " is: " + uuid)
                                .clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                            return 1;
                        })
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

                            Component base = Component.empty();
                            String stringDeserialized = Crux.format().deserializeString(text, TagContainer.merged()
                                .hook(getExecutor(ctx.getSource())));
                            if(!stringDeserialized.contains("<click")){
                                base = base.clickEvent(ClickEvent.copyToClipboard(PlainTextComponentSerializer.plainText().serialize(output)));
                            }
                            if(!stringDeserialized.contains("<hover")){
                                base = base.hoverEvent(HoverEvent.showText(Component.text("Click to copy")));
                            }

                            getExecutor(ctx.getSource()).sendMessage(Component.text("Output: ").append(
                                base.append(output)
                            ));
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
            Commands.literal("title")
                .then(
                    Commands.argument("fade_in", IntegerArgumentType.integer())
                        .then(
                            Commands.argument("stay", IntegerArgumentType.integer())
                                .then(
                                    Commands.argument("fade_out", IntegerArgumentType.integer())
                                        .then(
                                            Commands.argument("text", StringArgumentType.string())
                                                .executes(ctx ->{
                                                    int fadeIn = ctx.getArgument("fade_in", Integer.class);
                                                    int stay = ctx.getArgument("stay", Integer.class);
                                                    int fadeOut = ctx.getArgument("fade_out", Integer.class);
                                                    String text = ctx.getArgument("text", String.class);
                                                    CreateTitle title = CreateTitle.title(
                                                        text, null, fadeIn, stay, fadeOut
                                                    );
                                                    title.use(getExecutor(ctx.getSource()), TagContainer.merged()
                                                        .hook(getExecutor(ctx.getSource())));
                                                    return 1;
                                                }).then(
                                                    Commands.argument("sub_text", StringArgumentType.greedyString())
                                                        .executes(ctx ->{
                                                            int fadeIn = ctx.getArgument("fade_in", Integer.class);
                                                            int stay = ctx.getArgument("stay", Integer.class);
                                                            int fadeOut = ctx.getArgument("fade_out", Integer.class);
                                                            String text = ctx.getArgument("text", String.class);
                                                            String subText = ctx.getArgument("text", String.class);
                                                            CreateTitle title = CreateTitle.title(
                                                                text, subText, fadeIn, stay, fadeOut
                                                            );
                                                            title.use(getExecutor(ctx.getSource()), TagContainer.merged()
                                                                .hook(getExecutor(ctx.getSource())));
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
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
            Commands.literal("playsound")
                .then(
                    Commands.argument("targets", ArgumentTypes.players())
                        .then(
                            Commands.argument("sound", ArgumentTypes.key())
                                .suggests((ctx, builder) ->{
                                    for (Sound sound : Registry.SOUNDS) {
                                        if(!(sound instanceof Keyed k)) continue;
                                        builder.suggest(k.key().asString());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx ->{
                                    Collection<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                        .resolve(ctx.getSource());
                                    Key sound = ctx.getArgument("sound", Key.class);

                                    for(Player p : targets){
                                        CreateSound.sound(sound).playFor(p);
                                    }

                                    getExecutor(ctx.getSource()).sendMessage(
                                        Component.text("Played sound " + sound + " to " + targets.size() + " players.")
                                    );

                                    return 1;
                                })
                                .then(
                                    Commands.argument("volume", FloatArgumentType.floatArg())
                                        .executes(ctx ->{
                                            Collection<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            Key sound = ctx.getArgument("sound", Key.class);
                                            float volume = ctx.getArgument("volume", Float.class);

                                            for(Player p : targets){
                                                CreateSound.sound(sound, net.kyori.adventure.sound.Sound.Source.MASTER, volume, 1f).playFor(p);
                                            }

                                            getExecutor(ctx.getSource()).sendMessage(
                                                Component.text("Played sound " + sound + " to " + targets.size() + " players.")
                                            );

                                            return 1;
                                        }).then(
                                            Commands.argument("pitch", FloatArgumentType.floatArg())
                                                .executes(ctx ->{
                                                    Collection<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                        .resolve(ctx.getSource());
                                                    Key sound = ctx.getArgument("sound", Key.class);
                                                    float volume = ctx.getArgument("volume", Float.class);
                                                    float pitch = ctx.getArgument("pitch", Float.class);

                                                    for(Player p : targets){
                                                        CreateSound.sound(sound, net.kyori.adventure.sound.Sound.Source.MASTER, volume, pitch).playFor(p);
                                                    }

                                                    getExecutor(ctx.getSource()).sendMessage(
                                                        Component.text("Played sound " + sound + " to " + targets.size() + " players.")
                                                    );

                                                    return 1;
                                                })
                                        )
                                )
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

    public static @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source) {
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

}
