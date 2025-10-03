package killercreepr.cruxcore.util;

import killercreepr.crux.core.util.CruxMath;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

@Deprecated
public class CruxSurvivalGearGen {
    private static final Random random = CruxMath.random();

    public enum Stage {
        EARLY,
        MID,
        LATE,
        END
    }

    public static void giveProgressionLoot(Player player, Stage stage) {
        switch (stage) {
            case EARLY -> giveEarlyGame(player);
            case MID -> giveMidGame(player);
            case LATE -> giveLateGame(player);
            case END -> giveEndGame(player);
        }
    }

    // Helper to create worn tools/armor
    private static ItemStack wornItem(Material mat, int minWear, int maxWear) {
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable dmg) {
            int maxDurability = mat.getMaxDurability();
            int wear = randomRange(minWear, maxWear);
            int damage = Math.min(maxDurability - 1, wear); // ensure not broken
            dmg.setDamage(damage);
            item.setItemMeta(meta);
        }
        return item;
    }

    // ---------------- EARLY GAME ----------------
    private static void giveEarlyGame(Player p) {
        Stage stage = Stage.EARLY;
        p.getInventory().addItem(maybeEnchant(wornItem(Material.STONE_PICKAXE, 20, 90), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.WOODEN_AXE, 5, 40), stage));
        if (random.nextBoolean()) p.getInventory().addItem(maybeEnchant(wornItem(Material.WOODEN_SWORD, 5, 40), stage));

        p.getInventory().addItem(new ItemStack(Material.COAL, randomRange(4, 12)));
        p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, randomRange(16, 32)));
        p.getInventory().addItem(new ItemStack(Material.OAK_LOG, randomRange(8, 16)));
        p.getInventory().addItem(new ItemStack(Material.TORCH, randomRange(8, 24)));

        p.getInventory().addItem(new ItemStack(Material.BREAD, randomRange(4, 10)));
    }

    // ---------------- MID GAME ----------------
    private static void giveMidGame(Player p) {
        Stage stage = Stage.EARLY;
        p.getInventory().addItem(maybeEnchant(wornItem(Material.IRON_PICKAXE, 40, 120), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.IRON_AXE, 40, 120), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.IRON_SWORD, 40, 120), stage));
        if (random.nextInt(10) < 4) p.getInventory().addItem(maybeEnchant(wornItem(Material.SHIELD, 60, 180), stage));

        // Armor set (some pieces, not always full)
        if (random.nextInt(10) < 7) p.getInventory().addItem(maybeEnchant(wornItem(Material.IRON_HELMET, 40, 120), stage));
        if (random.nextInt(10) < 8) p.getInventory().addItem(maybeEnchant(wornItem(Material.IRON_CHESTPLATE, 60, 180), stage));
        if (random.nextInt(10) < 7) p.getInventory().addItem(maybeEnchant(wornItem(Material.IRON_LEGGINGS, 60, 180), stage));
        if (random.nextInt(10) < 6) p.getInventory().addItem(maybeEnchant(wornItem(Material.IRON_BOOTS, 40, 120), stage));

        p.getInventory().addItem(new ItemStack(Material.IRON_INGOT, randomRange(6, 16)));
        p.getInventory().addItem(new ItemStack(Material.COAL, randomRange(12, 32)));
        if (random.nextInt(10) < 5) p.getInventory().addItem(new ItemStack(Material.REDSTONE, randomRange(6, 20)));
        if (random.nextInt(10) < 3) p.getInventory().addItem(new ItemStack(Material.LAPIS_LAZULI, randomRange(4, 12)));
        if (random.nextInt(10) < 2) p.getInventory().addItem(new ItemStack(Material.DIAMOND, randomRange(1, 3)));

        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, randomRange(6, 12)));
        p.getInventory().addItem(new ItemStack(Material.TORCH, randomRange(16, 48)));
        if (random.nextBoolean()) p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
    }

    // ---------------- LATE GAME ----------------
    private static void giveLateGame(Player p) {
        Stage stage = Stage.EARLY;
        p.getInventory().addItem(maybeEnchant(wornItem(Material.DIAMOND_PICKAXE, 100, 400), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.DIAMOND_AXE, 100, 400), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.DIAMOND_SWORD, 100, 400), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.SHIELD, 120, 300), stage));
        if (random.nextInt(10) < 4) p.getInventory().addItem(maybeEnchant(wornItem(Material.BOW, 50, 200), stage));

        // Diamond armor (higher chance for full set than mid-game)
        if (random.nextInt(10) < 8) p.getInventory().addItem(maybeEnchant(wornItem(Material.DIAMOND_HELMET, 100, 400), stage));
        if (random.nextInt(10) < 9) p.getInventory().addItem(maybeEnchant(wornItem(Material.DIAMOND_CHESTPLATE, 150, 500), stage));
        if (random.nextInt(10) < 8) p.getInventory().addItem(maybeEnchant(wornItem(Material.DIAMOND_LEGGINGS, 120, 450), stage));
        if (random.nextInt(10) < 7) p.getInventory().addItem(maybeEnchant(wornItem(Material.DIAMOND_BOOTS, 100, 350), stage));

        p.getInventory().addItem(new ItemStack(Material.DIAMOND, randomRange(2, 8)));
        p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, randomRange(4, 12)));
        p.getInventory().addItem(new ItemStack(Material.IRON_INGOT, randomRange(8, 20)));
        p.getInventory().addItem(new ItemStack(Material.COAL, randomRange(16, 48)));
        p.getInventory().addItem(new ItemStack(Material.REDSTONE, randomRange(8, 24)));
        p.getInventory().addItem(new ItemStack(Material.LAPIS_LAZULI, randomRange(8, 16)));
        if (random.nextInt(10) < 3) p.getInventory().addItem(new ItemStack(Material.NETHERITE_SCRAP, 1));

        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, randomRange(12, 20)));
        p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, randomRange(1, 2)));
        p.getInventory().addItem(new ItemStack(Material.TORCH, randomRange(32, 64)));

        if (random.nextInt(10) < 6) p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
        if (random.nextInt(10) < 3) p.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
    }

    // ---------------- END GAME ----------------
    private static void giveEndGame(Player p) {
        Stage stage = Stage.EARLY;
        // Netherite Gear (used)
        p.getInventory().addItem(maybeEnchant(wornItem(Material.NETHERITE_PICKAXE, 200, 800), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.NETHERITE_AXE, 200, 800), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.NETHERITE_SWORD, 200, 800), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.NETHERITE_CHESTPLATE, 200, 600), stage));
        p.getInventory().addItem(maybeEnchant(wornItem(Material.SHIELD, 200, 400), stage));

        // High-end resources
        p.getInventory().addItem(new ItemStack(Material.NETHERITE_INGOT, randomRange(1, 4)));
        p.getInventory().addItem(new ItemStack(Material.DIAMOND, randomRange(6, 16)));
        p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, randomRange(8, 20)));
        p.getInventory().addItem(new ItemStack(Material.ANCIENT_DEBRIS, randomRange(1, 3)));

        // Late-game survival items
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, randomRange(16, 24)));
        p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, randomRange(2, 4)));
        if (random.nextInt(10) < 2) p.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));

        // End-related goodies (rare chance)
        if (random.nextInt(10) < 3) p.getInventory().addItem(wornItem(Material.ELYTRA, 20, 400));
        if (random.nextInt(10) < 4) p.getInventory().addItem(new ItemStack(Material.SHULKER_SHELL, randomRange(1, 4)));

        // Utility
        p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, randomRange(2, 8)));
        if (random.nextInt(10) < 5) p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
        if (random.nextInt(10) < 3) p.getInventory().addItem(new ItemStack(Material.ENDER_CHEST));
    }

    private static ItemStack maybeEnchant(ItemStack item, Stage stage) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Small chance depending on stage
        int chance = switch (stage) {
            case EARLY -> 0;      // No enchants early game
            case MID -> 25;       // 25% chance
            case LATE -> 50;      // 50% chance
            case END -> 75;       // 75% chance
        };

        if (random.nextInt(100) < chance) {
            // Pick a few common enchantments
            if (meta instanceof Damageable) {
                // Tools / weapons
                if (item.getType().toString().contains("SWORD")) {
                    meta.addEnchant(Enchantment.SHARPNESS, randomRange(1, 3), true);
                    if (random.nextBoolean()) meta.addEnchant(Enchantment.UNBREAKING, randomRange(1, 2), true);
                } else if (item.getType().toString().contains("PICKAXE") || item.getType().toString().contains("AXE")) {
                    meta.addEnchant(Enchantment.EFFICIENCY, randomRange(1, 3), true);
                    if (random.nextInt(10) < 3) meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                } else if (item.getType().toString().contains("BOW")) {
                    meta.addEnchant(Enchantment.POWER, randomRange(1, 3), true);
                    if (random.nextBoolean()) meta.addEnchant(Enchantment.INFINITY, 1, true);
                }
            }

            // Armor protection enchants
            if (item.getType().toString().contains("HELMET") ||
                item.getType().toString().contains("CHESTPLATE") ||
                item.getType().toString().contains("LEGGINGS") ||
                item.getType().toString().contains("BOOTS")) {
                meta.addEnchant(Enchantment.PROTECTION, randomRange(1, 3), true);
                if (random.nextInt(10) < 4) meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            }

            item.setItemMeta(meta);
        }

        return item;
    }


    private static int randomRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
