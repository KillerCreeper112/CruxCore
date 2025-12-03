package killercreepr.cruxcore.command;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.endercentral.crazy_advancements.CrazyAdvancementsAPI;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.api.communication.boss.CreateBossBar;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.plugin.module.CruxModule;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.command.argument.CruxCmdArguments;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxString;
import killercreepr.crux.paper.ItemHolder;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcore.menu.PluginItemsMenu;
import killercreepr.cruxitems.core.command.argument.CruxItemsArguments;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

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
            commands.register(
                Commands.literal("fly")
                    .requires(source -> source.getSender().hasPermission("cruxcore.command.fly"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        p.setAllowFlight(!p.getAllowFlight());
                        p.sendMessage("Set allowed flight: " + p.getAllowFlight());
                        return 1;
                    })
                    .build()
            );
            commands.register(
                Commands.literal("pweather")
                    .requires(source -> source.getSender().hasPermission("cruxcore.command.pweather"))
                    .then(
                        Commands.literal("reset")
                            .executes(ctx ->{
                                var sender = getExecutor(ctx.getSource());
                                if(!(sender instanceof Player p)) return -1;
                                p.resetPlayerWeather();
                                sender.sendMessage("Reset player weather");
                                return 1;
                            })
                    )
                    .then(
                        Commands.argument("weather", StringArgumentType.string())
                            .suggests((ctx, builder) ->{
                                for (WeatherType value : WeatherType.values()) {
                                    builder.suggest(value.toString().toLowerCase());
                                }
                                return builder.buildFuture();
                            })
                            .executes(ctx ->{
                                var sender = getExecutor(ctx.getSource());
                                if(!(sender instanceof Player p)) return -1;
                                WeatherType weatherType;
                                try{
                                    weatherType = WeatherType.valueOf(ctx.getArgument("weather", String.class).toUpperCase());
                                } catch (IllegalArgumentException e) {
                                    sender.sendMessage("Invalid weather type.");
                                    return 0;
                                }

                                p.setPlayerWeather(weatherType);
                                sender.sendMessage("Set weather to: " + weatherType.toString().toLowerCase());
                                return 1;
                            })
                    )
                    .build()
            );

            commands.register(
                Commands.literal("ptime")
                    .requires(source -> source.getSender().hasPermission("cruxcore.command.ptime"))
                    .then(
                        Commands.literal("reset")
                            .executes(ctx ->{
                                var sender = getExecutor(ctx.getSource());
                                if(!(sender instanceof Player p)) return -1;
                                p.resetPlayerTime();
                                sender.sendMessage("Reset player time");
                                return 1;
                            })
                    )
                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("time", LongArgumentType.longArg())
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        if(!(sender instanceof Player p)) return -1;
                                        long time = ctx.getArgument("time", long.class);
                                        p.setPlayerTime(time, false);
                                        sender.sendMessage("Set time to " + time + " relative=false");
                                        return 1;
                                    })
                                    .then(
                                        Commands.argument("relative", BoolArgumentType.bool())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                if(!(sender instanceof Player p)) return -1;
                                                long time = ctx.getArgument("time", long.class);
                                                boolean relative = ctx.getArgument("relative", boolean.class);
                                                p.setPlayerTime(time, relative);
                                                sender.sendMessage("Set time to " + time + " relative=" + relative);
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build()
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
            Commands.literal("lang")
                .then(
                    Commands.literal("send")
                        .then(
                            Commands.argument("targets", ArgumentTypes.entities())
                                .then(
                                    Commands.argument("plugin", CruxCmdArguments.CRUX_PLUGIN)
                                        .then(
                                            Commands.argument("translation", StringArgumentType.string())
                                                .suggests((ctx, builder) ->{
                                                    var plugin = ctx.getLastChild().getArgument("plugin", CruxPlugin.class);
                                                    if(plugin instanceof LangProvider langProvider){
                                                        for (String id : langProvider.lang().getTranslations()) {
                                                            builder.suggest(id);
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx ->{
                                                    CruxPlugin cruxPlugin = ctx.getArgument("plugin", CruxPlugin.class);
                                                    String id = ctx.getArgument("translation", String.class);
                                                    var targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                        .resolve(ctx.getSource());
                                                    var sender = getExecutor(ctx.getSource());
                                                    if(cruxPlugin instanceof LangProvider langProvider){
                                                        for(Entity p : targets){
                                                            var tags = TagContainer.merged().hook(sender).hook(p);
                                                            langProvider.lang().use(id, p, tags);
                                                        }

                                                        getExecutor(ctx.getSource()).sendMessage(
                                                            Component.text("Sent lang message to " + targets.size() + " players: " + cruxPlugin.name() + " - " + id)
                                                        );
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        ).then(
            Commands.literal("uuid")
                .then(
                    Commands.argument("name", StringArgumentType.string())
                        .suggests((ctx, builder) ->{
                            for(OfflinePlayer player : plugin.getServer().getOfflinePlayers()){
                                builder.suggest(player.getName());
                            }
                            for(Player p : plugin.getServer().getOnlinePlayers()){
                                builder.suggest(p.getName());
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx ->{
                            CommandSender sender = getExecutor(ctx.getSource());
                            String name = ctx.getArgument("name", String.class);
                            UUID uuid = plugin.getServer().getPlayerUniqueId(name);

                            if(uuid == null){
                                sender.sendMessage("No UUID for " + name);
                                return 0;
                            }
                            sender.sendMessage(
                                Component.empty()
                                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                                    .clickEvent(ClickEvent.copyToClipboard(uuid.toString()))
                                    .append(
                                        Component.text("UUID for " + name + " is: " + uuid)
                                    )
                            );
                            return 1;
                        })
                )
        ).then(
            Commands.literal("items")
                .executes(ctx ->{
                    if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                    var holder = MenuHolder.holder(
                        Crux.key("temp/plugin_items"),
                        "Items <module_plugin_items_page>/<module_plugin_items_max_page>",
                        NumberProvider.constant(54),
                        MenuItems.items(new TreeMap<>()),
                        DataExchange.empty(),
                        new ArrayList<>()
                    );
                    holder.setRegistry(plugin.cruxMenus().menuRegistry());

                    var menu = new PluginItemsMenu(
                        holder,
                        DataExchange.builder()
                            .put("viewer", p)
                            .build()
                    );
                    menu.load();
                    menu.open(p);
                    return 1;
                })
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
                                        sender.sendMessage("Showing mob goals for " + mob.getName() + ":");
                                        for (Goal<Mob> goal : plugin.getServer().getMobGoals().getAllGoals(mob)) {
                                            index++;
                                            sender.sendMessage(Component.empty()
                                                    .clickEvent(ClickEvent.copyToClipboard(goal.getKey().getNamespacedKey().asString()))
                                                .append(
                                                    Component.text(
                                                        "#" + index + " - " + goal.getKey().getNamespacedKey()
                                                    )
                                                ));
                                        }
                                    }
                                    return 1;
                                })
                        )
                )
        ).then(
            Commands.literal("player")
                .then(
                    Commands.literal("profile")
                        .then(
                            Commands.argument("target", ArgumentTypes.entities())
                                .then(
                                    Commands.literal("reset")
                                        .executes(ctx ->{
                                            var sender = getExecutor(ctx.getSource());
                                            var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            for(Entity e : list){
                                                if(!(e instanceof Player p)) continue;
                                                PlayerProfile oldProfile = plugin.getServer().createProfileExact(p.getUniqueId(), p.getName());
                                                p.setPlayerProfile(oldProfile);
                                            }
                                            sender.sendMessage("Reset player skin for " + list.size() + " entities");
                                            return 1;
                                        })
                                ).then(
                                    Commands.literal("skin")
                                        .then(
                                            Commands.argument("value", StringArgumentType.greedyString())
                                                .executes(ctx ->{
                                                    var sender = getExecutor(ctx.getSource());
                                                    var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                        .resolve(ctx.getSource());
                                                    var value = ctx.getArgument("value", String.class);
                                                    String skinId = value.contains("minesk") ?
                                                        value.substring(value.lastIndexOf("/") + 1) :
                                                        value;

                                                    PlayerProfile profile = fetchSkin(skinId, UUID.randomUUID(), "a");
                                                    for(Entity e : list){
                                                        if(!(e instanceof Player p)) continue;
                                                        PlayerProfile set = plugin.getServer().createProfile(p.getUniqueId(), p.getName());
                                                        set.setTextures(profile.getTextures());
                                                        p.setPlayerProfile(set);
                                                    }
                                                    sender.sendMessage("Set player skin to " + value + " for " + list.size() + " entities");
                                                    return 1;
                                                })
                                        )
                                ).then(
                                    Commands.literal("playerskin")
                                        .then(
                                            Commands.argument("value", StringArgumentType.greedyString())
                                                .executes(ctx ->{
                                                    var sender = getExecutor(ctx.getSource());
                                                    var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                        .resolve(ctx.getSource());
                                                    var value = ctx.getArgument("value", String.class);
                                                    UUID uuid;
                                                    String name;
                                                    try{
                                                        uuid = UUID.fromString(value);
                                                        name = plugin.getServer().getOfflinePlayer(uuid).getName();
                                                    } catch (IllegalArgumentException e) {
                                                        uuid = plugin.getServer().getOfflinePlayer(value).getUniqueId();
                                                        name = value;
                                                    }

                                                    PlayerProfile profile = plugin.getServer().createProfileExact(uuid, name);
                                                    profile.complete();
                                                    for(Entity e : list){
                                                        if(!(e instanceof Player p)) continue;
                                                        PlayerProfile set = plugin.getServer().createProfile(p.getUniqueId(), p.getName());
                                                        set.setTextures(profile.getTextures());
                                                        p.setPlayerProfile(set);
                                                    }
                                                    sender.sendMessage("Set player skin from player to " + value + " for " + list.size() + " entities");
                                                    return 1;
                                                })
                                        )
                                ).then(
                                    Commands.literal("name")
                                        .then(
                                            Commands.argument("value", StringArgumentType.greedyString())
                                                .executes(ctx ->{
                                                    var sender = getExecutor(ctx.getSource());
                                                    var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                        .resolve(ctx.getSource());
                                                    var value = ctx.getArgument("value", String.class);

                                                    for(Entity e : list){
                                                        if(!(e instanceof Player p)) continue;
                                                        PlayerProfile current = p.getPlayerProfile();
                                                        PlayerProfile set = plugin.getServer().createProfileExact(current.getId(), value);
                                                        p.setPlayerProfile(set);
                                                    }
                                                    sender.sendMessage("Set player name to " + value + " for " + list.size() + " entities");
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        ).then(
            Commands.literal("entity")
                .then(
                    Commands.literal("hp")
                        .then(
                            Commands.argument("target", ArgumentTypes.entities())
                                .then(
                                    Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(ctx ->{
                                            var sender = getExecutor(ctx.getSource());
                                            var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            var value = ctx.getArgument("value", String.class);
                                            for(Entity e : list){
                                                if(!(e instanceof LivingEntity mob)) continue;
                                                double hp = CruxMath.evaluate(Crux.format().deserializeString(value, TagContainer.string().hook(e)));
                                                mob.setHealth(CruxMath.clamp(
                                                    hp, 0D, mob.getAttribute(Attribute.MAX_HEALTH).getValue()
                                                ));
                                            }
                                            sender.sendMessage("Set health to " + value + " for " + list.size() + " entities");
                                            return 1;
                                        })
                                )
                        )
                ).then(
                    Commands.literal("ai")
                        .then(
                            Commands.argument("target", ArgumentTypes.entities())
                                .then(
                                    Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(ctx ->{
                                            var sender = getExecutor(ctx.getSource());
                                            var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            var value = ctx.getArgument("value", String.class);
                                            for(Entity e : list){
                                                if(!(e instanceof LivingEntity mob)) continue;
                                                String x = Crux.format().deserializeString(value, TagContainer.string().hook(e));
                                                mob.setAI(CruxString.parseBoolean(x));
                                            }
                                            sender.sendMessage("Set AI to " + value + " for " + list.size() + " entities");
                                            return 1;
                                        })
                                )
                        )
                ).then(
                    Commands.literal("aware")
                        .then(
                            Commands.argument("target", ArgumentTypes.entities())
                                .then(
                                    Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(ctx ->{
                                            var sender = getExecutor(ctx.getSource());
                                            var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            var value = ctx.getArgument("value", String.class);
                                            for(Entity e : list){
                                                if(!(e instanceof Mob mob)) continue;
                                                String x = Crux.format().deserializeString(value, TagContainer.string().hook(e));
                                                mob.setAware(CruxString.parseBoolean(x));
                                            }
                                            sender.sendMessage("Set aware to " + value + " for " + list.size() + " entities");
                                            return 1;
                                        })
                                )
                        )
                ).then(
                    Commands.literal("food")
                        .then(
                            Commands.argument("target", ArgumentTypes.entities())
                                .then(
                                    Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(ctx ->{
                                            var sender = getExecutor(ctx.getSource());
                                            var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            var value = ctx.getArgument("value", String.class);
                                            for(Entity e : list){
                                                if(!(e instanceof HumanEntity mob)) continue;
                                                double hp = CruxMath.evaluate(Crux.format().deserializeString(value, TagContainer.string().hook(e)));
                                                mob.setFoodLevel((int) hp);
                                            }
                                            sender.sendMessage("Set food level to " + value + " for " + list.size() + " entities");
                                            return 1;
                                        })
                                )
                        )
                ).then(
                    Commands.literal("saturation")
                        .then(
                            Commands.argument("target", ArgumentTypes.entities())
                                .then(
                                    Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(ctx ->{
                                            var sender = getExecutor(ctx.getSource());
                                            var list = ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            var value = ctx.getArgument("value", String.class);
                                            for(Entity e : list){
                                                if(!(e instanceof HumanEntity mob)) continue;
                                                double hp = CruxMath.evaluate(Crux.format().deserializeString(value, TagContainer.string().hook(e)));
                                                mob.setSaturation((float) hp);
                                            }
                                            sender.sendMessage("Set saturation to " + value + " for " + list.size() + " entities");
                                            return 1;
                                        })
                                )
                        )
                ).then(
                    Commands.literal("hitbox")
                        .then(
                            Commands.argument("target", ArgumentTypes.entity())
                                .executes(ctx ->{
                                    var sender = getExecutor(ctx.getSource());
                                    for(Entity e : ctx.getArgument("target", EntitySelectorArgumentResolver.class)
                                        .resolve(ctx.getSource())){
                                        if(!(e instanceof Mob mob)) continue;
                                        sender.sendMessage(mob.getName() + " width=" + mob.getWidth() + ", height=" + mob.getHeight());
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
            Commands.literal("bossbar")
                .then(
                    Commands.argument("key", StringArgumentType.word())
                        .then(
                            Commands.argument("color", StringArgumentType.word())
                                .suggests((ctx, builder) ->{
                                    for (String s : BossBar.Color.NAMES.keys()) {
                                        builder.suggest(s);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(
                                    Commands.argument("style", StringArgumentType.word())
                                        .suggests((ctx, builder) ->{
                                            for (String s : BossBar.Overlay.NAMES.keys()) {
                                                builder.suggest(s);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(
                                            Commands.argument("progress", StringArgumentType.word())
                                                .then(
                                                    Commands.argument("duration", StringArgumentType.word())
                                                        .then(
                                                            Commands.argument("title", StringArgumentType.greedyString())
                                                                .executes(ctx ->{
                                                                    CreateBossBar.bossBar(
                                                                        ctx.getArgument("key", String.class),
                                                                        ctx.getArgument("title", String.class),
                                                                        ctx.getArgument("progress", String.class),
                                                                        ctx.getArgument("color", String.class),
                                                                        ctx.getArgument("style", String.class),
                                                                        ctx.getArgument("duration", String.class),
                                                                        null
                                                                    ).showBossBar(getExecutor(ctx.getSource()));
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
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
                    Commands.argument("targets", ArgumentTypes.entities())
                        .then(
                            Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx ->{
                                    Collection<Entity> targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                        .resolve(ctx.getSource());
                                    String text = ctx.getArgument("text", String.class);

                                    for(Entity p : targets){
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
                    Commands.argument("targets", ArgumentTypes.entities())
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
                                    Collection<Entity> targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                        .resolve(ctx.getSource());
                                    Key sound = ctx.getArgument("sound", Key.class);

                                    for(Entity p : targets){
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
                                            Collection<Entity> targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                .resolve(ctx.getSource());
                                            Key sound = ctx.getArgument("sound", Key.class);
                                            float volume = ctx.getArgument("volume", Float.class);

                                            for(Entity p : targets){
                                                CreateSound.sound(sound, net.kyori.adventure.sound.Sound.Source.MASTER, volume, 1f).playFor(p);
                                            }

                                            getExecutor(ctx.getSource()).sendMessage(
                                                Component.text("Played sound " + sound + " to " + targets.size() + " players.")
                                            );

                                            return 1;
                                        }).then(
                                            Commands.argument("pitch", FloatArgumentType.floatArg())
                                                .executes(ctx ->{
                                                    Collection<Entity> targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                        .resolve(ctx.getSource());
                                                    Key sound = ctx.getArgument("sound", Key.class);
                                                    float volume = ctx.getArgument("volume", Float.class);
                                                    float pitch = ctx.getArgument("pitch", Float.class);

                                                    for(Entity p : targets){
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

        if(plugin.getServer().getPluginManager().getPlugin("CrazyAdvancementsAPI") != null){
            dispatcher.then(
                Commands.literal("showtoast")
                    .then(
                        Commands.argument("targets", ArgumentTypes.players())
                            .then(
                                Commands.argument("item", CruxItemsArguments.itemHolder())
                                    .then(
                                        Commands.argument("type", StringArgumentType.string())
                                            .suggests((ctx, builder) ->{
                                                for (AdvancementDisplay.AdvancementFrame value : AdvancementDisplay.AdvancementFrame.values()) {
                                                    builder.suggest(value.toString().toLowerCase());
                                                }
                                                return builder.buildFuture();
                                            })
                                            .then(
                                                Commands.argument("message", StringArgumentType.greedyString())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource());
                                                        var item = ctx.getArgument("item", ItemHolder.class).value();
                                                        String rawType = ctx.getArgument("type", String.class);
                                                        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.parseStrict(rawType);
                                                        String message = ctx.getArgument("message", String.class);
                                                        for(Player player : targets) {
                                                            ToastNotification toast = new ToastNotification(item, message, frame);
                                                            toast.send(player);
                                                            sender.sendMessage("Displayed toast to " + player.getName());
                                                        }
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
            );
        }

        return dispatcher.build();
    }

    public PlayerProfile fetchSkin(String skinId, UUID uuid, String name) {
        try {
            URL url = URI.create("https://api.mineskin.org/get/id/" + skinId).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject texture = json.getAsJsonObject("data").getAsJsonObject("texture");

                String value = texture.get("value").getAsString();
                String signature = texture.get("signature").getAsString();

                PlayerProfile profile = plugin.getServer().createProfile(uuid, name);
                profile.setProperty(new ProfileProperty("textures", value, signature));

                return profile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source) {
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

}
