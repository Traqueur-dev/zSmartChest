package fr.traqueur.storageplus;

import fr.traqueur.storageplus.api.StoragePlusManager;
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
            this.manager.placeChest(event.getPlayer(), event.getBlockPlaced().getLocation(), chest, item);
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.manager.breakChest(event, event.getBlock().getLocation());
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
        ItemStack item = this.manager.addItemsToChest(event.getItemDrop().getLocation().getChunk(), event.getItemDrop().getItemStack());
        if (item == null) {
            event.getItemDrop().remove();
        } else {
            event.getItemDrop().setItemStack(item);
        }
    }


    @EventHandler
    public void onDrop(EntityDropItemEvent event) {
        ItemStack item = this.manager.addItemsToChest(event.getItemDrop().getLocation().getChunk(), event.getItemDrop().getItemStack());
        if (item == null) {
            event.getItemDrop().remove();
        } else {
            event.getItemDrop().setItemStack(item);
        }
    }

    @EventHandler
    public void onDrop(BlockDropItemEvent event) {
        for (Item itemDrop : event.getItems()) {
            ItemStack item = this.manager.addItemsToChest(itemDrop.getLocation().getChunk(), itemDrop.getItemStack());
            if (item == null) {
                itemDrop.remove();
            } else {
                itemDrop.setItemStack(item);
            }
        }
    }
}
