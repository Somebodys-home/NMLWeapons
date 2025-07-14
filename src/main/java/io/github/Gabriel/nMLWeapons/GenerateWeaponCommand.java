package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
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
    private WeaponSystem weaponSystem;
    private ProfileManager profileManager;

    public GenerateWeaponCommand(NMLWeapons nmlWeapons) {
        weaponSystem = nmlWeapons.getWeaponSystem();
        profileManager = nmlWeapons.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            String type = args[0];
            String rarity = args[1];
            ItemStack weapon = weaponSystem.generateWeapon(WeaponType.getWeaponTypeFromString(type),
                    WeaponRarity.getWeaponRarityFromString(rarity),
                    profileManager.getPlayerProfile(player.getUniqueId()).getStats().getLevel());

            player.getInventory().setItemInMainHand(weapon);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(List.of("sword", "dagger", "axe", "hammer", "spear", "glove", "bow", "wand", "staff", "catalyst")).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return new ArrayList<>(List.of("common", "uncommon", "rare", "mythical")).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
