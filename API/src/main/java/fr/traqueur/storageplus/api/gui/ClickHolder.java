package fr.traqueur.storageplus.api.gui;

import fr.groupez.api.zcore.CompatibilityUtil;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import fr.traqueur.storageplus.api.domains.PlacedChestContent;
import fr.traqueur.storageplus.api.domains.StorageItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class ClickHolder {

    private final StoragePlusPlugin plugin;

    public ClickHolder(StoragePlusPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleLeftClick(InventoryClickEvent event, Player player, ItemStack cursor, int slot, PlacedChest chest, PlacedChestContent content, int page, int inventorySize) {
        StoragePlusManager manager = this.plugin.getManager(StoragePlusManager.class);
        int slotInInventory = slot;
        slot = slot + ((page-1) * inventorySize);
        StorageItem vaultItem = this.getItemIntSlot(content, slot);
        InventoryAction action = event.getAction();
        ItemStack current = vaultItem.item().clone();
        switch (action) {
            case PLACE_ALL,PLACE_SOME, SWAP_WITH_CURSOR -> {
                int leftToAdd = this.addItemInChest(content, cursor, cursor.getAmount(), chest, page, event.getInventory(), player);
                if(leftToAdd == 0) {
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                } else {
                    cursor.setAmount(leftToAdd);
                    player.setItemOnCursor(cursor);
                }
            }
            case PICKUP_ALL -> {
                int amountToRemove = Math.min(vaultItem.amount(), current.getMaxStackSize());
                vaultItem.removeAmount(amountToRemove);
                if(vaultItem.isEmpty()) {
                    event.getInventory().setItem(slotInInventory, new ItemStack(Material.AIR));
                } else {
                    event.getInventory().setItem(slotInInventory, vaultItem.toItem(player, chest.getChestTemplate().isInfinite()));
                }
                var itemCursor = manager.cloneItemStack(current);
                itemCursor.setAmount(amountToRemove);
                player.setItemOnCursor(itemCursor);
            }
        }
    }
    
    public void handleRightClick(InventoryClickEvent event, Player player, ItemStack cursor, int slot, PlacedChestContent content, PlacedChest chest, int page, int inventorySize) {
        int slotInInventory = slot;
        slot = slot + ((page-1) * inventorySize);
        StorageItem vaultItem =  this.getItemIntSlot(content, slot);
        InventoryAction action = event.getAction();
        switch (action) {
            case PICKUP_HALF -> {
                int amountToRemove = Math.min(vaultItem.item().getMaxStackSize() / 2, Math.min(vaultItem.amount(), vaultItem.amount() / 2));
                if(vaultItem.amount() == 1) {
                    amountToRemove = 1;
                }
                vaultItem.removeAmount(amountToRemove);
                if(vaultItem.isEmpty()) {
                    event.getInventory().setItem(slotInInventory, new ItemStack(Material.AIR));
                } else {
                    event.getInventory().setItem(slotInInventory, vaultItem.toItem(player, chest.getChestTemplate().isInfinite()));
                }
                var itemCursor = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
                itemCursor.setAmount(amountToRemove);
                player.setItemOnCursor(itemCursor);
            }
            case PLACE_ONE, SWAP_WITH_CURSOR -> {
                int leftToAdd = this.addItemInChest(content, cursor, 1, chest, page, event.getInventory(), player);
                int cursorAmount = cursor.getAmount() - 1 + leftToAdd;
                if(cursorAmount == 0) {
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                } else {
                    cursor.setAmount(cursorAmount);
                    player.setItemOnCursor(cursor);
                }
            }
        }
    }

    
    public void handleShift(InventoryClickEvent event, Player player, int slot, int inventorySize, PlacedChestContent vault, PlacedChest chest, List<Integer> slots, int page) {
        int slotInInventory = slot;
        slot = slot + ((page-1) * inventorySize);
        boolean isTop = slotInInventory < inventorySize;
        StoragePlusManager manager = this.plugin.getManager(StoragePlusManager.class);
        if(isTop) {
            StorageItem item = this.getItemIntSlot(vault, slot);
            ItemStack clone = manager.cloneItemStack(item.item());
            int amountToRemove = Math.min(item.amount(), item.item().getMaxStackSize());
            item.removeAmount(amountToRemove);
            if(item.isEmpty()) {
                event.getInventory().setItem(slotInInventory, new ItemStack(Material.AIR));
            } else {
                event.getInventory().setItem(slotInInventory, item.toItem(player, chest.getChestTemplate().isInfinite()));
            }
            clone.setAmount(amountToRemove);
            var left = player.getInventory().addItem(clone);
            if(!left.isEmpty()) {
                left.forEach((integer, itemStack) -> {
                    player.getWorld().dropItem(player.getLocation(), itemStack);
                });
            }
        } else {
            if(event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
                return;
            }
            int leftToAdd = this.addItemInChest(vault, event.getCurrentItem(), event.getCurrentItem().getAmount(), chest, page, event.getInventory(), player);
            if(leftToAdd == 0) {
                player.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
            } else {
                event.getCurrentItem().setAmount(leftToAdd);
                player.getInventory().setItem(event.getSlot(), event.getCurrentItem());
            }
        }
    }

    
    public void handleDrop(InventoryClickEvent event, Player player, int slot, PlacedChestContent vault, PlacedChest chest, boolean controlDrop, int page, int inventorySize) {
        int slotInInventory = slot;
        int finalSlot = slot + ((page-1) * inventorySize);
        StorageItem vaultItem = this.getItemIntSlot(vault, finalSlot);
        if(vaultItem.isEmpty()) {
            return;
        }
        int amountToDrop;
        if(controlDrop) {
            amountToDrop = Math.min(vaultItem.amount(), vaultItem.item().getMaxStackSize());
        } else {
            amountToDrop = 1;
        }
        ItemStack clone = plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
        vaultItem.removeAmount(amountToDrop);
        if(vaultItem.isEmpty()) {
            event.getInventory().setItem(slotInInventory, new ItemStack(Material.AIR));
        } else {
            event.getInventory().setItem(slotInInventory, vaultItem.toItem(player, chest.getChestTemplate().isInfinite()));
        }
        while (amountToDrop > 0) {
            int amount = Math.min(amountToDrop, clone.getMaxStackSize());
            clone.setAmount(amount);
            player.getWorld().dropItem(player.getLocation(), clone);
            amountToDrop -= amount;
        }
        
    }

    
    public void handleNumberKey(InventoryClickEvent event, Player player, int slot, PlacedChestContent vault, PlacedChest chest, int page, int inventorySize) {
        int finalSlot = slot + ((page-1) * inventorySize);
        ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
        StorageItem vaultItem = vault.content().stream().filter(item -> item.slot() == finalSlot).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, finalSlot));

        if(chest.getChestTemplate().isInfinite()) {
            if(vaultItem.isEmpty() && hotbarItem != null && !hotbarItem.getType().isAir()) {

            } else if(!vaultItem.isEmpty() && (hotbarItem == null || hotbarItem.getType().isAir())) {
                int amount = Math.min(vaultItem.amount(), plugin.getManager(StoragePlusManager.class).getMaxStackSize(chest, vaultItem.item()));

            } else if (hotbarItem != null && vaultItem.item().isSimilar(hotbarItem)) {

            }
        } else {
            if(vaultItem.isEmpty() && hotbarItem != null && !hotbarItem.getType().isAir()) {

            } else if(!vaultItem.isEmpty() && (hotbarItem == null || hotbarItem.getType().isAir())) {

            } else if (hotbarItem != null && !this.isDifferent(this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item()), hotbarItem, false)) {

            }
        }

    }

    private int findCorrespondingSlot(ItemStack correspond, PlacedChestContent content, PlacedChest chest) {
        for (StorageItem vaultItem : content.content()) {
            if(correspond.isSimilar(vaultItem.item()) && vaultItem.amount() < this.plugin.getManager(StoragePlusManager.class).getMaxStackSize(chest, vaultItem.item())) {
                return vaultItem.slot();
            }
        }
        return content.content().stream().filter(StorageItem::isEmpty).findFirst().map(StorageItem::slot).orElse(-1);
    }


    private boolean isDifferent(ItemStack item1, ItemStack item2, boolean checkAmount) {
        if (item1 == null && item2 == null) {
            return false;
        }

        if (item1 == null || item2 == null) {
            return true;
        }

        if (item1.getType() != item2.getType()) {
            return true;
        }

        if(checkAmount) {
            if (item1.getAmount() != item2.getAmount()) {
                return true;
            }
        }

        if (!item1.hasItemMeta() && !item2.hasItemMeta()) {
            return false;
        }

        if (item1.hasItemMeta() != item2.hasItemMeta()) {
            return true;
        }

        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            if (!item1.getItemMeta().equals(item2.getItemMeta())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add an item in the chest
     * @param content the content of the chest
     * @param item the item to add
     * @param amount the amount of the item
     * @param chest the chest
     * @return the amount left to add
     */
    private int addItemInChest(PlacedChestContent content, ItemStack item, int amount, PlacedChest chest, int page, Inventory chestInventory, Player player) {
        StoragePlusManager manager = this.plugin.getManager(StoragePlusManager.class);
        int leftAmount = amount;
        while (leftAmount > 0) {
            int slotToAdd = this.findCorrespondingSlot(item,content, chest);
            if(slotToAdd == -1) {
                return leftAmount;
            }
            StorageItem storageItem = this.getItemIntSlot(content, slotToAdd);
            int maxStackSize = manager.getMaxStackSize(chest, item);
            int amountToAdd = Math.min(leftAmount, maxStackSize - storageItem.amount());
            if(storageItem.isEmpty()) {
                storageItem.setItem(item.clone());
            }
            storageItem.addAmount(amountToAdd);
            if(this.getPageFromSlot(slotToAdd, chestInventory.getSize()) == page) {
                chestInventory.setItem(getRawSlotFromSlot(slotToAdd, page, chestInventory.getSize()), storageItem.toItem(player, chest.getChestTemplate().isInfinite()));
            }
            leftAmount -= amountToAdd;
        }
        return leftAmount;
    }

    private StorageItem getItemIntSlot(PlacedChestContent content, int slot) {
        return content.content().stream().filter(storageItem -> storageItem.slot() == slot).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slot));
    }

    private int getRawSlotFromSlot(int slot, int page, int inventorySize) {
        return slot - ((page - 1) * inventorySize);
    }

    private int getPageFromSlot(int slot, int inventorySize) {
        return (slot / inventorySize) + 1;
    }
}
