package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GlovesTracker {
    private NMLWeapons nmlWeapons;
    private BukkitTask glovesTracker;

    public GlovesTracker(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
    }

    public void startTracker() {
        glovesTracker = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack mainHand = playerInventory.getItemInMainHand();
                    ItemStack offHand = playerInventory.getItemInOffHand();

                    if (ItemSystem.isItemType(mainHand, ItemType.GLOVE)) { // when the player holds a glove
                        if (offHand.getType().isAir() || (ItemSystem.isItemType(offHand, ItemType.GLOVE) && !offHand.isSimilar(mainHand))) {
                            // if they've got an empty offhand or a different glove in their offhand

                            playerInventory.setItemInOffHand(mainHand); // put the correct glove in their offhand
                        }
                    } else { // when they aren't holding a glove
                        if (ItemSystem.isItemType(offHand, ItemType.GLOVE)) { // if their offhand is a glove
                            playerInventory.setItemInOffHand(null); // remove it
                        }
                    }
                }
            }
        }.runTaskTimer(nmlWeapons, 0, 1);
    }

    public void stopTracker() {
        glovesTracker.cancel();
    }
}
