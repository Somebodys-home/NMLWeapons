package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class WeaponManager {
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
                case RADIANTDAMAGE -> stat = "radiantdamage";
                case NECROTICDAMAGE -> stat = "necroticdamage";
                case PUREDAMAGE -> stat = "puredamage";
                case CRITCHANCE -> stat = "critchance";
                case CRITDAMAGE -> stat = "critdamage";
            }

            playerStats.put(stat, itemStatEntry.getValue());
        }

        return playerStats;
    }

    public void addWeaponStatsToPlayer(Player player, ItemStack weapon) {
        HashMap<String, Double> stats2Add = weaponStatsToPlayerStats(weapon);

        for (Map.Entry<String, Double> statEntry : stats2Add.entrySet()) {
            Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, statEntry.getKey(), statEntry.getValue()));
        }
    }

    public void removeWeaponStatsFromPlayer(Player player, ItemStack weapon) {
        HashMap<String, Double> stats2Add = weaponStatsToPlayerStats(weapon);

        for (Map.Entry<String, Double> statEntry : stats2Add.entrySet()) {
            Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, statEntry.getKey(), -statEntry.getValue()));
        }
    }
}
