package io.github.Gabriel.nMLWeapons.weaponEffects;

import io.github.Gabriel.nMLWeapons.NMLWeapons;
import io.github.Gabriel.nMLWeapons.WeaponSystem;
import io.github.Gabriel.nMLWeapons.WeaponType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class WeaponListener implements Listener {
    private NMLWeapons nmlWeapons;
    private WeaponSystem weaponSystem;

    public WeaponListener(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        weaponSystem = nmlWeapons.getWeaponSystem();
    }

    @EventHandler
    public void onSwingWeapon(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        WeaponEffects weaponEffects = new WeaponEffects(nmlWeapons);

        if (weapon.getItemMeta() != null) {
            switch (weaponSystem.getWeaponType(weapon)) {
                case SWORD -> weaponEffects.swordEffect(weapon, player);
                case DAGGER -> weaponEffects.daggerEffect(weapon, player);
                case AXE -> weaponEffects.axeEffect(weapon, player);
                case HAMMER -> weaponEffects.hammerEffect(weapon, player);
                case SPEAR -> weaponEffects.spearEffect(weapon, player);
                case GLOVE -> weaponEffects.gloveEffect(weapon, player);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();
            WeaponEffects weaponEffects = new WeaponEffects(nmlWeapons);

            if (weapon.getItemMeta() != null && new WeaponSystem(nmlWeapons).getWeaponType(weapon) != null) {
                switch (weaponSystem.getWeaponType(weapon)) {
                    case SWORD -> weaponEffects.swordEffect(weapon, player);
                    case DAGGER -> weaponEffects.daggerEffect(weapon, player);
                    case AXE -> weaponEffects.axeEffect(weapon, player);
                    case HAMMER -> weaponEffects.hammerEffect(weapon, player);
                    case SPEAR -> weaponEffects.spearEffect(weapon, player);
                    case GLOVE -> weaponEffects.gloveEffect(weapon, player);
                }
            }
        }
    }

    @EventHandler
    public void onHoldGlove(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItem(event.getNewSlot());
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (mainHand != null) {
            // if mainhand item has metadata and IS a glove
            if (mainHand.hasItemMeta() && new WeaponSystem(nmlWeapons).getWeaponType(mainHand) == WeaponType.GLOVE) {
                if (offhand.getType() == Material.AIR) { // and theres nothing in the offhand
                    player.getInventory().setItemInOffHand(mainHand); // put a glove in the offhand
                }
            } else {
                // if mainhand item has metadata but ISNT a glove
                if (mainHand.hasItemMeta() && new WeaponSystem(nmlWeapons).getWeaponType(mainHand) != WeaponType.GLOVE) {
                    if (offhand.getItemMeta() != null && new WeaponSystem(nmlWeapons).getWeaponType(offhand) == WeaponType.GLOVE) { // and if offhand item is a glove
                        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR)); // remove it
                    }
                }
            }
        } else { // if mainhand IS null
            if (offhand.getItemMeta() != null && new WeaponSystem(nmlWeapons).getWeaponType(offhand) == WeaponType.GLOVE) { // and if offhand item is a glove
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR)); // remove it
            }
        }
    }

    @EventHandler
    public void dontTakeGlovesOffOfOffhand(InventoryClickEvent event) {
        ItemStack cursorItem = event.getCurrentItem();

        if (event.getCursor() != null && cursorItem != null && cursorItem.hasItemMeta()) {
            if (new WeaponSystem(nmlWeapons).getWeaponType(cursorItem) == WeaponType.GLOVE) {
                if (event.getAction() == InventoryAction.PICKUP_ALL) {
                    if (event.getSlot() == 40) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void dontPlaceGlovesInOffhand(InventoryClickEvent event) {
        ItemStack cursorItem = event.getCursor();

        if (cursorItem != null && cursorItem.getType() != Material.AIR && cursorItem.hasItemMeta()) {
            if (new WeaponSystem(nmlWeapons).getWeaponType(cursorItem) == WeaponType.GLOVE) {
                if (event.getSlot() == 40) {
                    if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
