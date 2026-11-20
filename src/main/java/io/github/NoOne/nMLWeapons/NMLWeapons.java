package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.StatConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private static NMLWeapons instance;
    private StatConfigManager statConfigManager;
    private AttackCooldownSystem attackCooldownSystem;
    private GlovesTracker glovesTracker;

    @Override
    public void onEnable() {
        instance = this;
        statConfigManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getStatConfigManager();

        attackCooldownSystem = new AttackCooldownSystem(this);
        attackCooldownSystem.start();

        GlovesTracker.startTracker();
        ArrowTracker.startArrowTracker();
        ArrowTracker.startArrowTrailTracker();
        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
    }

    @Override
    public void onDisable() {
        attackCooldownSystem.stop();
    }

    public static NMLWeapons getInstance() {
        return instance;
    }

    public StatConfigManager getStatConfigManager() {
        return statConfigManager;
    }

    public AttackCooldownSystem getAttackCooldownSystem() {
        return attackCooldownSystem;
    }
}
