package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLItems.ItemRarity;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLItems.itemDictionary.Weapons;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateWeaponCommand implements CommandExecutor, TabCompleter {
    private WeaponStatsManager weaponStatsManager;
    private Weapons weapons;

    public GenerateWeaponCommand(NMLWeapons nmlWeapons) {
        weaponStatsManager = nmlWeapons.getWeaponStatsManager();
        weapons = nmlWeapons.getWeapons();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            int level = Integer.parseInt(args[0]);
            String rarity = args[1];
            String type = args[2];
            ItemStack weapon = weapons.generateWeapon(player, ItemType.getItemTypeFromString(type), ItemRarity.getItemRarityFromString(rarity), level);

            player.getInventory().addItem(weapon);

            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand.isSimilar(weapon)) {
                weaponStatsManager.addWeaponStatsToPlayer(player, mainHand);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(List.of("<level>")).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        else if (args.length == 2) {
            return new ArrayList<>(List.of("common", "uncommon", "rare", "mythical")).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            return new ArrayList<>(List.of("sword", "dagger", "axe", "hammer", "spear", "glove", "bow", "wand", "staff", "catalyst")).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}