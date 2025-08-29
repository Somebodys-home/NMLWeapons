package io.github.Gabriel.nMLWeapons;

import io.github.Gabriel.damagePlugin.customDamage.CustomDamageEvent;
import io.github.Gabriel.damagePlugin.customDamage.DamageConverter;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Objects;

public class WeaponListener implements Listener {
    private NMLWeapons nmlWeapons;
    private NMLPlayerStats nmlPlayerStats;
    private WeaponManager weaponManager;
    private WeaponEffects weaponEffects;

    public WeaponListener(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        nmlPlayerStats = NMLWeapons.getNmlPlayerStats();
        weaponManager = new WeaponManager(nmlWeapons);
        weaponEffects = new WeaponEffects(nmlWeapons);
    }

    @EventHandler
    public void onSwingWeapon(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (ItemSystem.isItemUsable(weapon, player)) {
                switch (ItemSystem.getItemType(weapon)) {
                    case SWORD -> weaponEffects.swordEffect(weapon, player);
                    case DAGGER -> weaponEffects.daggerEffect(weapon, player);
                    case AXE -> weaponEffects.axeEffect(weapon, player);
                    case HAMMER -> weaponEffects.hammerEffect(weapon, player);
                    case SPEAR -> weaponEffects.spearEffect(weapon, player);
                    case GLOVE -> weaponEffects.gloveEffect(weapon, player, 1);
                    case WAND, STAFF, CATALYST -> weaponEffects.magicalEffect(weapon, player);
                    case BOW, SHIELD, QUIVER, HELMET, CHESTPLATE, LEGGINGS, BOOTS, LIGHT, MEDIUM, HEAVY -> {}
                    case null -> {}
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (ItemSystem.isItemUsable(weapon, player)) {
                if (ItemSystem.getItemType(weapon) == ItemType.GLOVE) {
                    weaponEffects.gloveEffect(weapon, player, 0);
                }
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();

            if (weapon.hasItemMeta() && ItemSystem.getItemType(weapon) != null) {
                if (ItemSystem.isItemUsable(weapon, player)) {
                    switch (ItemSystem.getItemType(weapon)) {
                        case SWORD -> weaponEffects.swordEffect(weapon, player);
                        case DAGGER -> weaponEffects.daggerEffect(weapon, player);
                        case AXE -> weaponEffects.axeEffect(weapon, player);
                        case HAMMER -> weaponEffects.hammerEffect(weapon, player);
                        case SPEAR -> weaponEffects.spearEffect(weapon, player);
                        case GLOVE -> weaponEffects.gloveEffect(weapon, player, 1);
                        case WAND, STAFF, CATALYST -> weaponEffects.magicalEffect(weapon, player);
                    }
                }
            }
        }

        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player && arrow.hasMetadata("custom arrow")) {
            event.setDamage(0);
            arrow.remove();
            Bukkit.getPluginManager().callEvent(new CustomDamageEvent((LivingEntity) event.getEntity(), player,
                    DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats())));
        }
    }

    @EventHandler
    public void bowShots(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        ItemStack bow = event.getBow();

        if (ItemSystem.isItemUsable(bow, player)) {
            if (ItemSystem.getItemType(player.getInventory().getItemInOffHand()) == ItemType.QUIVER) {
                arrow.setMetadata("custom arrow", new FixedMetadataValue(nmlWeapons, bow));
                arrow.setCritical(false);
                weaponEffects.bowEffect(arrow, player, event.getForce());
            } else {
                player.sendMessage("§c⚠ §nBows require a quiver in your offhand to use!§r§c ⚠");
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void dontThrowSpears(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();

            if (item == null) return;
            if (ItemSystem.getItemType(item) == ItemType.SPEAR) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void holdBothGloves(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItem(event.getNewSlot());
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (mainHand != null) {
            // if mainhand item has metadata and IS a glove
            if (mainHand.hasItemMeta() && ItemSystem.getItemType(mainHand) == ItemType.GLOVE) {
                if (offhand.getType() == Material.AIR) { // and there's nothing in the offhand
                    player.getInventory().setItemInOffHand(mainHand); // put glove in offhand
                }
            } else {
                // if mainhand item has metadata but ISNT a glove
                if (mainHand.hasItemMeta() && ItemSystem.getItemType(mainHand) != ItemType.GLOVE) {
                    if (offhand.getItemMeta() != null && ItemSystem.getItemType(offhand) == ItemType.GLOVE) { // and if offhand item is a glove
                        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR)); // remove it
                    }
                }
            }
        } else { // if mainhand IS null
            if (offhand.getItemMeta() != null && ItemSystem.getItemType(offhand) == ItemType.GLOVE) { // and if offhand item is a glove
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR)); // remove it
            }
        }
    }

    @EventHandler
    public void pickupBothGloves(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack pickedUpItem = event.getItem().getItemStack();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (pickedUpItem.hasItemMeta() && ItemSystem.getItemType(pickedUpItem) == ItemType.GLOVE && offhand.getType() == Material.AIR) {
            Bukkit.getScheduler().runTaskLater(nmlWeapons, () -> {
                int slot = player.getInventory().first(pickedUpItem.getType());
                ItemStack glove = player.getInventory().getItem(slot);

                if (glove != null && glove.isSimilar(pickedUpItem)) {
                    player.getInventory().setItemInOffHand(glove);
                }
            }, 1L);
        }
    }

    @EventHandler // todo: fix this
    public void dontMoveOffhandGlove(InventoryClickEvent event) {
        ItemStack cursorItem = event.getCurrentItem();

        if (event.getCursor() != null && cursorItem != null && cursorItem.hasItemMeta()) {
            if (ItemSystem.getItemType(cursorItem) == ItemType.GLOVE) {
                if (event.getAction() == InventoryAction.PICKUP_ALL) {
                    if (event.getSlot() == 40) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void dontPlaceGloveInOffhand(InventoryClickEvent event) {
        ItemStack cursorItem = event.getCursor();

        if (cursorItem != null && cursorItem.getType() != Material.AIR && cursorItem.hasItemMeta()) {
            if (ItemSystem.getItemType(cursorItem) == ItemType.GLOVE) {
                if (event.getSlot() == 40) {
                    if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE ||
                        event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {

                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void dropBothGloves(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (droppedItem.hasItemMeta() && ItemSystem.getItemType(droppedItem) == ItemType.GLOVE) {
            ItemStack offhandItem = event.getPlayer().getInventory().getItem(40);

            if (offhandItem != null && offhandItem.hasItemMeta() && ItemSystem.getItemType(offhandItem) == ItemType.GLOVE) {
                event.getPlayer().getInventory().setItem(40, new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void dontDropOffhandGlove(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null && clickedItem.hasItemMeta() && ItemSystem.getItemType(clickedItem) == ItemType.GLOVE) {
            if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void dontSwapHandsWithGlove(PlayerSwapHandItemsEvent event) {
        if ((event.getMainHandItem().hasItemMeta() && ItemSystem.getItemType(event.getMainHandItem()) == ItemType.GLOVE) ||
            (event.getOffHandItem().hasItemMeta() && ItemSystem.getItemType(event.getOffHandItem()) == ItemType.GLOVE)) {

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void dontSwapGloveToOffhand(InventoryClickEvent event) {
        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            ItemStack targetItem = event.getCurrentItem();
            if (targetItem != null && !targetItem.getType().isAir() && targetItem.hasItemMeta()) {
                if (ItemSystem.getItemType(targetItem) == ItemType.GLOVE) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void dontPlaceGloveAsBlock(BlockPlaceEvent event) {
        ItemStack maybeGlove = event.getItemInHand();

        if (maybeGlove.hasItemMeta() && ItemSystem.getItemType(maybeGlove) == ItemType.GLOVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void updateGlovesFromInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        ItemType currentType = ItemSystem.getItemType(event.getCurrentItem());
        ItemType cursorType = ItemSystem.getItemType(event.getCursor());
        ItemType offhandType = ItemSystem.getItemType(playerInventory.getItemInOffHand());

        if (currentType == ItemType.GLOVE && offhandType == ItemType.GLOVE) {
            playerInventory.setItemInOffHand(null);
            return;
        }

        if (cursorType == ItemType.GLOVE && event.getSlot() == playerInventory.getHeldItemSlot() && playerInventory.getItemInOffHand().getType() == Material.AIR) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerInventory.setItemInOffHand(playerInventory.getItemInMainHand());
                }
            }.runTaskLater(nmlWeapons, 1L);
        }
    }

    @EventHandler
    public void updateWeaponStatsOnHold(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

        if (ItemSystem.isWeapon(newItem)) {
            weaponManager.addWeaponStatsToPlayer(player, newItem);
        }
        if (ItemSystem.isWeapon(oldItem)) {
            weaponManager.removeWeaponStatsFromPlayer(player, oldItem);
        }
    }

    @EventHandler
    public void updateWeaponStatsOnClickSwitch(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == player.getInventory().getHeldItemSlot()) {
            ItemStack oldItem = player.getInventory().getItemInMainHand();

            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack newItem = player.getInventory().getItemInMainHand();

                    if (ItemSystem.isWeapon(newItem)) {
                        weaponManager.addWeaponStatsToPlayer(player, newItem);
                    }
                    if (ItemSystem.isWeapon(oldItem)) {
                        weaponManager.removeWeaponStatsFromPlayer(player, oldItem);
                    }
                }
            }.runTaskLater(nmlWeapons, 1L);
        }
    }

    @EventHandler
    public void updateWeaponStatsWhenDropped(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (ItemSystem.isWeapon(droppedItem)) {
            weaponManager.removeWeaponStatsFromPlayer(player, droppedItem);
        }
    }

    @EventHandler
    public void updateWeaponStatsOnPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack pickedUpItem = event.getItem().getItemStack();
        PlayerInventory inv = player.getInventory();
        ItemStack oldHand = inv.getItemInMainHand();

        // where did that item go?
        int slot = -1;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && stack.isSimilar(pickedUpItem) && stack.getAmount() < stack.getMaxStackSize()) {
                slot = i;
            }
        }

        if (slot == -1) {
            slot = inv.firstEmpty();
        }

        if (slot == inv.getHeldItemSlot()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack newHand = inv.getItemInMainHand();

                    if (ItemSystem.isWeapon(newHand)) {
                        weaponManager.addWeaponStatsToPlayer(player, newHand);
                    }
                    if (ItemSystem.isWeapon(oldHand)) {
                        weaponManager.removeWeaponStatsFromPlayer(player, oldHand);
                    }
                }
            }.runTaskLater(nmlWeapons, 1L);
        }
    }
}