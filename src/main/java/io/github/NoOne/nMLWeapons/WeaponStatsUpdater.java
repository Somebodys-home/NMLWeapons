package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLItems.ItemSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class WeaponStatsUpdater {
    private NMLWeapons nmlWeapons;
    private WeaponStatsManager weaponStatsManager;
    private HashMap<UUID, ItemStack> prevHeldItems;

    public WeaponStatsUpdater(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        weaponStatsManager = nmlWeapons.getWeaponManager();
        prevHeldItems = new HashMap<>();
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateStatsFromWeapon(player);
            }
            }
        }.runTaskTimer(nmlWeapons, 0L, 1L);
    }

    public void updateStatsFromWeapon(Player player) {
        UUID uuid = player.getUniqueId();
        HashMap<String, Double> prevHeldItemStats = ItemSystem.convertItemStatsToPlayerStats(prevHeldItems.get(uuid));
        HashMap<String, Double> newHeldItemStats = ItemSystem.convertItemStatsToPlayerStats(player.getInventory().getItemInMainHand());

        if (!prevHeldItemStats.equals(newHeldItemStats)) {
            weaponStatsManager.removeWeaponStatsFromPlayer(player, prevHeldItems.get(uuid));
            weaponStatsManager.addWeaponStatsToPlayer(player, player.getInventory().getItemInMainHand());
            prevHeldItems.put(uuid, player.getInventory().getItemInMainHand());
        }
    }
}
