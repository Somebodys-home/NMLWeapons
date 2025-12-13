package io.github.NoOne.nMLWeapons;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.NoOne.nMLItems.NMLItems;
import io.github.NoOne.nMLItems.itemDictionary.Weapons;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLWeapons extends JavaPlugin {
    private static NMLWeapons instance;
    private ProtocolManager protocolManager;
    private WeaponStatsManager weaponStatsManager;
    private ProfileManager profileManager;
    private Weapons weapons;

    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

        weaponStatsManager = new WeaponStatsManager(JavaPlugin.getPlugin(NMLPlayerStats.class));
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();;
        weapons = JavaPlugin.getPlugin(NMLItems.class).getWeaponGenerator();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemSlotHandler(protocolManager), this);
        getCommand("generateWeapon").setExecutor(new GenerateWeaponCommand(this));
    }

    public static NMLWeapons getInstance() {
        return instance;
    }

    public WeaponStatsManager getWeaponStatsManager() {
        return weaponStatsManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public Weapons getWeapons() {
        return weapons;
    }
}
