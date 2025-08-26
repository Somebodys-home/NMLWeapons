package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class WeaponManager {
    private NMLPlayerStats nmlPlayerStats;

    public WeaponManager(NMLWeapons nmlWeapons) {
        nmlPlayerStats = nmlWeapons.getNmlPlayerStats();
    }

    private HashMap<String, Double> weaponStatsToPlayerStats(ItemStack weapon) {
        HashMap<String, Double> playerStats = new HashMap<>();

        for (Map.Entry<ItemStat, Double> itemStatEntry : ItemSystem.getAllStats(weapon).entrySet()) {
            String stat = "";

            switch (itemStatEntry.getKey()) {
                case PHYSICALDAMAGE -> stat = "physicaldamage";
                case FIREDAMAGE -> stat = "firedamage";
                case COLDDAMAGE -> stat = "colddamage";
                case EARTHDAMAGE -> stat = "earthdamage";
                case LIGHTNINGDAMAGE -> stat = "lightningdamage";
                case AIRDAMAGE -> stat = "airdamage";
                case LIGHTDAMAGE -> stat = "lightdamage";
                case DARKDAMAGE -> stat = "darkdamage";
                case PUREDAMAGE -> stat = "puredamage";
                case CRITCHANCE -> stat = "critchance";
                case CRITDAMAGE -> stat = "critdamage";
            }

            playerStats.put(stat, itemStatEntry.getValue());
        }

        return playerStats;
    }

    public void addWeaponStatsToPlayer(Player player, ItemStack weapon) {
        Stats stats = nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats();
        HashMap<String, Double> stats2Add = weaponStatsToPlayerStats(weapon);

        for (Map.Entry<String, Double> statEntry : stats2Add.entrySet()) {
            stats.add2Stat(statEntry.getKey(), statEntry.getValue());
        }
    }

    public void removeWeaponStatsFromPlayer(Player player, ItemStack weapon) {
        Stats stats = nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats();
        HashMap<String, Double> stats2Add = weaponStatsToPlayerStats(weapon);

        for (Map.Entry<String, Double> statEntry : stats2Add.entrySet()) {
            stats.removeFromStat(statEntry.getKey(), statEntry.getValue());
        }
    }
}
