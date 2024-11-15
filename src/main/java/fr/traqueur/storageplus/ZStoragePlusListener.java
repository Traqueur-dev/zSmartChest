package fr.traqueur.storageplus;

import fr.traqueur.storageplugs.api.StoragePlusManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ZStoragePlusListener implements Listener {

    private final StoragePlusManager manager;

    public ZStoragePlusListener(StoragePlusManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        this.manager.getChestFromItem(item).ifPresent(chest -> {
            this.manager.placeChest(event.getBlockPlaced().getLocation(), chest);
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.manager.breakChest(event.getBlock().getLocation());
    }
}
