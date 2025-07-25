package io.github.Gabriel.nMLWeapons;

import io.github.Gabriel.damagePlugin.customDamage.DamageKey;
import io.github.Gabriel.damagePlugin.customDamage.DamageLoreUtil;
import io.github.Gabriel.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.Gabriel.damagePlugin.customDamage.DamageType.*;
import static io.github.Gabriel.damagePlugin.customDamage.DamageType.AIR;
import static io.github.Gabriel.damagePlugin.customDamage.DamageType.DARK;
import static io.github.Gabriel.damagePlugin.customDamage.DamageType.EARTH;
import static io.github.Gabriel.damagePlugin.customDamage.DamageType.LIGHT;
import static io.github.Gabriel.damagePlugin.customDamage.DamageType.LIGHTNING;
import static io.github.Gabriel.damagePlugin.customDamage.DamageType.PURE;

public class WeaponSystem {
    private NMLWeapons nmlWeapons;
    private DamageKey damageKey;
    private NamespacedKey originalNameKey;
    private NamespacedKey levelKey;

    public WeaponSystem(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        damageKey = new DamageKey();
        originalNameKey = new NamespacedKey(nmlWeapons, "original_name");
        levelKey = new NamespacedKey(nmlWeapons, "level");
    }

    public ItemStack generateWeapon(Player receiver, WeaponType type, WeaponRarity rarity, int level) {
        ItemStack weapon = new ItemStack(WeaponType.getWeaponTypeMaterial(type));
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        pdc.set(makeWeaponTypeKey(type), PersistentDataType.INTEGER, 1);
        pdc.set(makeWeaponRarityKey(rarity), PersistentDataType.INTEGER, 1);
        pdc.set(levelKey, PersistentDataType.INTEGER, level);
        weapon.setItemMeta(meta);

        String name = generateWeaponName(type, rarity, level);
        meta.setDisplayName(name);
        pdc.set(originalNameKey, PersistentDataType.STRING, name);

        lore.add(WeaponRarity.getWeaponRarityColor(rarity) + "" + ChatColor.BOLD + WeaponRarity.getWeaponRarityString(rarity).toUpperCase() + " " + WeaponType.getWeaponTypeString(type).toUpperCase());
        lore.add("");
        meta.setLore(lore);
        weapon.setItemMeta(meta);

        generateWeaponDamage(weapon, type, rarity, level);
        updateUnusableWeaponName(weapon, isWeaponUsable(weapon, receiver));

        return weapon;
    }

    public String generateWeaponName(WeaponType type, WeaponRarity rarity, int level) {
        String[] nameSegments = null;
        String name = "";

        if (rarity == WeaponRarity.COMMON) {

            nameSegments = new String[2];
            List<String> badAdjectives = new ArrayList<>(List.of("Garbage", "Awful", "Do Better", "Babies' First", "Oh God That", "Rotten", "Poor", "Degrading", "Forgotten", "Racist"));

            nameSegments[0] = badAdjectives.get(ThreadLocalRandom.current().nextInt(badAdjectives.size()));

        } else if (rarity == WeaponRarity.UNCOMMON || rarity == WeaponRarity.RARE) {

            nameSegments = new String[3];
            List<String> goodAdjectives = new ArrayList<>(List.of("Pretty Alright", "Solid", "Well-Made", "Lifelong", "Based", "W", "Almost Mythical", "Neato Dorito", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(goodAdjectives.size());

            nameSegments[0] = goodAdjectives.get(randomAdjective);
            goodAdjectives.remove(randomAdjective);
            nameSegments[1] = goodAdjectives.get(ThreadLocalRandom.current().nextInt(goodAdjectives.size()));

        } else if (rarity == WeaponRarity.MYTHICAL) {

            nameSegments = new String[3];
            List<String> greatAdjectives = new ArrayList<>(List.of("Amazing", "Godly", "King's", "Fabled", "Based", "W", "Legendary", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(greatAdjectives.size());

            nameSegments[0] = greatAdjectives.get(randomAdjective);
            greatAdjectives.remove(randomAdjective);
            nameSegments[1] = greatAdjectives.get(ThreadLocalRandom.current().nextInt(greatAdjectives.size()));

        }

        if (type == WeaponType.SWORD) {
            List<String> sword = new ArrayList<>(List.of("Le Stabby", "Seax", "Scimitar"));
            nameSegments[nameSegments.length - 1] = sword.get(ThreadLocalRandom.current().nextInt(sword.size()));
        } else if (type == WeaponType.DAGGER) {
            List<String> dagger = new ArrayList<>(List.of("le small stabby"));
            nameSegments[nameSegments.length - 1] = dagger.get(ThreadLocalRandom.current().nextInt(dagger.size()));
        } else if (type == WeaponType.AXE) {
            List<String> axe = new ArrayList<>(List.of("le curved stabby"));
            nameSegments[nameSegments.length - 1] = axe.get(ThreadLocalRandom.current().nextInt(axe.size()));
        } else if (type == WeaponType.HAMMER) {
            List<String> hammer = new ArrayList<>(List.of("Squeaky Toy", "Blunt", "Mallet", "Bonker", "Hammer"));
            nameSegments[nameSegments.length - 1] = hammer.get(ThreadLocalRandom.current().nextInt(hammer.size()));
        } else if (type == WeaponType.SPEAR) {
            List<String> spear = new ArrayList<>(List.of("Giant Arrow", "Javelin", "Military Fork", "Overcompensator"));
            nameSegments[nameSegments.length - 1] = spear.get(ThreadLocalRandom.current().nextInt(spear.size()));
        } else if (type == WeaponType.GLOVE) {
            List<String> glove = new ArrayList<>(List.of("Jawbreaker", "TKO", "Rock 'Em", "Sock 'Em", "Jojo's Reference"));
            nameSegments[nameSegments.length - 1] = glove.get(ThreadLocalRandom.current().nextInt(glove.size()));
        } else if (type == WeaponType.BOW) {
            List<String> bow = new ArrayList<>(List.of("le flying stabby"));
            nameSegments[nameSegments.length - 1] = bow.get(ThreadLocalRandom.current().nextInt(bow.size()));
        } else if (type == WeaponType.WAND) {
            List<String> wand = new ArrayList<>(List.of("le magical stabby"));
            nameSegments[nameSegments.length - 1] = wand.get(ThreadLocalRandom.current().nextInt(wand.size()));
        } else if (type == WeaponType.STAFF) {
            List<String> staff = new ArrayList<>(List.of("le long magical stabby"));
            nameSegments[nameSegments.length - 1] = staff.get(ThreadLocalRandom.current().nextInt(staff.size()));
        } else if (type == WeaponType.CATALYST) {
            List<String> catalyst = new ArrayList<>(List.of("le weird stabby"));
            nameSegments[nameSegments.length - 1] = catalyst.get(ThreadLocalRandom.current().nextInt(catalyst.size()));
        }

        name += "§o§fLv. " + level + "§r " + WeaponRarity.getWeaponRarityColor(rarity);
        for (int i = 0; i < nameSegments.length; i++) {
            if (i == nameSegments.length - 1) {
                name += nameSegments[i];
            } else {
                name += nameSegments[i] + " ";
            }
        }

        return name;
    }

    public void updateUnusableWeaponName(ItemStack weapon, boolean unusable) {
        ItemMeta meta = weapon.getItemMeta();
        String originalName = getOriginalItemName(weapon);
        String editedName;

        if (!unusable) {
            editedName = originalName.replaceAll("§[0-9a-fk-or]", "");
            editedName = "§c§m" + editedName;
        } else {
           editedName = originalName;
        }

        meta.setDisplayName(editedName);
        weapon.setItemMeta(meta);
    }

    public boolean isWeaponUsable(ItemStack weapon, Player player) {
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Integer itemLevel = pdc.get(levelKey, PersistentDataType.INTEGER);
        int playerLevel = new ProfileManager(nmlWeapons.getNmlPlayerStats()).getPlayerProfile(player.getUniqueId()).getStats().getLevel();

        return playerLevel >= itemLevel;
    }

    public void generateWeaponDamage(ItemStack weapon, WeaponType type, WeaponRarity rarity, int level) {
        List<DamageType> possibleFirstDamageTypes = null;
        List<DamageType> possibleSecondDamageTypes = null;

        switch (type) {
            case SWORD, AXE, HAMMER, SPEAR, GLOVE -> {
                possibleFirstDamageTypes = new ArrayList<>(List.of(PHYSICAL));
                possibleSecondDamageTypes = new ArrayList<>(List.of(PHYSICAL, FIRE, COLD, EARTH, LIGHTNING, AIR, LIGHT, DARK));
            }
            case DAGGER, BOW -> {
                possibleFirstDamageTypes = new ArrayList<>(List.of(PHYSICAL));
                possibleSecondDamageTypes = new ArrayList<>(List.of(PHYSICAL, FIRE, COLD, EARTH, LIGHTNING, AIR, LIGHT, DARK, PURE));
            }
            case WAND, STAFF, CATALYST -> {
                possibleFirstDamageTypes = new ArrayList<>(List.of(FIRE, COLD, EARTH, LIGHTNING, AIR, LIGHT, DARK));
                possibleSecondDamageTypes = new ArrayList<>(List.of(FIRE, COLD, EARTH, LIGHTNING, AIR, LIGHT, DARK));
            }
        }

        DamageType firstType = possibleFirstDamageTypes.get(ThreadLocalRandom.current().nextInt(possibleFirstDamageTypes.size()));
        int firstDamage = level * 2;
        DamageType secondType = possibleSecondDamageTypes.get(ThreadLocalRandom.current().nextInt(possibleSecondDamageTypes.size()));
        int secondDamage = level;

        switch (rarity) {
            case COMMON -> {
                damageKey.setDamage(weapon, firstType, firstDamage);
            }
            case UNCOMMON, RARE -> {
                if (firstType == secondType) {
                    damageKey.setDamage(weapon, firstType, firstDamage + secondDamage);
                } else {
                    damageKey.setDamage(weapon, firstType, firstDamage);
                    damageKey.setDamage(weapon, secondType, secondDamage);
                }
            }
            case MYTHICAL -> {
                firstDamage = level * 3;

                if (firstType == secondType) {
                    damageKey.setDamage(weapon, firstType, firstDamage + secondDamage);
                } else {
                    damageKey.setDamage(weapon, firstType, firstDamage);
                    damageKey.setDamage(weapon, secondType, secondDamage);
                }
            }
        }

        DamageLoreUtil.updateLoreWithElementalDamage(weapon, weapon.getItemMeta());
    }

    public NamespacedKey makeWeaponTypeKey(WeaponType type) {
        return new NamespacedKey(nmlWeapons, WeaponType.getWeaponTypeString(type));
    }

    public WeaponType getWeaponType(ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for (WeaponType type : WeaponType.values()) {
            if (pdc.has(makeWeaponTypeKey(type), PersistentDataType.INTEGER)) return type;
        }

        return null;
    }

    public NamespacedKey makeWeaponRarityKey(WeaponRarity rarity) {
        return new NamespacedKey(nmlWeapons, WeaponRarity.getWeaponRarityString(rarity));
    }

    public WeaponRarity getWeaponRarity(ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for (WeaponRarity rarity : WeaponRarity.values()) {
            if (pdc.has(makeWeaponRarityKey(rarity), PersistentDataType.INTEGER)) return rarity;
        }

        return null;
    }

    public String getOriginalItemName(ItemStack weapon) {
        if (weapon == null || weapon.getType().isAir()) return null;

        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(originalNameKey, PersistentDataType.STRING)) return null;

        return pdc.get(originalNameKey, PersistentDataType.STRING);
    }
}
