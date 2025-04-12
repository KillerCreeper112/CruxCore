package killercreepr.cruxcore.config;

import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CruxCoreConfig extends Cfg {
    public final NumCfgValue DEBUG = new NumCfgValue(0);
    public final CfgValue<Map<String, String>> GLOBAL_STRING_TAGS = new CommonValue<>(){};
    public final CfgValue<Map<String, List<String>>> GLOBAL_STRING_LIST_TAGS = new CommonValue<>(){};
    public final CfgValue<List<Key>> AUTO_LOAD_WORLDS = new CommonValue<>(){};
    public CruxCoreConfig(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public CruxCoreConfig(@NotNull File file) {
        super(file);
    }

    public CruxCoreConfig(@NotNull CruxConfig cfg) {
        super(cfg);
    }
}
