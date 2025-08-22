package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLItems.ItemRarity;
import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeaponSystem {
    public WeaponSystem() {}

    public ItemStack generateWeapon(Player receiver, ItemType type, ItemRarity rarity, int level) {
        ItemStack weapon = new ItemStack(ItemType.getItemTypeMaterial(type));
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        pdc.set(ItemSystem.makeItemTypeKey(type), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.makeItemRarityKey(rarity), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.getLevelKey(), PersistentDataType.INTEGER, level);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        weapon.setItemMeta(meta);

        String name = generateWeaponName(type, rarity, level);
        meta.setDisplayName(name);
        pdc.set(ItemSystem.getOriginalNameKey(), PersistentDataType.STRING, name);

        lore.add(ItemRarity.getItemRarityColor(rarity) + "" + ChatColor.BOLD + ItemRarity.getItemRarityString(rarity).toUpperCase() + " " + ItemType.getItemTypeString(type).toUpperCase());
        meta.setLore(lore);
        weapon.setItemMeta(meta);

        addASCIIArtToWeapon(weapon, type);
        generateWeaponDamage(weapon, type, rarity, level);
        ItemSystem.updateUnusableItemName(weapon, ItemSystem.isItemUsable(weapon, receiver));

        return weapon;
    }

    public String generateWeaponName(ItemType type, ItemRarity rarity, int level) {
        String[] nameSegments = null;
        String name = "";

        if (rarity == ItemRarity.COMMON) {
            nameSegments = new String[2];
            List<String> badAdjectives = new ArrayList<>(List.of("Garbage", "Awful", "Pitiful", "You Deserve This", "Disgusting", "Be Better", "Babies' First", "Oh God That", "Rotten", "Poor", "Degrading", "Forgotten", "Racist"));

            nameSegments[0] = badAdjectives.get(ThreadLocalRandom.current().nextInt(badAdjectives.size()));
        } else if (rarity == ItemRarity.UNCOMMON) {
            nameSegments = new String[2];
            List<String> goodAdjectives = new ArrayList<>(List.of("Pretty Alright", "Lifelong", "Based", "Neato Dorito", "Goofy Ahh", "Nobodies'", "Knave's"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(goodAdjectives.size());

            nameSegments[0] = goodAdjectives.get(randomAdjective);
        } else if (rarity == ItemRarity.RARE) {
            nameSegments = new String[3];
            List<String> goodAdjectives = new ArrayList<>(List.of("Pretty Alright", "Solid", "Well-Made", "Lifelong", "Based", "W", "Almost Mythical", "Neato Dorito", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(goodAdjectives.size());

            nameSegments[0] = goodAdjectives.get(randomAdjective);
            goodAdjectives.remove(randomAdjective);
            goodAdjectives.remove("Based");
            goodAdjectives.remove("Nobodies'");
            nameSegments[1] = goodAdjectives.get(ThreadLocalRandom.current().nextInt(goodAdjectives.size()));
        } else if (rarity == ItemRarity.MYTHICAL) {
            nameSegments = new String[3];
            List<String> greatAdjectives = new ArrayList<>(List.of("Amazing", "Godly", "King's", "Queen's", "Fabled", "Based", "W", "Legendaric", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(greatAdjectives.size());

            nameSegments[0] = greatAdjectives.get(randomAdjective);
            greatAdjectives.remove(randomAdjective);
            greatAdjectives.remove("Based");
            greatAdjectives.remove("King's");
            greatAdjectives.remove("Queen's");
            greatAdjectives.remove("Nobodies'");
            nameSegments[1] = greatAdjectives.get(ThreadLocalRandom.current().nextInt(greatAdjectives.size()));
        }

        assert nameSegments != null;
        if (type == ItemType.SWORD) {
            List<String> sword = new ArrayList<>(List.of("Sword", "Seax", "Scimitar", "Bigger Knife"));
            nameSegments[nameSegments.length - 1] = sword.get(ThreadLocalRandom.current().nextInt(sword.size()));
        } else if (type == ItemType.DAGGER) {
            List<String> dagger = new ArrayList<>(List.of("Dagger", "Knife", "Cutlery"));
            nameSegments[nameSegments.length - 1] = dagger.get(ThreadLocalRandom.current().nextInt(dagger.size()));
        } else if (type == ItemType.AXE) {
            List<String> axe = new ArrayList<>(List.of("Axe", "Hatchet", "Cleaver", "Battle Axe", "Tomahawk"));
            nameSegments[nameSegments.length - 1] = axe.get(ThreadLocalRandom.current().nextInt(axe.size()));
        } else if (type == ItemType.HAMMER) {
            List<String> hammer = new ArrayList<>(List.of("Squeaky Toy", "Blunt", "Mallet", "Bonker", "Hammer", "Piko Piko"));
            nameSegments[nameSegments.length - 1] = hammer.get(ThreadLocalRandom.current().nextInt(hammer.size()));
        } else if (type == ItemType.SPEAR) {
            List<String> spear = new ArrayList<>(List.of("Giant Arrow", "Javelin", "Military Fork", "Overcompensator", "Trident", "Spear", "Spork"));
            nameSegments[nameSegments.length - 1] = spear.get(ThreadLocalRandom.current().nextInt(spear.size()));
        } else if (type == ItemType.GLOVE) {
            List<String> glove = new ArrayList<>(List.of("Jawbreaker", "TKO", "Rock 'Em", "Sock 'Em", "Failure", "Gloves"));
            nameSegments[nameSegments.length - 1] = glove.get(ThreadLocalRandom.current().nextInt(glove.size()));
        } else if (type == ItemType.BOW) {
            List<String> bow = new ArrayList<>(List.of("Bow", "Peashooter", "Fling Sling", "...Gun?", "Yeet Cannon"));
            nameSegments[nameSegments.length - 1] = bow.get(ThreadLocalRandom.current().nextInt(bow.size()));
        } else if (type == ItemType.WAND) {
            List<String> wand = new ArrayList<>(List.of("Wand", "Rabbit Maker"));
            nameSegments[nameSegments.length - 1] = wand.get(ThreadLocalRandom.current().nextInt(wand.size()));
        } else if (type == ItemType.STAFF) {
            List<String> staff = new ArrayList<>(List.of("Staff", "Walking Stick"));
            nameSegments[nameSegments.length - 1] = staff.get(ThreadLocalRandom.current().nextInt(staff.size()));
        } else if (type == ItemType.CATALYST) {
            List<String> catalyst = new ArrayList<>(List.of("Catalyst", "Grimoire", "Reading Material", "Textbook"));
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

    public void addASCIIArtToWeapon(ItemStack weapon, ItemType type) {
        ItemMeta meta = weapon.getItemMeta();
        List<String> lore = meta.getLore();

        if (type == ItemType.SWORD) {
            lore.add("§7        />______________");
            lore.add("§7♦#####[]______________>");
            lore.add("§7        \\>");
        } else if (type == ItemType.DAGGER) {
            lore.add("§7    ʃ                      ʃ");
            lore.add("§7♦##|======-  -======|##♦");
            lore.add("§7    ʃ                      ʃ");
        } else if (type == ItemType.AXE) {
            lore.add("§7                           /\\");
            lore.add("§7♦===============######");
            lore.add("§7                       \\_____/");
        } else if (type == ItemType.HAMMER) {
            lore.add("§7             ╔══╗");
            lore.add("§7♦=======♦|███|♦");
            lore.add("§7             ╚══╝");
        } else if (type == ItemType.SPEAR) {
            lore.add("§7                           \\`-._");
            lore.add("§7♦========♦========♦   _>");
            lore.add("§7                           /.-'");
        } else if (type == ItemType.GLOVE) {
            lore.add("§7‾‾‾‾‾‾‾‾‾|♦|‾‾‾‾‾‾‾‾‾");
            lore.add("§7\\_   @_|♦|_@   _/");
            lore.add("§7   \\__)    (__/");
        } else if (type == ItemType.BOW) {
            lore.add("§7                  ◁----<<");
            lore.add("§7  ︷__♦__︷        >>----▷");
            lore.add("§7/ˍˍˍˍˍˍˍˍˍˍˍˍˍˍˍˍˍ\\  ◁----<<");
        } else if (type == ItemType.WAND) {
            lore.add("§7             * ╲ ╱  *");
            lore.add("§7♦========< ⭐ >");
            lore.add("§7          *    ╱ ╲   *");
        } else if (type == ItemType.STAFF) {
            lore.add("§7                *           ╗ * ╲ ╱  ");
            lore.add("§7♦========♦========♦║ < ⭐ > ");
            lore.add("§7      *                 *   ╝   ╱ ╲   *");
        } else if (type == ItemType.CATALYST) {
            lore.add("§7  /‾‾/   \\‾‾\\");
            lore.add("§7 <   |  ♦  |   >");
            lore.add("§7  \\_\\    /_/");
        }

        meta.setLore(lore);
        weapon.setItemMeta(meta);
    }

    public void generateWeaponDamage(ItemStack weapon, ItemType type, ItemRarity rarity, int level) {
        List<ItemStat> possibleFirstDamageTypes = null;
        List<ItemStat> possibleSecondDamageTypes = null;

        switch (type) {
            case SWORD, AXE, HAMMER, SPEAR, GLOVE -> {
                possibleFirstDamageTypes = new ArrayList<>(List.of(ItemStat.PHYSICALDAMAGE));
                possibleSecondDamageTypes = new ArrayList<>(List.of(ItemStat.PHYSICALDAMAGE, ItemStat.FIREDAMAGE, ItemStat.COLDDAMAGE, ItemStat.EARTHDAMAGE,
                                                                    ItemStat.LIGHTNINGDAMAGE, ItemStat.AIRDAMAGE, ItemStat.LIGHTDAMAGE, ItemStat.DARKDAMAGE));
            }
            case DAGGER, BOW -> {
                possibleFirstDamageTypes = new ArrayList<>(List.of(ItemStat.PHYSICALDAMAGE));
                possibleSecondDamageTypes = new ArrayList<>(List.of(ItemStat.PHYSICALDAMAGE, ItemStat.FIREDAMAGE, ItemStat.COLDDAMAGE, ItemStat.EARTHDAMAGE,
                                                                    ItemStat.LIGHTNINGDAMAGE, ItemStat.AIRDAMAGE, ItemStat.LIGHTDAMAGE, ItemStat.DARKDAMAGE,
                                                                    ItemStat.PUREDAMAGE));
            }
            case WAND, STAFF, CATALYST -> {
                possibleFirstDamageTypes = new ArrayList<>(List.of(ItemStat.PHYSICALDAMAGE, ItemStat.FIREDAMAGE, ItemStat.COLDDAMAGE, ItemStat.EARTHDAMAGE,
                                                                   ItemStat.LIGHTNINGDAMAGE, ItemStat.AIRDAMAGE, ItemStat.LIGHTDAMAGE, ItemStat.DARKDAMAGE));
                possibleSecondDamageTypes = new ArrayList<>(List.of(ItemStat.PHYSICALDAMAGE, ItemStat.FIREDAMAGE, ItemStat.COLDDAMAGE, ItemStat.EARTHDAMAGE,
                                                                    ItemStat.LIGHTNINGDAMAGE, ItemStat.AIRDAMAGE, ItemStat.LIGHTDAMAGE, ItemStat.DARKDAMAGE));
            }
        }

        ItemStat firstType = possibleFirstDamageTypes.get(ThreadLocalRandom.current().nextInt(possibleFirstDamageTypes.size()));
        int firstDamage = level * 2;
        ItemStat secondType = possibleSecondDamageTypes.get(ThreadLocalRandom.current().nextInt(possibleSecondDamageTypes.size()));
        int secondDamage = level;

        switch (rarity) {
            case COMMON -> {
                ItemSystem.setStat(weapon, firstType, firstDamage);
            }
            case UNCOMMON, RARE -> {
                if (firstType == secondType) {
                    ItemSystem.setStat(weapon, firstType, firstDamage + secondDamage);
                } else {
                    ItemSystem.setStat(weapon, firstType, firstDamage);
                    ItemSystem.setStat(weapon, secondType, secondDamage);
                }
            }
            case MYTHICAL -> {
                firstDamage = level * 3;

                if (firstType == secondType) {
                    ItemSystem.setStat(weapon, firstType, firstDamage + secondDamage);
                } else {
                    ItemSystem.setStat(weapon, firstType, firstDamage);
                    ItemSystem.setStat(weapon, secondType, secondDamage);
                }
            }
        }

        ItemSystem.updateLoreWithItemStats(weapon);
    }
}
