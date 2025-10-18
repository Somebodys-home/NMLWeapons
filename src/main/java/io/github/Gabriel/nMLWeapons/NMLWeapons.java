package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLSkills.NMLSkills;
import io.github.NoOne.nMLSkills.skillSetSystem.SkillSetManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private static ProfileManager profileManager;
    private static SkillSetManager skillSetManager;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();
        skillSetManager = JavaPlugin.getPlugin(NMLSkills.class).getSkillSetManager();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getCommand("generateWeapon").setExecutor(new GenerateWeaponCommand());
    }

    public static ProfileManager getProfileManager() {
        return profileManager;
    }

    public static SkillSetManager getSkillSetManager() {
        return skillSetManager;
    }
}
