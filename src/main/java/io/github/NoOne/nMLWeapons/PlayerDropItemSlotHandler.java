package io.github.NoOne.nMLWeapons;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDropItemSlotHandler implements Listener {
    public PlayerDropItemSlotHandler(ProtocolManager protocolManager) {
        protocolManager.addPacketListener(new PacketAdapter(NMLWeapons.getInstance(), PacketType.Play.Client.BLOCK_DIG) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer container = event.getPacket();
                if (container.getPlayerDigTypes().getValues().get(0).equals(EnumWrappers.PlayerDigType.DROP_ITEM) ||
                    container.getPlayerDigTypes().getValues().get(0).equals(EnumWrappers.PlayerDigType.DROP_ALL_ITEMS)) {

                    Player player = event.getPlayer();
                    ItemStack item = player.getInventory().getItemInMainHand(); // capture immediately

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerDropItemSlotEvent customEvent = new PlayerDropItemSlotEvent(
                                    player,
                                    item,
                                    player.getInventory(),
                                    player.getInventory().getHeldItemSlot());

                            Bukkit.getPluginManager().callEvent(customEvent);
                            if (customEvent.isCancelled()) event.setCancelled(true);
                        }
                    }.runTask(NMLWeapons.getInstance()); // Ensures it runs synchronously
                }
            }
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        PlayerDropItemSlotEvent customEvent = null;

        if (event.getAction() == InventoryAction.DROP_ALL_SLOT || event.getAction() == InventoryAction.DROP_ONE_SLOT) {
            customEvent = new PlayerDropItemSlotEvent(player, event.getCurrentItem(), event.getClickedInventory(), event.getSlot());
        } else if (event.getAction() == InventoryAction.DROP_ALL_CURSOR || event.getAction() == InventoryAction.DROP_ONE_CURSOR) {
            customEvent = new PlayerDropItemSlotEvent(player, event.getCursor(), event.getClickedInventory(), -1);
        }

        if (customEvent == null) return;

        Bukkit.getPluginManager().callEvent(customEvent);
        if (customEvent.isCancelled()) event.setCancelled(true);
    }
}