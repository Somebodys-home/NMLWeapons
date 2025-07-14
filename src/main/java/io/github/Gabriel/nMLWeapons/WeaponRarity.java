package io.github.Gabriel.nMLWeapons;

import io.github.Gabriel.damagePlugin.customDamage.DamageType;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;

public enum WeaponRarity {
    COMMON,
    UNCOMMON,
    RARE,
    MYTHICAL,
    RELIC;

    public static String getWeaponRarityString(WeaponRarity rarity) {
        switch (rarity) {
            case COMMON -> { return "common"; }
            case UNCOMMON -> { return "uncommon"; }
            case RARE -> { return "rare"; }
            case MYTHICAL -> { return "mythical"; }
            case RELIC -> { return "relic"; }
            default -> { return ""; }
        }
    }

    public static WeaponRarity getWeaponRarityFromString(String string) {
        switch (string) {
            case "common" -> { return COMMON; }
            case "uncommon" -> { return UNCOMMON; }
            case "rare" -> { return RARE; }
            case "mythical" -> { return MYTHICAL; }
            case "relic" -> { return RELIC; }
            default -> { return null; }
        }
    }

    public static ChatColor getWeaponRarityColor(WeaponRarity rarity) {
        switch (rarity) {
            case COMMON -> { return ChatColor.GRAY; }
            case UNCOMMON -> { return ChatColor.GREEN; }
            case RARE -> { return ChatColor.AQUA; }
            case MYTHICAL -> { return ChatColor.LIGHT_PURPLE; }
            case RELIC -> { return ChatColor.DARK_RED; }
            default -> { return null; }
        }
    }
}
