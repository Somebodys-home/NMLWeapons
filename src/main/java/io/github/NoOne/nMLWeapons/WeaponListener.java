package io.github.NoOne.nMLWeapons;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageHelper;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLAbilities.abilitySystem.AbilityItemManager;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class WeaponListener implements Listener {
    private NMLWeapons nmlWeapons;
    private ProfileManager profileManager;
    private WeaponEffects weaponEffects;
    private ItemSystem itemSystem;

    public WeaponListener(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        profileManager = nmlWeapons.getProfileManager();
        weaponEffects = new WeaponEffects(nmlWeapons);
        itemSystem = nmlWeapons.getItemSystem();
    }

    @EventHandler
    public void onSwingWeapon(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (AttackCooldownSystem.isOnAttackCooldown(player)) return;
            if (itemSystem.isItemUsable(weapon, player)) {
                ItemType type = itemSystem.getItemType(weapon);

                if (type == ItemType.SWORD) {
                    weaponEffects.swordEffect(player);
                } else if (type == ItemType.DAGGER) {
                    weaponEffects.daggerEffect(player);
                } else if (type == ItemType.AXE) {
                    weaponEffects.axeEffect(player);
                } else if (type == ItemType.HAMMER) {
                    weaponEffects.hammerEffect(player);
                } else if (type == ItemType.SPEAR) {
                    weaponEffects.spearEffect(player);
                } else if (type == ItemType.GLOVE) {
                    weaponEffects.gloveEffect(player, 1);
                } else if (type == ItemType.WAND || type == ItemType.STAFF || type == ItemType.CATALYST) {
                    weaponEffects.magicalEffect(player);
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (AttackCooldownSystem.isOnAttackCooldown(player)) {
                return;
            }

            if (itemSystem.isItemUsable(weapon, player) && itemSystem.getItemType(player.getInventory().getItemInOffHand()) == ItemType.GLOVE) {
                if (itemSystem.getItemType(weapon) == ItemType.GLOVE) {
                    weaponEffects.gloveEffect(player, 0);
                }
            }
        }
    }

    @EventHandler
    public void onWeaponHit(PrePlayerAttackEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (AttackCooldownSystem.isOnAttackCooldown(player)) return;
        if (event.getAttacked() instanceof LivingEntity livingEntity && DamageHelper.isMobDamageable(livingEntity)) {
            // punching
            if ((weapon.getType() == Material.AIR || !itemSystem.hasDamageStats(weapon))) {
                HashMap<DamageType, Double> fist = new HashMap<>(){{
                    put(DamageType.PHYSICAL, 1.0);
                }};

                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, fist, false));
                AttackCooldownSystem.setAttackCooldown(player, .5);
                return;
            }

            if (itemSystem.getItemType(weapon) != null && itemSystem.isItemUsable(weapon, player)) {
                switch (itemSystem.getItemType(weapon)) {
                    case SWORD -> weaponEffects.swordEffect(player);
                    case DAGGER -> weaponEffects.daggerEffect(player);
                    case AXE -> weaponEffects.axeEffect(player);
                    case HAMMER -> weaponEffects.hammerEffect(player);
                    case SPEAR -> weaponEffects.spearEffect(player);
                    case GLOVE -> weaponEffects.gloveEffect(player, 1);
                    case WAND, STAFF, CATALYST -> weaponEffects.magicalEffect(player);
                }
            }
        }
    }

    @EventHandler
    public void noFistDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();

        if ((weapon.getType() == Material.AIR || !itemSystem.hasDamageStats(weapon)) && AttackCooldownSystem.isOnAttackCooldown(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void customArrowDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player && arrow.hasMetadata("custom_arrow")) {
            HashMap<DamageType, Double> damageMap = (HashMap<DamageType, Double>) arrow.getMetadata("custom_arrow").get(0).value();
            int noDamageTicks = 20;

            event.setCancelled(true);

            if (event.getEntity() instanceof LivingEntity livingEntity) {
                if (arrow.hasMetadata("no_damage_ticks")) {
                    noDamageTicks = (int) arrow.getMetadata("no_damage_ticks").get(0).value();
                }

                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, damageMap, noDamageTicks));
                arrow.remove();
            }
        }
    }

    @EventHandler
    public void bowShots(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;
        if (itemSystem.isItemUsable(event.getBow(), player)) {
            if (itemSystem.getItemType(player.getInventory().getItemInOffHand()) == ItemType.QUIVER) {
                HashMap<DamageType, Double> damageMap = DamageHelper.convertPlayerStats2Damage(profileManager.getPlayerProfile(player.getUniqueId()).getStats());

                arrow.setMetadata("custom_arrow", new FixedMetadataValue(nmlWeapons, damageMap));
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
    public void dontLowerSpears(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (itemSystem.getItemType(event.getItem()) == ItemType.SPEAR && (event.getAction() == Action.RIGHT_CLICK_AIR ||
            event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            event.setCancelled(true);

            if (itemSystem.isItemType(player.getInventory().getItemInOffHand(), ItemType.SHIELD)) {
                player.swingOffHand();
                player.startUsingItem(EquipmentSlot.OFF_HAND);
            }
        }
    }
}