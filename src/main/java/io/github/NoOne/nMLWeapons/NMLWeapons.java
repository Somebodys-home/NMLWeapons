package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private static NMLWeapons instance;
    private ProfileManager profileManager;
    private AttackCooldownSystem attackCooldownSystem;
    private GlovesTracker glovesTracker;

    @Override
    public void onEnable() {
        instance = this;
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        attackCooldownSystem = new AttackCooldownSystem(this);
        attackCooldownSystem.start();

        glovesTracker = new GlovesTracker(this);
        glovesTracker.startTracker();

        ArrowTracker.startArrowTracker();
        ArrowTracker.startArrowTrailTracker();
        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
    }

    @Override
    public void onDisable() {
        attackCooldownSystem.stop();
        glovesTracker.stopTracker();
    }

    public static NMLWeapons getInstance() {
        return instance;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public AttackCooldownSystem getAttackCooldownSystem() {
        return attackCooldownSystem;
    }
}
