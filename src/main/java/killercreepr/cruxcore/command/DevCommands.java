package killercreepr.cruxcore.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.CruxCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        .put("bow", new Bow())
        .put("crossbowbow", new Crossbow())
        .put("fishing_rod", new FishingRod())
        .build();
    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                                  LifecycleEventManager<?> manager){
        //cruxclaim view (player)
        dispatcher.then(
            Commands.literal("create")
                .then(
                    Commands.argument("type", StringArgumentType.string())
                        .suggests((d, e) ->{
                            generations.keySet().forEach(e::suggest);
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

    private static class Bow extends Generation{
        @Override
        public void generate(String name) {
            //{
            //  "parent": "minecraft:item/bow",
            //  "textures": {
            //    "layer0": "crux:item/ice_bow_pulling_1"
            //  }
            //}
            CruxJson json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name));
            json.serialize("parent", "minecraft:item/bow");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString()));
            json.save();

            for(int i = 0; i < 3; i++){
                json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name) + "_pulling_" + i);
                json.serialize("parent", "minecraft:item/bow");
                json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name + "_pulling_" + i).asString()));
                json.save();
            }
            String pulling0 = Crux.key(name) + "_pulling_0";
            String pulling1 = Crux.key(name) + "_pulling_1";
            String pulling2 = Crux.key(name) + "_pulling_2";
            String jsonText = String.format("""
        {
          "model": {
            "type": "minecraft:condition",
            "on_false": {
              "type": "minecraft:model",
              "model": "crux:item/%s"
            },
            "on_true": {
              "type": "minecraft:range_dispatch",
              "entries": [
                {
                  "model": {
                    "type": "minecraft:model",
                    "model": "%s"
                  },
                  "threshold": 0.65
                },
                {
                  "model": {
                    "type": "minecraft:model",
                    "model": "%s"
                  },
                  "threshold": 0.9
                }
              ],
              "fallback": {
                "type": "minecraft:model",
                "model": "%s"
              },
              "property": "minecraft:use_duration",
              "scale": 0.05
            },
            "property": "minecraft:using_item"
          }
        }
        """, Crux.key(name).asString(), pulling1, pulling2, pulling0);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try {
                // Ensure the directory exists
                File file = new CruxJson(CruxCore.core(), "dev/generated/items/" + parseName(name), false).file();
                file.getParentFile().mkdirs();

                // Write to the file
                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Crossbow extends Generation{
        @Override
        public void generate(String name) {
            //{
            //  "parent": "minecraft:item/crossbow",
            //  "textures": {
            //    "layer0": "crux:item/ancient_crossbow_firework"
            //  }
            //}
            CruxJson json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name));
            json.serialize("parent", "minecraft:item/crossbow");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString() + "_standby"));
            json.save();
            json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name) + "_arrow");
            json.serialize("parent", "minecraft:item/crossbow");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString() + "_arrow"));
            json.save();
            json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name) + "_firework");
            json.serialize("parent", "minecraft:item/crossbow");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString() + "_firework"));
            json.save();

            for(int i = 0; i < 3; i++){
                json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name) + "_pulling_" + i);
                json.serialize("parent", "minecraft:item/crossbow");
                json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString() + "_pulling_" + i));
                json.save();
            }
            String arrow = Crux.key(name) + "_arrow";
            String firework = Crux.key(name) + "_firework";
            //name
            String pulling1 = Crux.key(name) + "_pulling_1";
            String pulling2 = Crux.key(name) + "_pulling_2";
            String pulling0 = Crux.key(name) + "_pulling_0";
            String jsonText = String.format("""
                {
                  "model": {
                    "type": "minecraft:condition",
                    "on_false": {
                      "type": "minecraft:select",
                      "cases": [
                        {
                          "model": {
                            "type": "minecraft:model",
                            "model": "%s"
                          },
                          "when": "arrow"
                        },
                        {
                          "model": {
                            "type": "minecraft:model",
                            "model": "%s"
                          },
                          "when": "rocket"
                        }
                      ],
                      "fallback": {
                        "type": "minecraft:model",
                        "model": "%s"
                      },
                      "property": "minecraft:charge_type"
                    },
                    "on_true": {
                      "type": "minecraft:range_dispatch",
                      "entries": [
                        {
                          "model": {
                            "type": "minecraft:model",
                            "model": "%s"
                          },
                          "threshold": 0.58
                        },
                        {
                          "model": {
                            "type": "minecraft:model",
                            "model": "%s"
                          },
                          "threshold": 1.0
                        }
                      ],
                      "fallback": {
                        "type": "minecraft:model",
                        "model": "%s"
                      },
                      "property": "minecraft:crossbow/pull"
                    },
                    "property": "minecraft:using_item"
                  }
                }
        """, arrow, firework, Crux.key(name).asString(), pulling1, pulling2, pulling0);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try {
                // Ensure the directory exists
                File file = new CruxJson(CruxCore.core(), "dev/generated/items/" + parseName(name), false).file();
                file.getParentFile().mkdirs();

                // Write to the file
                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static class FishingRod extends Generation{
        @Override
        public void generate(String name) {
            CruxJson json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name));
            json.serialize("parent", "minecraft:item/handheld_rod");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString()));
            json.save();

            json = new CruxJson(CruxCore.core(), "dev/generated/models/" + parseName(name) + "_cast");
            json.serialize("parent", "minecraft:item/handheld_rod");
            json.serialize("textures", new FileObject().addProperty("layer0", Crux.key(name).asString() + "_cast"));
            json.save();

            String jsonText = String.format("""
                {
                          "model": {
                            "type": "minecraft:condition",
                            "on_false": {
                              "type": "minecraft:model",
                              "model": "%s"
                            },
                            "on_true": {
                              "type": "minecraft:model",
                              "model": "%s"
                            },
                            "property": "minecraft:fishing_rod/cast"
                          }
                        }
        """, Crux.key(name).asString(), Crux.key(name) + "_cast");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try {
                // Ensure the directory exists
                File file = new CruxJson(CruxCore.core(), "dev/generated/items/" + parseName(name), false).file();
                file.getParentFile().mkdirs();

                // Write to the file
                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
