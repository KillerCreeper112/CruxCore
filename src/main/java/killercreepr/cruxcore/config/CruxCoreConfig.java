package killercreepr.cruxcore.config;

import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CruxCoreConfig extends Cfg {
    public final NumCfgValue DEBUG = new NumCfgValue(0);
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
