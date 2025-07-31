package io.github.Gabriel.nMLWeapons;

import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.NMLItems;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private NMLWeapons instance;
    private static NMLPlayerStats nmlPlayerStats;
    private NMLItems nmlItems;
    private ItemSystem itemSystem;
    private WeaponSystem weaponSystem;

    @Override
    public void onEnable() {
        instance = this;
        nmlItems = JavaPlugin.getPlugin(NMLItems.class);

        itemSystem = nmlItems.getItemSystem();

        Plugin plugin = Bukkit.getPluginManager().getPlugin("NMLPlayerStats");
        if (plugin instanceof NMLPlayerStats statsPlugin) {
            nmlPlayerStats = statsPlugin;
        }

        weaponSystem = new WeaponSystem(this);

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getCommand("generateWeapon").setExecutor(new GenerateWeaponCommand(this));
    }

    public NMLWeapons getInstance() {
        return instance;
    }

    public static NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }

    public WeaponSystem getWeaponSystem() {
        return weaponSystem;
    }

    public ItemSystem getItemSystem() {
        return itemSystem;
    }
}
