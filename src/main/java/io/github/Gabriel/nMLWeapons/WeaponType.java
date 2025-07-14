package io.github.Gabriel.nMLWeapons;

import org.bukkit.Material;

public enum WeaponType {
    SWORD,
    DAGGER,
    AXE,
    HAMMER,
    SPEAR,
    GLOVE,
    BOW,
    WAND,
    STAFF,
    CATALYST;

    public static String getWeaponTypeString(WeaponType type) {
        switch (type) {
            case SWORD -> { return "sword"; }
            case DAGGER -> { return "dagger"; }
            case AXE -> { return "axe"; }
            case HAMMER -> { return "hammer"; }
            case SPEAR -> { return "spear"; }
            case GLOVE -> { return "glove"; }
            case BOW -> { return "bow"; }
            case WAND -> { return "wand"; }
            case STAFF -> { return "staff"; }
            case CATALYST -> { return "catalyst"; }
            default -> { return ""; }
        }
    }

    public static WeaponType getWeaponTypeFromString(String string) {
        switch (string) {
            case "sword" -> { return SWORD; }
            case "dagger" -> { return DAGGER; }
            case "axe" -> { return AXE; }
            case "hammer" -> { return HAMMER; }
            case "spear" -> { return SPEAR; }
            case "glove" -> { return GLOVE; }
            case "bow" -> { return BOW; }
            case "wand" -> { return WAND; }
            case "staff" -> { return STAFF; }
            case "catalyst" -> { return CATALYST; }
            default -> { return null; }
        }
    }

    public static Material getWeaponTypeMaterial(WeaponType type) {
        switch (type) {
            case SWORD, DAGGER -> { return Material.IRON_SWORD; }
            case AXE -> { return Material.IRON_AXE; }
            case HAMMER -> { return Material.MACE; }
            case SPEAR -> { return Material.TRIDENT; }
            case GLOVE -> { return Material.RED_WOOL; }
            case BOW -> { return Material.BOW; }
            case WAND, STAFF -> { return Material.STICK; }
            case CATALYST -> { return Material.ENCHANTED_BOOK; }
            default -> { return null; }
        }
    }
}
