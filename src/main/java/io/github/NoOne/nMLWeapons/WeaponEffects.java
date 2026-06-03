package io.github.NoOne.nMLWeapons;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageHelper;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.enums.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class WeaponEffects {
    private NMLWeapons nmlWeapons;
    private ProfileManager profileManager;
    private BukkitTask arrowDespawnTask;
    private ItemSystem itemSystem;

    public WeaponEffects(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        this.profileManager = nmlWeapons.getProfileManager();
        itemSystem = nmlWeapons.getItemSystem();
    }

    public void swordEffect(Player player) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(3); // distance in blocks of particle from player

        AttackCooldownSystem.setAttackCooldown(player, 1);
        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0, 0, 0, 0, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);

        for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, .33, 1.5)) {
            if (entity != player && entity instanceof LivingEntity livingEntity) {
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageHelper.convertPlayerStats2Damage(stats)));
            }
        }
    }

    public void daggerEffect(Player player) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player
        
        AttackCooldownSystem.setAttackCooldown(player, .5);
        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0, 0, 0, 0, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);

        for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, .33, 1.5)) {
            if (entity != player && entity instanceof LivingEntity livingEntity) {
                Vector knockback = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(.1);
                
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageHelper.convertPlayerStats2Damage(stats)));
                livingEntity.setVelocity(knockback);
            }
        }
    }

    public void axeEffect(Player player) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        Location baseLocation = player.getLocation().add(0, 1, 0);
        Vector forward = baseLocation.getDirection().normalize().multiply(3);
        Vector leftOffset = forward.clone().rotateAroundY(Math.toRadians(-25));
        Vector rightOffset = forward.clone().rotateAroundY(Math.toRadians(25));
        Location leftSlashLocation = baseLocation.clone().add(leftOffset);
        Location centerSlashLocation = baseLocation.clone().add(forward);
        Location rightSlashLocation = baseLocation.clone().add(rightOffset);
        HashSet<LivingEntity> hitEntities = new HashSet<>();

        AttackCooldownSystem.setAttackCooldown(player, 2);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, leftSlashLocation, 0, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, centerSlashLocation, 0, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, rightSlashLocation, 0, 0, 0, 0, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2f, .5f);

        for (Entity entity : player.getWorld().getNearbyEntities(leftSlashLocation, 1.5, 5, 1.5)) {
            if (entity != player && entity instanceof LivingEntity livingEntity) {
                hitEntities.add(livingEntity);
            }
        }

        for (Entity entity : player.getWorld().getNearbyEntities(centerSlashLocation, 1.5, 5, 1.5)) {
            if (entity != player && entity instanceof LivingEntity livingEntity) {
                hitEntities.add(livingEntity);
            }
        }

        for (Entity entity : player.getWorld().getNearbyEntities(rightSlashLocation, 1.5, 5, 1.5)) {
            if (entity != player && entity instanceof LivingEntity livingEntity) {
                hitEntities.add(livingEntity);
            }
        }

        for (LivingEntity livingEntity : hitEntities) {
            Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageHelper.convertPlayerStats2Damage(stats)));
        }
    }

    public void hammerEffect(Player player) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        Location baseLocation = player.getLocation().add(0, 1, 0);
        Vector forward = baseLocation.getDirection().normalize().multiply(3);
        Location explosion = baseLocation.clone().add(forward);

        AttackCooldownSystem.setAttackCooldown(player, 3);
        player.playSound(player.getLocation(), Sound.ITEM_MACE_SMASH_AIR, 1f, 1f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, explosion, 0, 0, 0, 0, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.getWorld().spawnParticle(Particle.CRIT, explosion.clone().add(0, .5, 0), 50, .20, .20, .20);
            }
        }.runTaskLater(nmlWeapons, 7L);

        for (Entity entity : player.getWorld().getNearbyEntities(explosion, 1.5, 2, 1.5)) {
            if (entity != player && entity instanceof LivingEntity livingEntity) {
                Vector knockback = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageHelper.convertPlayerStats2Damage(stats)));
                knockback.setY(.2);
                livingEntity.setVelocity(knockback);
            }
        }
    }

    public void spearEffect(Player player) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        Location start = player.getLocation().add(0, 1, 0);
        Vector direction = start.getDirection().normalize().multiply(.5);
        HashSet<LivingEntity> hitEntities = new HashSet<>();

        AttackCooldownSystem.setAttackCooldown(player, 1);
        player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1f, 1f);

        for (int i = 1; i <= 12; i++) {
            Location point = start.clone().add(direction.clone().multiply(i));

            point.setY(point.getY() + (i * .05));
            player.getWorld().spawnParticle(Particle.CRIT, point, 5, .01, .01, .01, 0);

            for (Entity entity : player.getWorld().getNearbyEntities(point, .55, .55, .55)) {
                if (entity != player && entity instanceof LivingEntity livingEntity) {
                    hitEntities.add(livingEntity);
                }
            }
        }

        for (LivingEntity livingEntity : hitEntities) {
            Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageHelper.convertPlayerStats2Damage(stats)));
        }
    }

    public void gloveEffect(Player player, int punchPattern) {
        Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
        PlayerInventory playerInventory = player.getInventory();
        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player
        HashMap<DamageType, Double> halfDamage = DamageHelper.multiplyDamageMap(DamageHelper.convertPlayerStats2Damage(stats), .5);

        player.setMetadata("glove_effect", new FixedMetadataValue(nmlWeapons, true));
        AttackCooldownSystem.setAttackCooldown(player, 1);
        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.EXPLOSION, particleLocation, 0, 0, 0, 0, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 2f, 1f);

        if (punchPattern == 0) {
            player.swingOffHand();
        }

        for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 2, 1.5)) {
            if (entity != player && entity instanceof LivingEntity livingEntity) {
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, halfDamage));
            }
        }

        if (itemSystem.getItemType(playerInventory.getItemInOffHand()) == ItemType.GLOVE) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location particleLocation = player.getLocation().add(0, 1, 0);
                    Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player

                    particleLocation.add(direction);
                    player.removeMetadata("glove_effect", nmlWeapons);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, particleLocation, 0, 0, 0, 0, 0);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 2f, 1f);

                    if (punchPattern == 0) {
                        player.swingMainHand();
                    } else {
                        player.swingOffHand();
                    }

                    for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 20, 1.5)) {
                        if (entity != player && entity instanceof LivingEntity livingEntity) {
                            livingEntity.setNoDamageTicks(0);
                            Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, halfDamage));
                        }
                    }
                }
            }.runTaskLater(nmlWeapons, 7L);
        }

        player.removeMetadata("glove_effect", nmlWeapons);
    }

    public void bowEffect(Player player, Arrow arrow, Float force) {
        arrowDespawnTask = Bukkit.getScheduler().runTaskTimer(nmlWeapons, () -> {
            if (arrow.isDead() || arrow.isInBlock()) {
                arrow.remove();
                arrowDespawnTask.cancel();
            }
        }, 100L, 40L);

        // arrow trail
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

        if (force <= 2.0f) { // semi-charged shot
            double boost;
            if (force <= .5) {
                boost = 1.0 + (.5 * (1 - (force / .5)));
            } else {
                double scale = (2.0 - force) / 1.5;
                boost = 1.0 + (.25 * scale);
            }
            arrow.setVelocity(arrow.getVelocity().multiply(boost));
        }
    }

    public void magicalEffect(Player player) {
        RayTraceResult target = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                16,
                entity -> entity instanceof LivingEntity && !entity.equals(player)
        );

        if (target != null && target.getHitEntity() instanceof LivingEntity livingEntity) { // successfully traced a target
            Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();
            Location eyeLoc = player.getEyeLocation();
            Vector direction = eyeLoc.getDirection().normalize();
            Random random = new Random();

            Vector randomVec = new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble()).normalize();
            Vector curveAxis = direction.clone().crossProduct(randomVec).normalize();
            Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

            Location start = player.getLocation().add(0, 1.2, 0).add(right.multiply(.4));
            Location end = livingEntity.getLocation().add(0, .5, 0);

            double curveDirection = random.nextBoolean() ? 1 : -1; // Is the arc gonna curve left or right
            double verticalCurveDirection = random.nextBoolean() ? 1 : -1; // Is the arc gonna curve up or down
            double curveAmount = 1.5 + random.nextDouble() * 3.0; // How far the arc bends left/right (scaled randomly for variety)
            double minHeight = .2 + random.nextDouble(); // Minimum arc height
            double maxHeight = 1.0 + random.nextDouble() * 1.5; // Maximum arc height
            int particleInstances = 10;

            AttackCooldownSystem.setAttackCooldown(player, 1.15);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .6f, 1f);

            new BukkitRunnable() { /// particle arc
                int i = 0;

                @Override
                public void run() {
                    if (i > particleInstances) {
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, .8f, 1f);
                        player.getWorld().spawnParticle(Particle.EXPLOSION, end, 1, 0, 1, 0, 0);
                        Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageHelper.convertPlayerStats2Damage(stats)));
                        cancel();
                        return;
                    }

                    // Linear interpolation between start and end
                    double progress = (double) i / particleInstances; // Progress along the arc (.0 -> 1)
                    double baseX = start.getX() + (end.getX() - start.getX()) * progress;
                    double baseY = start.getY() + (end.getY() - start.getY()) * progress;
                    double baseZ = start.getZ() + (end.getZ() - start.getZ()) * progress;

                    // Apply side curve and vertical arc to base position
                    double curveOffset = curveDirection * curveAmount * Math.sin(progress * Math.PI); // Horozontial sinusoidal curve offset
                    double heightFactor = minHeight + (maxHeight - minHeight) * Math.sin(progress * Math.PI); // Vertical sinusoidal curve height
                    double x = baseX + curveAxis.getX() * curveOffset;
                    double y = baseY + heightFactor * verticalCurveDirection;
                    double z = baseZ + curveAxis.getZ() * curveOffset;

                    // Clamp the minimum y value based on TARGET'S position
                    double floorYLimit = end.getY() + .1;
                    y = Math.max(y, floorYLimit);

                    Location particleLocation = new Location(player.getWorld(), x, y, z);
                    player.getWorld().spawnParticle(Particle.GLOW, particleLocation, 50, .1, .075, .1, 0);

                    i++;
                }
            }.runTaskTimer(nmlWeapons, 0L, 1L);
        } else { /// miss x
            Location center = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1.5));
            int pointsPerLine = 5;
            double size = .33;

            center.setY(center.getY() - .125);
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, .6f, .5f);

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks > 2) {
                        cancel();
                        return;
                    }

                    Vector forward = player.getEyeLocation().getDirection().normalize();
                    Vector up = new Vector(0, 1, 0);

                    if (Math.abs(forward.dot(up)) > .98) {
                        up = new Vector(1, 0, 0);
                    }

                    Vector right = forward.clone().crossProduct(up).normalize();
                    Vector screenUp = right.clone().crossProduct(forward).normalize();

                    for (int i = 0; i <= pointsPerLine; i++) {
                        double t = i / (double) pointsPerLine;
                        double offset = size * (t - .5);

                        Vector offset1 = right.clone().multiply(offset).add(screenUp.clone().multiply(offset));
                        Vector offset2 = right.clone().multiply(offset).add(screenUp.clone().multiply(-offset));

                        Location point1 = center.clone().add(offset1);
                        Location point2 = center.clone().add(offset2);

                        player.getWorld().spawnParticle(Particle.DRIPPING_DRIPSTONE_LAVA, point1, 1, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.DRIPPING_DRIPSTONE_LAVA, point2, 1, 0, 0, 0);
                    }

                    ticks++;
                }
            }.runTaskTimer(nmlWeapons, 0L, 1L);
        }
    }
}
