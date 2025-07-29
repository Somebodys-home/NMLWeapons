package io.github.Gabriel.nMLWeapons;

import io.github.Gabriel.damagePlugin.customDamage.DamageManager;
import io.github.Gabriel.damagePlugin.customDamage.DamageLore;
import io.github.Gabriel.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLItems.ItemRarity;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
    private DamageManager damageManager;
    private NamespacedKey originalNameKey;
    private NamespacedKey levelKey;

    public WeaponSystem(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        damageManager = new DamageManager();
        originalNameKey = new NamespacedKey(nmlWeapons, "original_name");
        levelKey = new NamespacedKey(nmlWeapons, "level");
    }

    public ItemStack generateWeapon(Player receiver, ItemType type, ItemRarity rarity, int level) {
        ItemStack weapon = new ItemStack(ItemType.getItemTypeMaterial(type));
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        pdc.set(makeItemTypeKey(type), PersistentDataType.INTEGER, 1);
        pdc.set(makeItemRarityKey(rarity), PersistentDataType.INTEGER, 1);
        pdc.set(levelKey, PersistentDataType.INTEGER, level);
        weapon.setItemMeta(meta);

        String name = generateWeaponName(type, rarity, level);
        meta.setDisplayName(name);
        pdc.set(originalNameKey, PersistentDataType.STRING, name);

        lore.add(ItemRarity.getItemRarityColor(rarity) + "" + ChatColor.BOLD + ItemRarity.getItemRarityString(rarity).toUpperCase() + " " + ItemType.getItemTypeString(type).toUpperCase());
        lore.add("");
        meta.setLore(lore);
        weapon.setItemMeta(meta);

        generateWeaponDamage(weapon, type, rarity, level);
        updateUnusableWeaponName(weapon, isWeaponUsable(weapon, receiver));

        return weapon;
    }

    public String generateWeaponName(ItemType type, ItemRarity rarity, int level) {
        String[] nameSegments = null;
        String name = "";

        if (rarity == ItemRarity.COMMON) {

            nameSegments = new String[2];
            List<String> badAdjectives = new ArrayList<>(List.of("Garbage", "Awful", "Do Better", "Babies' First", "Oh God That", "Rotten", "Poor", "Degrading", "Forgotten", "Racist"));

            nameSegments[0] = badAdjectives.get(ThreadLocalRandom.current().nextInt(badAdjectives.size()));

        } else if (rarity == ItemRarity.UNCOMMON || rarity == ItemRarity.RARE) {

            nameSegments = new String[3];
            List<String> goodAdjectives = new ArrayList<>(List.of("Pretty Alright", "Solid", "Well-Made", "Lifelong", "Based", "W", "Almost Mythical", "Neato Dorito", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(goodAdjectives.size());

            nameSegments[0] = goodAdjectives.get(randomAdjective);
            goodAdjectives.remove(randomAdjective);
            nameSegments[1] = goodAdjectives.get(ThreadLocalRandom.current().nextInt(goodAdjectives.size()));

        } else if (rarity == ItemRarity.MYTHICAL) {

            nameSegments = new String[3];
            List<String> greatAdjectives = new ArrayList<>(List.of("Amazing", "Godly", "King's", "Fabled", "Based", "W", "Legendary", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(greatAdjectives.size());

            nameSegments[0] = greatAdjectives.get(randomAdjective);
            greatAdjectives.remove(randomAdjective);
            nameSegments[1] = greatAdjectives.get(ThreadLocalRandom.current().nextInt(greatAdjectives.size()));

        }

        assert nameSegments != null;
        if (type == ItemType.SWORD) {
            List<String> sword = new ArrayList<>(List.of("Le Stabby", "Seax", "Scimitar"));
            nameSegments[nameSegments.length - 1] = sword.get(ThreadLocalRandom.current().nextInt(sword.size()));
        } else if (type == ItemType.DAGGER) {
            List<String> dagger = new ArrayList<>(List.of("le small stabby"));
            nameSegments[nameSegments.length - 1] = dagger.get(ThreadLocalRandom.current().nextInt(dagger.size()));
        } else if (type == ItemType.AXE) {
            List<String> axe = new ArrayList<>(List.of("le curved stabby"));
            nameSegments[nameSegments.length - 1] = axe.get(ThreadLocalRandom.current().nextInt(axe.size()));
        } else if (type == ItemType.HAMMER) {
            List<String> hammer = new ArrayList<>(List.of("Squeaky Toy", "Blunt", "Mallet", "Bonker", "Hammer"));
            nameSegments[nameSegments.length - 1] = hammer.get(ThreadLocalRandom.current().nextInt(hammer.size()));
        } else if (type == ItemType.SPEAR) {
            List<String> spear = new ArrayList<>(List.of("Giant Arrow", "Javelin", "Military Fork", "Overcompensator"));
            nameSegments[nameSegments.length - 1] = spear.get(ThreadLocalRandom.current().nextInt(spear.size()));
        } else if (type == ItemType.GLOVE) {
            List<String> glove = new ArrayList<>(List.of("Jawbreaker", "TKO", "Rock 'Em", "Sock 'Em", "Jojo's Reference"));
            nameSegments[nameSegments.length - 1] = glove.get(ThreadLocalRandom.current().nextInt(glove.size()));
        } else if (type == ItemType.BOW) {
            List<String> bow = new ArrayList<>(List.of("le flying stabby"));
            nameSegments[nameSegments.length - 1] = bow.get(ThreadLocalRandom.current().nextInt(bow.size()));
        } else if (type == ItemType.WAND) {
            List<String> wand = new ArrayList<>(List.of("le magical stabby"));
            nameSegments[nameSegments.length - 1] = wand.get(ThreadLocalRandom.current().nextInt(wand.size()));
        } else if (type == ItemType.STAFF) {
            List<String> staff = new ArrayList<>(List.of("le long magical stabby"));
            nameSegments[nameSegments.length - 1] = staff.get(ThreadLocalRandom.current().nextInt(staff.size()));
        } else if (type == ItemType.CATALYST) {
            List<String> catalyst = new ArrayList<>(List.of("le weird stabby"));
            nameSegments[nameSegments.length - 1] = catalyst.get(ThreadLocalRandom.current().nextInt(catalyst.size()));
        }

        name += "§o§fLv. " + level + "§r " + ItemRarity.getItemRarityColor(rarity);
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
        if (weapon == null || !weapon.hasItemMeta()) return false;

        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Integer itemLevel = pdc.get(levelKey, PersistentDataType.INTEGER);

        if (itemLevel == null) return false;

        int playerLevel = new ProfileManager(nmlWeapons.getNmlPlayerStats()).getPlayerProfile(player.getUniqueId()).getStats().getLevel();
        return playerLevel >= itemLevel;
    }


    public void generateWeaponDamage(ItemStack weapon, ItemType type, ItemRarity rarity, int level) {
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
                damageManager.setDamage(weapon, firstType, firstDamage);
            }
            case UNCOMMON, RARE -> {
                if (firstType == secondType) {
                    damageManager.setDamage(weapon, firstType, firstDamage + secondDamage);
                } else {
                    damageManager.setDamage(weapon, firstType, firstDamage);
                    damageManager.setDamage(weapon, secondType, secondDamage);
                }
            }
            case MYTHICAL -> {
                firstDamage = level * 3;

                if (firstType == secondType) {
                    damageManager.setDamage(weapon, firstType, firstDamage + secondDamage);
                } else {
                    damageManager.setDamage(weapon, firstType, firstDamage);
                    damageManager.setDamage(weapon, secondType, secondDamage);
                }
            }
        }

        DamageLore.updateLoreWithElementalDamage(weapon, weapon.getItemMeta());
    }

    public NamespacedKey makeItemTypeKey(ItemType type) {
        return new NamespacedKey(nmlWeapons, ItemType.getItemTypeString(type));
    }

    public ItemType getItemType(ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for (ItemType type : ItemType.values()) {
            if (pdc.has(makeItemTypeKey(type), PersistentDataType.INTEGER)) return type;
        }

        return null;
    }

    public NamespacedKey makeItemRarityKey(ItemRarity rarity) {
        return new NamespacedKey(nmlWeapons, ItemRarity.getItemRarityString(rarity));
    }

    public ItemRarity getItemRarity(ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for (ItemRarity rarity : ItemRarity.values()) {
            if (pdc.has(makeItemRarityKey(rarity), PersistentDataType.INTEGER)) return rarity;
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
