package io.github.NoOne.nMLWeapons;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageHelper;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class WeaponListener implements Listener {
    private NMLWeapons nmlWeapons;
    private ProfileManager profileManager;
    private WeaponEffects weaponEffects;

    public WeaponListener(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        profileManager = nmlWeapons.getProfileManager();
        weaponEffects = new WeaponEffects(nmlWeapons);
    }

    @EventHandler
    public void onSwingWeapon(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (!ItemSystem.isItemUsable(weapon, player) || AttackCooldownSystem.isOnAttackCooldown(player) || player.hasMetadata("glove_effect")) return;
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (ItemSystem.isItemType(weapon, ItemType.SWORD)) {
                weaponEffects.swordEffect(player);
            } else if (ItemSystem.isItemType(weapon, ItemType.DAGGER)) {
                weaponEffects.daggerEffect(player);
            } else if (ItemSystem.isItemType(weapon, ItemType.AXE)) {
                weaponEffects.axeEffect(player);
            } else if (ItemSystem.isItemType(weapon, ItemType.HAMMER)) {
                weaponEffects.hammerEffect(player);
            } else if (ItemSystem.isItemType(weapon, ItemType.SPEAR)) {
                weaponEffects.spearEffect(player);
            } else if (ItemSystem.isItemType(weapon, ItemType.GLOVE)) {
                weaponEffects.gloveEffect(player, 1);
            } else if (ItemSystem.isItemType(weapon, ItemType.WAND) || ItemSystem.isItemType(weapon, ItemType.STAFF) || ItemSystem.isItemType(weapon, ItemType.CATALYST)) {
                weaponEffects.magicalEffect(player);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (ItemSystem.isItemType(weapon, ItemType.GLOVE) && ItemSystem.isItemType(player.getInventory().getItemInOffHand(), ItemType.GLOVE)) {
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
            if (!ItemSystem.hasDamageStats(weapon)) { // no/fraud weapon check
                HashMap<DamageType, Double> fist = new HashMap<>(){{
                    put(DamageType.PHYSICAL, 1.0);
                }};

                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, fist, false));
                AttackCooldownSystem.setAttackCooldown(player, .5);
                return;
            }

            if (ItemSystem.isItemUsable(weapon, player)) {
                if (ItemSystem.isItemType(weapon, ItemType.SWORD)) {
                    weaponEffects.swordEffect(player);
                } else if (ItemSystem.isItemType(weapon, ItemType.DAGGER)) {
                    weaponEffects.daggerEffect(player);
                } else if (ItemSystem.isItemType(weapon, ItemType.AXE)) {
                    weaponEffects.axeEffect(player);
                } else if (ItemSystem.isItemType(weapon, ItemType.HAMMER)) {
                    weaponEffects.hammerEffect(player);
                } else if (ItemSystem.isItemType(weapon, ItemType.SPEAR)) {
                    weaponEffects.spearEffect(player);
                } else if (ItemSystem.isItemType(weapon, ItemType.GLOVE)) {
                    weaponEffects.gloveEffect(player, 1);
                } else if (ItemSystem.isItemType(weapon, ItemType.WAND) || ItemSystem.isItemType(weapon, ItemType.STAFF) || ItemSystem.isItemType(weapon, ItemType.CATALYST)) {
                    weaponEffects.magicalEffect(player);
                }
            }
        }
    }

    @EventHandler
    public void noRegularDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();

            if ((ItemSystem.getAllItemTypes(weapon).isEmpty() || !ItemSystem.hasDamageStats(weapon)) && AttackCooldownSystem.isOnAttackCooldown(player)) {
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
        if (!(event.getEntity() instanceof Player player) || !(event.getProjectile() instanceof Arrow arrow)) return;
        if (ItemSystem.isItemUsable(event.getBow(), player)) {
            if (ItemSystem.isItemType(player.getInventory().getItemInOffHand(), ItemType.QUIVER)) {
                HashMap<DamageType, Double> damageMap = DamageHelper.convertPlayerStats2Damage(profileManager.getPlayerProfile(player.getUniqueId()).getStats());

                ArrowTracker.makeCustomArrow(arrow, damageMap);
                weaponEffects.bowEffect(arrow, event.getForce());
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
        if (ItemSystem.isItemType(event.getItem(), ItemType.SPEAR) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);

            // using shields with spears
            if (ItemSystem.isItemType(player.getInventory().getItemInOffHand(), ItemType.SHIELD)) {
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

        if (ItemSystem.isItemType(item, ItemType.GLOVE)) {
            event.setCancelled(true);
        }
    }
}