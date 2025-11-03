package io.github.NoOne.nMLWeapons;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageConverter;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import io.github.NoOne.expertiseStylePlugin.abilitySystem.AbilityItemManager;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
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
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class WeaponListener implements Listener {
    private NMLWeapons nmlWeapons;
    private ProfileManager profileManager;
    private WeaponStatsManager weaponStatsManager;
    private WeaponEffects weaponEffects;

    public WeaponListener(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        profileManager = nmlWeapons.getProfileManager();
        weaponStatsManager = nmlWeapons.getWeaponManager();
        weaponEffects = new WeaponEffects(nmlWeapons);
    }

    @EventHandler
    public void onSwingWeapon(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.hasMetadata("using ability")) return; // metadata block in expertisestyleplugin
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
            if (player.hasMetadata("using ability")) { // metadata block in expertisestyleplugin
                event.setCancelled(true);
                return;
            }
            if (ItemSystem.isItemUsable(weapon, player) && ItemSystem.getItemType(player.getInventory().getItemInOffHand()) == ItemType.GLOVE) {
                if (ItemSystem.getItemType(weapon) == ItemType.GLOVE) {
                    weaponEffects.gloveEffect(weapon, player, 0);
                }
            }
        }
    }

    @EventHandler
    public void onWeaponHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (player.hasMetadata("using ability")) return; // metadata block in expertisestyleplugin

            ItemStack weapon = player.getInventory().getItemInMainHand();

            if (ItemSystem.getItemType(weapon) != null && ItemSystem.isItemUsable(weapon, player)) {
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

        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player && arrow.hasMetadata("custom arrow")) {
            MetadataValue meta = arrow.getMetadata("custom arrow").get(0);
            HashMap<DamageType, Double> damageMap = (HashMap<DamageType, Double>) meta.value();

            event.setDamage(0);
            arrow.remove();
            Bukkit.getPluginManager().callEvent(new CustomDamageEvent((LivingEntity) event.getEntity(), player, damageMap));
        }
    }

    @EventHandler
    public void bowShots(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;
        if (ItemSystem.isItemUsable(event.getBow(), player)) {
            if (ItemSystem.getItemType(player.getInventory().getItemInOffHand()) == ItemType.QUIVER) {
                HashMap<DamageType, Double> damageMap = DamageConverter.convertPlayerStats2Damage(profileManager.getPlayerProfile(player.getUniqueId()).getStats());

                arrow.setMetadata("custom arrow", new FixedMetadataValue(nmlWeapons, damageMap));
                arrow.setCritical(false);
                weaponEffects.bowEffect(player, arrow, event.getForce());
            } else {
                player.sendMessage("§c⚠ §nBows require a quiver in your offhand to use!§r§c ⚠");
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void dontPlaceWeaponsInOffhand(InventoryClickEvent event) {
        if (ItemSystem.isWeapon(event.getCursor()) && event.getSlot() == 40) {
            if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_SOME ||
                event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void dontSwapWeaponsToOffhand(InventoryClickEvent event) {
        if ((event.getClick() == ClickType.SWAP_OFFHAND) && ItemSystem.isWeapon(event.getCurrentItem())) {
            event.setCancelled(true);
            weaponStatsManager.removeWeaponStatsFromPlayer((Player) event.getWhoClicked(), event.getCurrentItem());
        }
    }

    @EventHandler
    public void updateWeaponStatsOnHold(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

        if (!AbilityItemManager.isAnAbility(newItem)) { // for ability items
            if (ItemSystem.isWeapon(newItem)) {
                weaponStatsManager.addWeaponStatsToPlayer(player, newItem);
            }
            if (ItemSystem.isWeapon(oldItem)) {
                weaponStatsManager.removeWeaponStatsFromPlayer(player, oldItem);
            }
        }
    }

    @EventHandler
    public void updateWeaponStatsOnInventoryMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        int heldItemSlot = player.getInventory().getHeldItemSlot();
        ItemStack triggeringItem = event.getCurrentItem();
        ItemStack previouslyHeldItem = player.getInventory().getItem(heldItemSlot);

        // clicking weapons manually
        switch (event.getClick()) {
            case SHIFT_LEFT, SHIFT_RIGHT, NUMBER_KEY, CREATIVE -> {
                if (ItemSystem.isWeapon(triggeringItem)) {
                    if (clickedSlot == heldItemSlot) {
                        weaponStatsManager.removeWeaponStatsFromPlayer(player, triggeringItem);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemStack currentlyHeldItem = player.getInventory().getItem(heldItemSlot);

                            if (triggeringItem.isSimilar(currentlyHeldItem)) {
                                weaponStatsManager.addWeaponStatsToPlayer(player, currentlyHeldItem);
                            }

                            if (previouslyHeldItem != null && !previouslyHeldItem.isSimilar(currentlyHeldItem)) {
                                weaponStatsManager.removeWeaponStatsFromPlayer(player, previouslyHeldItem);
                            }
                        }
                    }.runTaskLater(nmlWeapons, 1L);
                }
            }
        }

        // moving weapons manually normally
        if (clickedSlot == heldItemSlot) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack cursorItem = event.getCursor();
                    ItemStack newMainHandItem = player.getInventory().getItemInMainHand();

                    if (ItemSystem.isWeapon(newMainHandItem)) {
                        weaponStatsManager.addWeaponStatsToPlayer(player, newMainHandItem);
                    }
                    if (ItemSystem.isWeapon(cursorItem)) {
                        weaponStatsManager.removeWeaponStatsFromPlayer(player, cursorItem);
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
            weaponStatsManager.removeWeaponStatsFromPlayer(player, droppedItem);
        }
    }

    @EventHandler
    public void updateWeaponStatsOnPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack pickedUpItem = event.getItem().getItemStack();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack oldHand = playerInventory.getItemInMainHand();

        // where did that item go?
        int slot = -1;
        for (int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (stack != null && stack.isSimilar(pickedUpItem) && stack.getAmount() < stack.getMaxStackSize()) {
                slot = i;
            }
        }

        if (slot == -1) {
            slot = playerInventory.firstEmpty();
        }

        if (slot == playerInventory.getHeldItemSlot()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack newHand = playerInventory.getItemInMainHand();

                    if (ItemSystem.isWeapon(newHand)) {
                        weaponStatsManager.addWeaponStatsToPlayer(player, newHand);
                    }
                    if (ItemSystem.isWeapon(oldHand)) {
                        weaponStatsManager.removeWeaponStatsFromPlayer(player, oldHand);
                    }
                }
            }.runTaskLater(nmlWeapons, 1L);
        }
    }

    @EventHandler
    public void dontThrowSpears(PlayerInteractEvent event) {
        if (ItemSystem.getItemType(event.getItem()) == ItemType.SPEAR && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void holdBothGloves(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItem(event.getNewSlot());
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (ItemSystem.getItemType(mainHand) == ItemType.GLOVE) { // if main hand item IS a glove
            if (offhand.getType() == Material.AIR) { // and there's nothing in the offhand
                player.getInventory().setItemInOffHand(mainHand); // put glove in offhand
            }
        } else { // if main hand item ISN'T a glove
            if (ItemSystem.getItemType(offhand) == ItemType.GLOVE && !AbilityItemManager.isAnAbility(mainHand)) { // and if offhand item is a glove and youre not swapping
                                                                                                                   // to an ability item
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR)); // remove it
            }
        }

        if (mainHand == null) { // if mainhand is null
            if (ItemSystem.getItemType(offhand) == ItemType.GLOVE) { // and if offhand item is a glove
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR)); // remove it
            }
        }
    }

    @EventHandler
    public void pickupBothGloves(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack pickedUpItem = event.getItem().getItemStack();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack offhand = playerInventory.getItemInOffHand();

        // where did that item go?
        int slot = -1;
        for (int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (stack != null && stack.isSimilar(pickedUpItem) && stack.getAmount() < stack.getMaxStackSize()) {
                slot = i;
            }
        }

        if (slot == -1) {
            slot = playerInventory.firstEmpty();
        }


        if (slot == playerInventory.getHeldItemSlot() && ItemSystem.getItemType(pickedUpItem) == ItemType.GLOVE && offhand.getType() == Material.AIR) {
            Bukkit.getScheduler().runTaskLater(nmlWeapons, () -> {
                player.getInventory().setItemInOffHand(pickedUpItem);
            }, 1L);
        }
    }

    @EventHandler
    public void dropBothGloves(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (ItemSystem.getItemType(droppedItem) == ItemType.GLOVE) {
            ItemStack offhandItem = event.getPlayer().getInventory().getItem(40);

            if (ItemSystem.getItemType(offhandItem) == ItemType.GLOVE) {
                event.getPlayer().getInventory().setItem(40, new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void dontMoveOffhandGlove(InventoryClickEvent event) {
        ItemStack cursorItem = event.getCurrentItem();

        if (ItemSystem.getItemType(cursorItem) == ItemType.GLOVE) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                if (event.getSlot() == 40) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void dontDropOffhandGlove(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (ItemSystem.getItemType(clickedItem) == ItemType.GLOVE) {
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