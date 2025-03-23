package killercreepr.cruxcore.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.api.plugin.module.CruxModule;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.command.argument.CruxCmdArguments;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxString;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.CruxCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DevCommands {
    protected final @NotNull CruxCore plugin;

    public DevCommands(@NotNull CruxCore plugin) {
        this.plugin = plugin;
    }

    public void register(@NotNull CruxPlugin plugin){
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->{
            final Commands commands = event.registrar();
            LiteralCommandNode<CommandSourceStack> cmd = build(Commands.literal("filedev")
                .requires(source -> source.getSender().hasPermission("cruxcore.cmds.filedev.use")),
                plugin.getLifecycleManager());
            commands.register(cmd);
        });
    }

    private final Map<String, Generation> generations = new MapBuilder<String, Generation>()
        .put("generated", new Generated())
        .put("handheld", new HandHeld())
        .build();
    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                                  LifecycleEventManager<?> manager){
        //cruxclaim view (player)
        dispatcher.then(
            Commands.literal("create")
                .then(
                    Commands.argument("type", StringArgumentType.string())
                        .suggests((d, e) ->{
                            List.of(
                                "handheld",
                                "bow",
                                "crossbow",
                                "fishing_rod",
                                "generated"
                            ).forEach(e::suggest);
                            return e.buildFuture();
                        })
                        .then(
                            Commands.argument("name", StringArgumentType.string())
                                .executes(ctx ->{
                                    Generation generation = generations.get(ctx.getArgument("type", String.class));
                                    String name = ctx.getArgument("name", String.class);
                                    generation.generate(name);
                                    return 1;
                                })
                                .then(
                                    Commands.argument("args", StringArgumentType.greedyString())
                                        .suggests((d, e) ->{
                                            List.of(
                                                "skin",
                                                "tools",
                                                "armor"
                                            ).forEach(e::suggest);
                                            return e.buildFuture();
                                        })
                                        .executes(ctx ->{
                                            String[] argsSplit = ctx.getArgument("args", String.class).split(",");
                                            Collection<String> args = new HashSet<>(List.of(argsSplit));
                                            Generation generation = generations.get(ctx.getArgument("type", String.class));
                                            String name = ctx.getArgument("name", String.class);
                                            onCMD(args, name, generation);
                                            return 1;
                                        })
                                )
                        )
                )
        )
        ;
        return dispatcher.build();
    }

    private void onCMD(Collection<String> args, String name, Generation generation){
        boolean x = false;
        if(args.contains("tools")){
            args.remove("tools");
            args.remove("armor");
            for(String s : List.of("pickaxe", "axe", "shovel", "hoe", "sword")){
                onCMD(args, name + s, generation);
            }
             x= true;
        }
        if(args.contains("armor")){
            args.remove("armor");
            args.remove("tools");
            for(String s : List.of("helmet", "chestplate", "leggings", "boots")){
                onCMD(args, name + s, generation);
            }
            x = true;
        }
        if(x) return;

        generation.generate(name);

        if(args.contains("skin")){
            CruxJson json = new CruxJson(CruxCore.core(), "dev/generated/skin_items/" + parseName(name));
            json.serialize("model", new FileObject()
                .addProperty("type", "minecraft:composite")
                .add("models", new FileArray(List.of(
                    new FileObject().addProperty("type", "model")
                        .addProperty("model", "crux:item/skin_base"),
                    new FileObject().addProperty("type", "model")
                        .addProperty("model", Crux.key(name).asString())
                )))
            );
            json.save();
        }
    }



    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source) {
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

    public static String parseName(String name){
        return name.replace("crux:item/", "");
    }

    private static class Generation{
        public void generate(String name){

        }
    }

    private static class Generated extends Generation{
        @Override
        public void generate(String name) {
            CruxJson json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name));
            json.serialize("parent", "minecraft:item/generated");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString()));
            json.save();

            json = new CruxJson(CruxCore.core(), "dev/generated/items/" + parseName(name));
            json.serialize("model", new FileObject().addProperty("type", "model")
                .addProperty("model", Crux.key(name).asString()));
            json.save();
        }
    }

    private static class HandHeld extends Generation{
        @Override
        public void generate(String name) {
            CruxJson json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name));
            json.serialize("parent", "minecraft:item/handheld");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString()));
            json.save();

            json = new CruxJson(CruxCore.core(), "dev/generated/items/" + parseName(name));
            json.serialize("model", new FileObject().addProperty("type", "model")
                .addProperty("model", Crux.key(name).asString()));
            json.save();
        }
    }
}
