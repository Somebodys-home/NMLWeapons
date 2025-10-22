package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private WeaponStatsManager weaponStatsManager;
    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        weaponStatsManager = new WeaponStatsManager(JavaPlugin.getPlugin(NMLPlayerStats.class));
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getCommand("generateWeapon").setExecutor(new GenerateWeaponCommand(this));
    }

    public WeaponStatsManager getWeaponManager() {
        return weaponStatsManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
