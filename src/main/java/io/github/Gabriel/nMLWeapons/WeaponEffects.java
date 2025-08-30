package io.github.Gabriel.nMLWeapons;

import io.github.Gabriel.damagePlugin.customDamage.CustomDamageEvent;
import io.github.Gabriel.damagePlugin.customDamage.CustomDamager;
import io.github.Gabriel.damagePlugin.customDamage.DamageConverter;
import io.github.Gabriel.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class WeaponEffects {
    private NMLWeapons nmlWeapons;
    private NMLPlayerStats nmlPlayerStats;
    private Set<UUID> hitEntityUUIDs;
    private BukkitTask arrowDespawnTask;

    public WeaponEffects(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
        this.nmlPlayerStats = NMLWeapons.getNmlPlayerStats();
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
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats())));
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }

        hitEntityUUIDs.clear();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
    }

    public void daggerEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        player.setCooldown(weapon.getType(), 5); // .25s cooldown

        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player

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
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats())));
                Vector knockback = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.1);

                livingEntity.setNoDamageTicks(5);
                livingEntity.setVelocity(knockback);
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }

        hitEntityUUIDs.clear();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);
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
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats())));                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }

        hitEntityUUIDs.clear();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2f, .5f);
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
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats())));                Vector knockback = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

                knockback.setY(.2);
                livingEntity.setVelocity(knockback);
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }

        hitEntityUUIDs.clear();
        player.playSound(player.getLocation(), Sound.ITEM_MACE_SMASH_AIR, 1f, 1f);
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
                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats())));                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }

        hitEntityUUIDs.clear();
        player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1f, 1f);
    }

    public void gloveEffect(ItemStack weapon, Player player, int punchPattern) {
        if (player.hasCooldown(weapon.getType())) return;
        PlayerInventory playerInventory = player.getInventory();

        player.setCooldown(weapon.getType(), 20); // 1s cooldown

        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player
        HashMap<DamageType, Double> halfDamage = DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats());
            halfDamage.replaceAll((k, v) -> v / 2); // actually halves the damage

        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.EXPLOSION, particleLocation, 0, 0, 0, 0, 0);

        if (punchPattern == 0) {
            player.swingOffHand();
        }

        for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 2, 1.5)) {
            if (entity != player && !entity.hasMetadata("been hit")) {
                hitEntityUUIDs.add(entity.getUniqueId());
                entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
            }
        }

        for (UUID uuid : hitEntityUUIDs) {
            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                CustomDamager.doDamage(livingEntity, player, halfDamage);
                livingEntity.setNoDamageTicks(7);
                livingEntity.removeMetadata("been hit", nmlWeapons);
            }
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);

        if (ItemSystem.getItemType(playerInventory.getItemInOffHand()) == ItemType.GLOVE) {
            // second hit on a .35s delay
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location particleLocation = player.getLocation().add(0, 1, 0);
                    Vector direction = particleLocation.getDirection().multiply(2); // distance in blocks of particle from player

                    particleLocation.add(direction);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, particleLocation, 0, 0, 0, 0, 0);

                    if (punchPattern == 0) {
                        player.swingMainHand();
                    } else {
                        player.swingOffHand();
                    }

                    for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 2, 1.5)) {
                        if (entity != player && !entity.hasMetadata("been hit")) {
                            hitEntityUUIDs.add(entity.getUniqueId());
                            entity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));
                        }
                    }

                    for (UUID uuid : hitEntityUUIDs) {
                        if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                            CustomDamager.doDamage(livingEntity, player, halfDamage);
                            livingEntity.removeMetadata("been hit", nmlWeapons);
                        }
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
                }
            }.runTaskLater(nmlWeapons, 7L);
        } else {
            player.sendMessage("§c⚠ §nThis is a two-handed weapon!§r§c ⚠");
        }

        hitEntityUUIDs.clear();
    }

    public void bowEffect(Player player, Arrow arrow, Float force) {
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
            arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(3));

            // sonic boom particles
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!arrow.isValid() || arrow.isDead() || arrow.isInBlock()) {
                        player.getWorld().spawnParticle(Particle.SONIC_BOOM, arrow.getLocation(), 0, 0, 0, 0, 0);
                        this.cancel();
                        return;
                    }

                    player.getWorld().spawnParticle(Particle.SONIC_BOOM, arrow.getLocation(), 0, 0, 0, 0, 0);
                }
            }.runTaskTimer(nmlWeapons, 2L, 10L);
        }

        // Schedule arrow despawn task
        arrowDespawnTask = Bukkit.getScheduler().runTaskTimer(nmlWeapons, () -> {
            if (arrow.isDead() || arrow.isInBlock()) {
                arrow.remove();
                arrowDespawnTask.cancel();
            }
        }, 100L, 40L);
    }

    public void magicalEffect(ItemStack weapon, Player player) {
        if (player.hasCooldown(weapon.getType())) return;

        RayTraceResult target = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                16,
                entity -> entity instanceof LivingEntity && !entity.equals(player)
        );

        if (target != null && target.getHitEntity() instanceof LivingEntity livingEntity) { // successfully traced a target
            if (livingEntity.hasMetadata("been hit")) {
                return;
            }

            player.setCooldown(weapon.getType(), 23); // 1.15s cooldown
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, .6f, 1f);

            hitEntityUUIDs.add(livingEntity.getUniqueId());
            livingEntity.setMetadata("been hit", new FixedMetadataValue(nmlWeapons, true));

            Location eyeLoc = player.getEyeLocation();
            Vector direction = eyeLoc.getDirection().normalize();
            Random random = new Random();

            Vector randomVec = new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble()).normalize();
            Vector curveAxis = direction.clone().crossProduct(randomVec).normalize();
            Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

            Location start = player.getLocation().add(0, 1.2, 0).add(right.multiply(0.4));
            Location end = livingEntity.getLocation().add(0, 0.5, 0);

            double curveDirection = random.nextBoolean() ? 1 : -1; // Is the arc gonna curve left or right
            double verticalCurveDirection = random.nextBoolean() ? 1 : -1; // Is the arc gonna curve up or down
            double curveAmount = 1.5 + random.nextDouble() * 3.0; // How far the arc bends left/right (scaled randomly for variety)
            double minHeight = 0.2 + random.nextDouble(); // Minimum arc height
            double maxHeight = 1.0 + random.nextDouble() * 1.5; // Maximum arc height
            int particleInstances = 10;

            new BukkitRunnable() { // create arc of particles
                int i = 0;

                @Override
                public void run() {
                    if (i > particleInstances) {
                        for (UUID uuid : hitEntityUUIDs) {
                            if (Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity2) {
                                Bukkit.getPluginManager().callEvent(new CustomDamageEvent(livingEntity, player, DamageConverter.convertPlayerStats2Damage(nmlPlayerStats.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats())));                                player.getWorld().spawnParticle(Particle.EXPLOSION, end, 1, 0, 0, 0, 0);
                                livingEntity2.removeMetadata("been hit", nmlWeapons);
                            }
                        }
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, .8f, 1f);
                        livingEntity.removeMetadata("been hit", nmlWeapons);
                        cancel();
                        return;
                    }

                    double progress = (double) i / particleInstances; // Progress along the arc (0.0 -> 1)

                    // Linear interpolation between start and end
                    double baseX = start.getX() + (end.getX() - start.getX()) * progress;
                    double baseY = start.getY() + (end.getY() - start.getY()) * progress;
                    double baseZ = start.getZ() + (end.getZ() - start.getZ()) * progress;

                    double curveOffset = curveDirection * curveAmount * Math.sin(progress * Math.PI); // Horozontial sinusoidal curve offset
                    double heightFactor = minHeight + (maxHeight - minHeight) * Math.sin(progress * Math.PI); // Vertical sinusoidal curve height

                    // Apply side curve and vertical arc to base position
                    double x = baseX + curveAxis.getX() * curveOffset;
                    double y = baseY + heightFactor * verticalCurveDirection;
                    double z = baseZ + curveAxis.getZ() * curveOffset;

                    double floorYLimit = end.getY() + .1; // Clamp the minimum y value based on TARGET'S position
                    y = Math.max(y, floorYLimit);

                    Location particleLocation = new Location(player.getWorld(), x, y, z);
                    player.getWorld().spawnParticle(Particle.GLOW, particleLocation, 50, 0.1, 0.075, 0.1, 0);

                    i++;
                }
            }.runTaskTimer(nmlWeapons.getInstance(), 0L, 1L);
        } else { // miss x
            Location center = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1.5));
            center.setY(center.getY() - .125);

            int pointsPerLine = 5;
            double size = 0.33;

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

                    if (Math.abs(forward.dot(up)) > 0.98) {
                        up = new Vector(1, 0, 0);
                    }

                    Vector right = forward.clone().crossProduct(up).normalize();
                    Vector screenUp = right.clone().crossProduct(forward).normalize();

                    for (int i = 0; i <= pointsPerLine; i++) {
                        double t = i / (double) pointsPerLine;
                        double offset = size * (t - 0.5);

                        Vector offset1 = right.clone().multiply(offset).add(screenUp.clone().multiply(offset));
                        Vector offset2 = right.clone().multiply(offset).add(screenUp.clone().multiply(-offset));

                        Location point1 = center.clone().add(offset1);
                        Location point2 = center.clone().add(offset2);

                        player.getWorld().spawnParticle(Particle.DRIPPING_DRIPSTONE_LAVA, point1, 1, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.DRIPPING_DRIPSTONE_LAVA, point2, 1, 0, 0, 0);
                    }

                    ticks++;
                }
            }.runTaskTimer(nmlWeapons.getInstance(), 0L, 1L);

            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, .6f, .5f);
        }

        hitEntityUUIDs.clear();
    }
}
