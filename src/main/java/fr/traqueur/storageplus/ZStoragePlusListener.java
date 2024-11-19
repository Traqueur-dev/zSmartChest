package fr.traqueur.storageplus;

import fr.traqueur.storageplugs.api.StoragePlusManager;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ZStoragePlusListener implements Listener {

    private final StoragePlusManager manager;

    public ZStoragePlusListener(StoragePlusManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        this.manager.getChestFromItem(item).ifPresent(chest -> {
            this.manager.placeChest(event.getPlayer(), event.getBlockPlaced().getLocation(), chest);
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.manager.breakChest(event.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) {
            return;
        }

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        this.manager.getChestFromBlock(event.getClickedBlock().getLocation()).ifPresent(chest -> {
            event.setCancelled(true);
            this.manager.openChest(event.getPlayer(), chest);
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        List<ItemStack> items = this.manager.addItemsToChest(event.getItemDrop().getLocation().getChunk(), event.getItemDrop().getItemStack());
        if (items.isEmpty()) {
            event.getItemDrop().remove();
        } else {
            event.getItemDrop().setItemStack(items.getFirst());
        }
    }


    @EventHandler
    public void onDrop(EntityDropItemEvent event) {
        List<ItemStack> items = this.manager.addItemsToChest(event.getItemDrop().getLocation().getChunk(),event.getItemDrop().getItemStack());
        if (items.isEmpty()) {
            event.getItemDrop().remove();
        } else {
            event.getItemDrop().setItemStack(items.getFirst());
        }
    }

    @EventHandler
    public void onDrop(BlockDropItemEvent event) {
        for (Item itemDrop : event.getItems()) {
            List<ItemStack> items = this.manager.addItemsToChest(itemDrop.getLocation().getChunk(),itemDrop.getItemStack());
            if (items.isEmpty()) {
                itemDrop.remove();
            } else {
                itemDrop.setItemStack(items.getFirst());
            }
        }
    }
}
