package io.github.Gabriel.nMLWeapons;

import io.github.Gabriel.damagePlugin.customDamage.CustomDamageEvent;
import io.github.Gabriel.damagePlugin.customDamage.DamageConverter;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class WeaponListener implements Listener {
    private NMLWeapons nmlWeapons;

    public WeaponListener(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
    }

    @EventHandler
    public void onSwingWeapon(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        WeaponEffects weaponEffects = new WeaponEffects(nmlWeapons);

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
           if (weapon.hasItemMeta()) {
                if (ItemSystem.isItemUsable(weapon, player)) {
                    switch (ItemSystem.getItemType(weapon)) {
                        case SWORD -> weaponEffects.swordEffect(weapon, player);
                        case DAGGER -> weaponEffects.daggerEffect(weapon, player);
                        case AXE -> weaponEffects.axeEffect(weapon, player);
                        case HAMMER -> weaponEffects.hammerEffect(weapon, player);
                        case SPEAR -> weaponEffects.spearEffect(weapon, player);
                        case GLOVE -> weaponEffects.gloveEffect(weapon, player, 1);
                        case WAND, STAFF, CATALYST -> weaponEffects.magicalEffect(weapon, player);
                        case BOW, SHIELD, HELMET, CHESTPLATE, LEGGINGS, BOOTS, LIGHT, MEDIUM, HEAVY -> {}
                        case null -> {}
                    }
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (weapon.hasItemMeta()) {
                if (ItemSystem.isItemUsable(weapon, player)) {
                    if (Objects.requireNonNull(ItemSystem.getItemType(weapon)) == ItemType.GLOVE) {
                        weaponEffects.gloveEffect(weapon, player, 0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();
            WeaponEffects weaponEffects = new WeaponEffects(nmlWeapons);

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
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            if (arrow.hasMetadata("custom arrow")) {
                ItemStack bow = (ItemStack) arrow.getMetadata("custom arrow").get(0).value();

                event.setDamage(0);
                arrow.remove();
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent((LivingEntity) event.getEntity(), player, DamageConverter.convertStatMap2DamageTypes(ItemSystem.getAllDamageStats(bow))));
            }
        }
    }

    @EventHandler()
    public void weaponLevelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        boolean usable = ItemSystem.isItemUsable(heldItem, player);

        if (heldItem == null || heldItem.getType() == Material.AIR) { return; }
        if (!heldItem.hasItemMeta()) { return; }
        if (ItemSystem.getItemType(heldItem) == null) { return; }
        if (!usable) {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        ItemSystem.updateUnusableItemName(heldItem, usable);
    }

    @EventHandler
    public void bowShots(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        Float force = event.getForce();

        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        if (ItemSystem.isItemUsable(bow, player)) {
            arrow.setMetadata("custom arrow", new FixedMetadataValue(nmlWeapons, bow));
            arrow.setCritical(false);

            if (force <= 2.0f) { // semi-charged shot
                double boost;
                if (force <= 0.5) {
                    boost = 1.0 + (0.5 * (1 - (force / 0.5)));
                } else {
                    double scale = (2.0 - force) / 1.5;
                    boost = 1.0 + (0.25 * scale);
                }
                arrow.setVelocity(arrow.getVelocity().multiply(boost));
            } else if (force >= 2.6f) { // fully charged shot
                new WeaponEffects(nmlWeapons).bowEffect(arrow, player);
            }

            // custom trail particles
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.isDead() || arrow.isOnGround()) {
                        this.cancel();
                        return;
                    }

                    double speed = arrow.getVelocity().length();
                    int particleCount = (int) (Math.pow(speed, 2) * 5);

                    if (particleCount > 0) {
                        Location loc = arrow.getLocation();
                        player.getWorld().spawnParticle(Particle.CRIT, loc, particleCount,0, 0, 0, 0);
                    }
                }
            }.runTaskTimer(nmlWeapons, 0, 1);
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

    @EventHandler
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
}
