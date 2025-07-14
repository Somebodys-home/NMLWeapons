package io.github.Gabriel.nMLWeapons.weaponEffects;

import io.github.Gabriel.nMLWeapons.NMLWeapons;
import io.github.Gabriel.nMLWeapons.WeaponSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class WeaponListener implements Listener {
    private WeaponSystem weaponSystem;

    public WeaponListener(NMLWeapons nmlWeapons) {
        weaponSystem = nmlWeapons.getWeaponSystem();
    }

    @EventHandler
    public void onSwingWeapon(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory pi = player.getInventory();
        ItemStack weapon = pi.getItemInMainHand();

        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            WeaponEffects weaponEffects = new WeaponEffects();
            switch (weaponSystem.getWeaponType(weapon)) {
                case SWORD -> weaponEffects.swordEffect(weapon, player, true);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();

            WeaponEffects weaponEffects = new WeaponEffects();
            switch (weaponSystem.getWeaponType(weapon)) {
                case SWORD -> weaponEffects.swordEffect(weapon, player, false);
            }
        }
    }
}
