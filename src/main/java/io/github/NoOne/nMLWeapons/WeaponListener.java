package io.github.NoOne.nMLWeapons;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageHelper;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
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
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

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

        if (!itemSystem.isItemUsable(weapon, player) || AttackCooldownSystem.isOnAttackCooldown(player) || player.hasMetadata("glove_effect")) return;
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            switch (itemSystem.getItemType(weapon)) {
                case SWORD -> weaponEffects.swordEffect(player);
                case DAGGER -> weaponEffects.daggerEffect(player);
                case AXE -> weaponEffects.axeEffect(player);
                case HAMMER -> weaponEffects.hammerEffect(player);
                case SPEAR -> weaponEffects.spearEffect(player);
                case GLOVE -> weaponEffects.gloveEffect(player, 1);
                case WAND, STAFF, CATALYST -> weaponEffects.magicalEffect(player);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (itemSystem.getItemType(weapon) == ItemType.GLOVE && itemSystem.getItemType(player.getInventory().getItemInOffHand()) == ItemType.GLOVE) {
                weaponEffects.gloveEffect(player, 0);
            }
        }
    }

    @EventHandler
    public void onWeaponHit(PrePlayerAttackEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (AttackCooldownSystem.isOnAttackCooldown(player)) {
            event.setCancelled(true);
            return;
        }
        if (event.getAttacked() instanceof LivingEntity livingEntity && DamageHelper.isMobDamageable(livingEntity)) {
            if (!itemSystem.hasDamageStats(weapon)) { // fraud weapon check
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
    public void noRegularDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();

            if ((itemSystem.getItemType(weapon) == null || !itemSystem.hasDamageStats(weapon)) && AttackCooldownSystem.isOnAttackCooldown(player)) {
                event.setCancelled(true);
            }
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

        if (event.getHand() != EquipmentSlot.HAND) return;

        if (itemSystem.getItemType(event.getItem()) == ItemType.SPEAR && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);

            // using shields with spears
            if (itemSystem.isItemType(player.getInventory().getItemInOffHand(), ItemType.SHIELD)) {
               new BukkitRunnable() {
                   @Override
                   public void run() {
                       player.startUsingItem(EquipmentSlot.OFF_HAND);
                   }
               }.runTaskLater(nmlWeapons, 1);
            }
        }
    }

    @EventHandler
    public void dontPlaceGloves(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (itemSystem.isItemType(item, ItemType.GLOVE)) {
            event.setCancelled(true);
        }
    }
}