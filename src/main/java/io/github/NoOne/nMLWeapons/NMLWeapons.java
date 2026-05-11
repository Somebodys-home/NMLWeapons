package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.NMLItems;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private ProfileManager profileManager;
    private AttackCooldownSystem attackCooldownSystem;
    private ItemSystem itemSystem;
    private GlovesTracker glovesTracker;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();
        itemSystem = JavaPlugin.getPlugin(NMLItems.class).getItemSystem();

        attackCooldownSystem = new AttackCooldownSystem(this);
        attackCooldownSystem.start();

        glovesTracker = new GlovesTracker(this);
        glovesTracker.startTracker();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
    }

    @Override
    public void onDisable() {
        attackCooldownSystem.stop();
        glovesTracker.stopTracker();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public ItemSystem getItemSystem() {
        return itemSystem;
    }

    public AttackCooldownSystem getAttackCooldownSystem() {
        return attackCooldownSystem;
    }
}
