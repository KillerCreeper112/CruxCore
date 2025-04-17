package killercreepr.cruxcore.config.data;

import net.kyori.adventure.key.Key;

import java.util.List;

public record CfgCmdMenu(String cmd, String permission, Key menu, List<String> aliases) {
}
