package io.github.NoOne.nMLWeapons;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private WeaponStatsManager weaponStatsManager;
    private ProfileManager profileManager;
    private WeaponStatsUpdater WeaponStatsUpdater;

    @Override
    public void onEnable() {
        weaponStatsManager = new WeaponStatsManager(JavaPlugin.getPlugin(NMLPlayerStats.class));
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        WeaponStatsUpdater = new WeaponStatsUpdater(this);
        WeaponStatsUpdater.start();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getCommand("generateWeapon").setExecutor(new GenerateWeaponCommand(this));
    }

    public WeaponStatsManager getWeaponManager() {
        return weaponStatsManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public WeaponStatsUpdater getWeaponthing() {
        return WeaponStatsUpdater;
    }
}
