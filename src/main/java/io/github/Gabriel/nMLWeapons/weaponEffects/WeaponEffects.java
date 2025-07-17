package io.github.Gabriel.nMLWeapons.weaponEffects;

import io.github.Gabriel.damagePlugin.customDamage.CustomDamager;
import io.github.Gabriel.damagePlugin.customDamage.DamageKey;
import io.github.Gabriel.nMLWeapons.NMLWeapons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WeaponEffects {
    private NMLWeapons nmlWeapons;
    private Set<UUID> hitEntityUUIDs;

    public WeaponEffects(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        hitEntityUUIDs = new HashSet<>();
    }

    public void swordEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        player.setCooldown(weapon.getType(), 20); // 1s cooldown

        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(3); // distance in blocks of particle from player

        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0, 0, 0, 0, 0);

        for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, .33, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (UUID uuid : hitEntityUUIDs) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                CustomDamager.doDamage(livingEntity, player, new DamageKey().getAllDamageStats(weapon));
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }
    }

    public void daggerEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        player.setCooldown(weapon.getType(), 10); // .5s cooldown

        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player

        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0, 0, 0, 0, 0);

        for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 5, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (UUID uuid : hitEntityUUIDs) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                CustomDamager.doDamage(livingEntity, player, new DamageKey().getAllDamageStats(weapon));
                Vector knockback = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.3);

                livingEntity.setVelocity(knockback);
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }
    }

    public void axeEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        player.setCooldown(weapon.getType(), 40); // 2s cooldown

        Location baseLocation = player.getLocation().add(0, 1, 0);
        Vector forward = baseLocation.getDirection().normalize().multiply(3);
        Vector leftOffset = forward.clone().rotateAroundY(Math.toRadians(-25));
        Vector rightOffset = forward.clone().rotateAroundY(Math.toRadians(25));

        Location leftSlash = baseLocation.clone().add(leftOffset);
        Location centerSlash = baseLocation.clone().add(forward);
        Location rightSlash = baseLocation.clone().add(rightOffset);

        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, leftSlash, 0, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, centerSlash, 0, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, rightSlash, 0, 0, 0, 0, 0);

        for (Entity entity : player.getWorld().getNearbyEntities(leftSlash, 1.5, 5, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (Entity entity : player.getWorld().getNearbyEntities(centerSlash, 1.5, 5, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (Entity entity : player.getWorld().getNearbyEntities(rightSlash, 1.5, 5, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (UUID uuid : hitEntityUUIDs) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                CustomDamager.doDamage(livingEntity, player, new DamageKey().getAllDamageStats(weapon));
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }
    }

    public void hammerEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        player.setCooldown(weapon.getType(), 60); // 3s cooldown

        Location baseLocation = player.getLocation().add(0, 1, 0);
        Vector forward = baseLocation.getDirection().normalize().multiply(3);
        Location explosion = baseLocation.clone().add(forward);

        player.getWorld().spawnParticle(Particle.EXPLOSION, explosion, 0, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.CRIT, explosion.clone().add(0, 0.5, 0), 80, 0.8, 0.8, 0.8, 0);

        for (Entity entity : player.getWorld().getNearbyEntities(explosion, 1.5, 2, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (UUID uuid : hitEntityUUIDs) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                CustomDamager.doDamage(livingEntity, player, new DamageKey().getAllDamageStats(weapon));
                Vector knockback = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

                knockback.setY(.2);
                livingEntity.setVelocity(knockback);
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }
    }

    public void spearEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        player.setCooldown(weapon.getType(), 40); // 2s cooldown

        Location start = player.getLocation().add(0, 1, 0);
        Vector direction = start.getDirection().normalize().multiply(0.5);

        for (int i = 1; i <= 12; i++) {
            Location point = start.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.CRIT, point, 5, 0.01, 0.01, 0.01, 0);

            for (Entity entity : player.getWorld().getNearbyEntities(point, .5, .5, .5)) {
                if (entity != player && !entity.hasMetadata("been hit")) {
                    hitEntityUUIDs.add(entity.getUniqueId());
                    entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
                }
            }
        }

        for (UUID uuid : hitEntityUUIDs) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                CustomDamager.doDamage(livingEntity, player, new DamageKey().getAllDamageStats(weapon));
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }
    }

    public void gloveEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        player.setCooldown(weapon.getType(), 25); // 1.5s cooldown

        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player

        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.EXPLOSION, particleLocation, 0, 0, 0, 0, 0);

        for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 2, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (UUID uuid : hitEntityUUIDs) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                CustomDamager.doDamage(livingEntity, player, new DamageKey().getAllDamageStats(weapon));
                livingEntity.setNoDamageTicks(0);
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }

        // second hit on a .35s delay
        new BukkitRunnable() {
            @Override
            public void run() {
                Location particleLocation = player.getLocation().add(0, 1, 0);
                Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player

                particleLocation.add(direction);
                player.getWorld().spawnParticle(Particle.EXPLOSION, particleLocation, 0, 0, 0, 0, 0);
                player.swingOffHand();

                for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 2, 1.5)) {
                    if (entity != player && !entity.hasMetadata("been hit")) {
                        hitEntityUUIDs.add(entity.getUniqueId());
                        entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
                    }
                }

                for (UUID uuid : hitEntityUUIDs) {
                    if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                        CustomDamager.doDamage(livingEntity, player, new DamageKey().getAllDamageStats(weapon));
                        livingEntity.removeMetadata("been hit", nmlWeapons);
                    }
                }
            }
        }.runTaskLater(nmlWeapons, 7L);
    }
}
