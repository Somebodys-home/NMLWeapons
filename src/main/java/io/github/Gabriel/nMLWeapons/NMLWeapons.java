package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private NMLWeapons instance;
    private static NMLPlayerStats nmlPlayerStats;
    private WeaponGenerator weaponGenerator;

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("NMLPlayerStats");
        if (plugin instanceof NMLPlayerStats statsPlugin) {
            nmlPlayerStats = statsPlugin;
        }

        weaponGenerator = new WeaponGenerator();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getCommand("generateWeapon").setExecutor(new GenerateWeaponCommand(this));
    }

    public NMLWeapons getInstance() {
        return instance;
    }

    public static NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }

    public WeaponGenerator getWeaponSystem() {
        return weaponGenerator;
    }
}
