package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WeaponStatsManager {
    private ProfileManager profileManager;

    public WeaponStatsManager(NMLPlayerStats nmlPlayerStats) { // have to make sure profileManager can't be null
        profileManager = nmlPlayerStats.getProfileManager();
    }

    public void addWeaponStatsToPlayer(Player player, ItemStack weapon) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

        for (Map.Entry<String, Double> statEntry : ItemSystem.convertItemStatsToPlayerStats(weapon).entrySet()) {
            stats.add2Stat(statEntry.getKey(), statEntry.getValue());
        }
    }

    public void removeWeaponStatsFromPlayer(Player player, ItemStack weapon) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

        for (Map.Entry<String, Double> statEntry : ItemSystem.convertItemStatsToPlayerStats(weapon).entrySet()) {
            stats.removeFromStat(statEntry.getKey(), statEntry.getValue());
        }
    }
}
