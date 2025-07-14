package io.github.Gabriel.nMLWeapons.weaponEffects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WeaponEffects {
    public void swordEffect(ItemStack weapon, Player player, boolean callDamageEvent) {
        Location particleLocation = player.getLocation().add(0, 1, 0);
        Vector direction = particleLocation.getDirection().multiply(3); // distance in blocks of particle from player

        particleLocation.add(direction);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 0, 0, 0, 0, 0);

        if (callDamageEvent) {
            for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 1.5, 5, 1.5)) {
                DamageSource source = DamageSource.builder(DamageType.PLAYER_ATTACK)
                        .withCausingEntity(player)
                        .withDirectEntity(player)
                        .withDamageLocation(particleLocation)
                        .build();

                Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, source, 0));
            }
        }
    }
}
