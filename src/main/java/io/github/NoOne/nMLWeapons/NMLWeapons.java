package io.github.NoOne.nMLWeapons;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.NoOne.nMLItems.NMLItems;
import io.github.NoOne.nMLItems.itemDictionary.Weapons;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private ProfileManager profileManager;
    private AttackCooldownSystem attackCooldownSystem;
    private Weapons weapons;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();;
        weapons = JavaPlugin.getPlugin(NMLItems.class).getWeaponGenerator();

        attackCooldownSystem = new AttackCooldownSystem(this);
        attackCooldownSystem.start();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemSlotHandler(ProtocolLibrary.getProtocolManager(), this), this);
    }

    @Override
    public void onDisable() {
        attackCooldownSystem.stop();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public Weapons getWeapons() {
        return weapons;
    }

    public AttackCooldownSystem getAttackCooldownSystem() {
        return attackCooldownSystem;
    }
}
