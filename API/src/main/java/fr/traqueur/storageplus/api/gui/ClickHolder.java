package fr.traqueur.storageplus.api.gui;

import fr.groupez.api.zcore.CompatibilityUtil;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import fr.traqueur.storageplus.api.domains.PlacedChestContent;
import fr.traqueur.storageplus.api.domains.StorageItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

    public void handleLeftClick(InventoryClickEvent event, Player player, ItemStack cursor, int slot, PlacedChest chest, PlacedChestContent vault, int page, int inventorySize) {
        int slotInInventory = slot;
        slot = slot + ((page-1) * inventorySize);
        int finalSlot = slot;
        StorageItem vaultItem = vault.content().stream().filter(item -> item.slot() == finalSlot).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slot));
        if(chest.getChestTemplate().isInfinite()) {
            ItemStack current = vaultItem.item();
            if(cursor == null || cursor.getType().isAir() && !vaultItem.isEmpty()) {
                int amountToRemove = Math.min(vaultItem.amount(), current.getMaxStackSize());
                this.removeItem(event, player, slot, vault, chest, vaultItem, amountToRemove, inventorySize, page);
            } else if(!cursor.getType().isAir()) {
                int slotToAdd = this.findCorrespondingSlot(event.getInventory(), cursor, vault, chest);
                if(slotToAdd == -1) {
                    return;
                }
                vaultItem = vault.content().stream().filter(item -> item.slot() == slotToAdd).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slotToAdd));
                this.addItem(event, player, slotToAdd, vault, chest, vaultItem, cursor, cursor.getAmount(), inventorySize, page);
            }
        } else {
            InventoryAction action = event.getAction();
            switch (action) {
                case PLACE_ALL -> {
                    var newStorageItem = this.addToStorageItem(player, chest, vault, vaultItem, this.plugin.getManager(StoragePlusManager.class).cloneItemStack(cursor), cursor.getAmount(), event, inventorySize, page);
                    event.getInventory().setItem(slotInInventory, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                    CompatibilityUtil.setCursor(event, new ItemStack(Material.AIR));
                }

                case PLACE_SOME -> {
                    int amountToAdd = cursor.getAmount();
                    int newAmount = Math.min(vaultItem.item().getMaxStackSize(), vaultItem.amount() + amountToAdd);
                    int restInCursor = amountToAdd - (newAmount - vaultItem.amount());
                    var newStorageItem = this.addToStorageItem(player, chest, vault, vaultItem, this.plugin.getManager(StoragePlusManager.class).cloneItemStack(cursor), newAmount - vaultItem.amount(), event, inventorySize, page);
                    event.getInventory().setItem(slotInInventory, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                    ItemStack newCursor = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(cursor);
                    if(restInCursor == 0) {
                        newCursor = new ItemStack(Material.AIR);
                    } else {
                        newCursor.setAmount(restInCursor);
                    }
                    CompatibilityUtil.setCursor(event,newCursor);
                }

                case SWAP_WITH_CURSOR -> {
                    this.switchWithCursor(event, player, cursor, slotInInventory, chest, vault, vaultItem, inventorySize, page);
                }

                case PICKUP_ALL -> {
                    var newStorageItem = this.removeFromStorageItem(vault, vaultItem, vaultItem.amount());
                    event.getInventory().setItem(slotInInventory, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                    ItemStack toAdd = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
                    toAdd.setAmount(vaultItem.amount());
                    CompatibilityUtil.setCursor(event,toAdd);
                }
            }
        }
    }

    
    public void handleRightClick(InventoryClickEvent event, Player player, ItemStack cursor, int slot, PlacedChestContent vault, PlacedChest chest, int page, int inventorySize) {
        int slotInInventory = slot;
        slot = slot + ((page-1) * inventorySize);
        int finalSlot = slot;
        StorageItem vaultItem = vault.content().stream().filter(item -> item.slot() == finalSlot).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slot));
        if (chest.getChestTemplate().isInfinite()) {
            if(cursor == null || cursor.getType().isAir() && !vaultItem.isEmpty()) {
                int amountToRemove = Math.min(vaultItem.amount() / 2, vaultItem.item().getMaxStackSize() / 2);
                if(amountToRemove == 0) {
                    amountToRemove = 1;
                }
                this.removeItem(event, player, slot, vault, chest, vaultItem, amountToRemove, inventorySize, page);
            } else if(!cursor.getType().isAir()) {
                int slotToAdd = this.findCorrespondingSlot(event.getInventory(), cursor, vault, chest);
                if(slotToAdd == -1) {
                    return;
                }
                vaultItem = vault.content().stream().filter(item -> item.slot() == slotToAdd).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slotToAdd));
                this.addItem(event, player, slotToAdd, vault, chest, vaultItem, cursor, 1, inventorySize, page);
            }
        } else {
            InventoryAction action = event.getAction();
            switch (action) {
                case SWAP_WITH_CURSOR -> {
                    this.switchWithCursor(event, player, cursor, slotInInventory, chest,vault, vaultItem, inventorySize, page);
                }

                case PICKUP_HALF -> {
                    int halfAmount = vaultItem.amount() / 2;
                    if(halfAmount == 0) {
                        halfAmount = 1;
                    }
                    var newStorageItem = this.removeFromStorageItem(vault, vaultItem, halfAmount);
                    event.getInventory().setItem(slotInInventory, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                    ItemStack toAdd = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
                    toAdd.setAmount(halfAmount);
                    CompatibilityUtil.setCursor(event,toAdd);
                }
                case PLACE_ONE -> {
                    var newStorageItem = this.addToStorageItem(player, chest, vault, vaultItem, this.plugin.getManager(StoragePlusManager.class).cloneItemStack(cursor), 1, event, inventorySize, page);
                    event.getInventory().setItem(slotInInventory, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                    ItemStack newCursor = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(cursor);
                    if(cursor.getAmount() - 1 == 0) {
                        newCursor = new ItemStack(Material.AIR);
                    } else {
                        newCursor.setAmount(cursor.getAmount() - 1);
                    }
                    CompatibilityUtil.setCursor(event,newCursor);
                }
            }
        }
    }

    
    public void handleShift(InventoryClickEvent event, Player player, ItemStack cursor, ItemStack current, int slot, int inventorySize, PlacedChestContent vault, PlacedChest chest, List<Integer> slots, int page) {
        int slotInInventory = slot;
        slot = slot + ((page-1) * inventorySize);
        if(chest.getChestTemplate().isInfinite()) {
            if (slotInInventory >= inventorySize) {
                if(cursor == null || cursor.getType().isAir() && current == null || current.getType().isAir()) {
                    return;
                }
                int slotToAdd = this.findCorrespondingSlot(event.getInventory(), current, vault, chest);
                if(slotToAdd == -1) {
                    return;
                }
                StorageItem vaultItem = vault.content().stream().filter(item -> item.slot() == slotToAdd).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slotToAdd));
                var newStorageItem = this.addToStorageItem(player, chest, vault, vaultItem, current, current.getAmount(), event, inventorySize, page);
                event.getInventory().setItem(slotToAdd - ((page-1)*inventorySize), newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                event.setCurrentItem(new ItemStack(Material.AIR));
            } else {
                this.shiftClickFromPlacedChestContent(event, player, cursor, current, slot, vault, chest, inventorySize, page);
            }
        } else {
            if (slotInInventory >= inventorySize) {
                if(cursor == null || cursor.getType().isAir() && current == null || current.getType().isAir()) {
                    return;
                }
                var virtualInv = Bukkit.createInventory(null, inventorySize, "virtual_inv");
                virtualInv.setContents(event.getInventory().getContents());
                var rest = virtualInv.addItem(current);
                for (int i : slots) {
                    ItemStack virtual = virtualInv.getItem(i);
                    ItemStack real = event.getInventory().getItem(i);
                    if(this.isDifferent(virtual, real, true)) {
                        var newStorageItem = this.addToStorageItem(player, chest, vault, new StorageItem(new ItemStack(Material.AIR), 1, i + ((page-1)*inventorySize)), virtual, virtual.getAmount(), event, inventorySize, page);
                        event.getInventory().setItem(i, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                    }
                }
                int newCurrentAmount = rest.values().stream().mapToInt(ItemStack::getAmount).sum();
                ItemStack newCurrent = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(current);
                if(newCurrentAmount == 0) {
                    newCurrent = new ItemStack(Material.AIR);
                } else {
                    newCurrent.setAmount(newCurrentAmount);
                }
                player.getInventory().setItem(event.getSlot(), newCurrent);
            } else {
                this.shiftClickFromPlacedChestContent(event, player, cursor, current, slot, vault, chest, inventorySize, page);
            }

        }
    }

    
    public void handleDrop(InventoryClickEvent event, Player player, int slot, PlacedChestContent vault, PlacedChest chest, boolean controlDrop, int page, int inventorySize) {
        int finalSlot = slot + ((page-1) * inventorySize);
        StorageItem vaultItem = vault.content().stream().filter(item -> item.slot() == finalSlot).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, finalSlot));
        if(vaultItem.isEmpty()) {
            return;
        }
        int amountToDrop;
        if(controlDrop) {
            amountToDrop = Math.min(vaultItem.amount(), vaultItem.item().getMaxStackSize());
        } else {
            amountToDrop = 1;
        }

        StorageItem newStorageItem = this.removeFromStorageItem(vault, vaultItem, amountToDrop);
        event.getInventory().setItem(slot, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
        ItemStack item = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
        item.setAmount(amountToDrop);
        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

    
    public void handleNumberKey(InventoryClickEvent event, Player player, int slot, PlacedChestContent vault, PlacedChest chest, int page, int inventorySize) {
        int finalSlot = slot + ((page-1) * inventorySize);
        ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
        StorageItem vaultItem = vault.content().stream().filter(item -> item.slot() == finalSlot).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, finalSlot));

        if(chest.getChestTemplate().isInfinite()) {
            if(vaultItem.isEmpty() && hotbarItem != null && !hotbarItem.getType().isAir()) {
                this.addFromHotbar(event, player, chest, vault, hotbarItem, inventorySize, page);
            } else if(!vaultItem.isEmpty() && (hotbarItem == null || hotbarItem.getType().isAir())) {
                int amount = Math.min(vaultItem.amount(), vaultItem.item().getMaxStackSize());
                ItemStack toAdd = vaultItem.item().clone();
                toAdd.setAmount(amount);
                player.getInventory().setItem(event.getHotbarButton(), toAdd);
                var newStorageItem = this.removeFromStorageItem(vault, vaultItem, amount);
                event.getInventory().setItem(slot, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
            } else if (hotbarItem != null && vaultItem.item().isSimilar(hotbarItem)) {
                this.addFromHotbar(event, player, chest, vault, hotbarItem, inventorySize, page);
            }
        } else {
            if(vaultItem.isEmpty() && hotbarItem != null && !hotbarItem.getType().isAir()) {
                var newStorageItem = this.addToStorageItem(player, chest, vault, new StorageItem(new ItemStack(Material.AIR), 1, finalSlot), hotbarItem, hotbarItem.getAmount(),event, inventorySize, page);
                event.getInventory().setItem(slot, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                player.getInventory().setItem(event.getHotbarButton(), new ItemStack(Material.AIR));
            } else if(!vaultItem.isEmpty() && (hotbarItem == null || hotbarItem.getType().isAir())) {
                ItemStack newHotbarItem = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
                newHotbarItem.setAmount(vaultItem.amount());
                player.getInventory().setItem(event.getHotbarButton(), newHotbarItem);
                var newStorageItem = this.removeFromStorageItem(vault, vaultItem, vaultItem.amount());
                event.getInventory().setItem(slot, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
            } else if (hotbarItem != null && !this.isDifferent(this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item()), hotbarItem, false)) {
                int newAmount = Math.min(vaultItem.amount() + hotbarItem.getAmount(), vaultItem.item().getMaxStackSize());
                int rest = hotbarItem.getAmount() - (newAmount - vaultItem.amount());
                var newStorageItem = this.addToStorageItem(player, chest, vault, vaultItem, this.plugin.getManager(StoragePlusManager.class).cloneItemStack(hotbarItem), newAmount - vaultItem.amount(), event, inventorySize, page);
                event.getInventory().setItem(slot, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                ItemStack newHotbarItem = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(hotbarItem);
                if(rest == 0) {
                    newHotbarItem = new ItemStack(Material.AIR);
                } else {
                    newHotbarItem.setAmount(rest);
                }
                player.getInventory().setItem(event.getHotbarButton(), newHotbarItem);
            }
        }

    }

    private void shiftClickFromPlacedChestContent(InventoryClickEvent event, Player player, ItemStack cursor, ItemStack current, int slot, PlacedChestContent vault, PlacedChest chest, int inventorySize, int page) {
        if(cursor == null || cursor.getType().isAir() && current == null || current.getType().isAir()) {
            return;
        }

        StorageItem vaultItem = vault.content().stream().filter(item -> item.slot() == slot).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slot));
        int removeAmount = Math.min(vaultItem.amount(), current.getMaxStackSize());
        ItemStack toAdd = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
        toAdd.setAmount(removeAmount);
        var rest = player.getInventory().addItem(toAdd);
        if(!rest.isEmpty()) {
            removeAmount -= rest.values().stream().mapToInt(ItemStack::getAmount).sum();
        }
        var newStorageItem = this.removeFromStorageItem(vault, vaultItem, removeAmount);
        event.getInventory().setItem(slot - ((page-1)*inventorySize), newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
    }

    private void switchWithCursor(InventoryClickEvent event, Player player, ItemStack cursor, int slot, PlacedChest chest, PlacedChestContent vault, StorageItem vaultItem, int inventorySize, int page) {
        var newStorageItem = this.addToStorageItem(player, chest, vault, new StorageItem(new ItemStack(Material.AIR), 1, slot + ((page-1)*inventorySize)), cursor, cursor.getAmount(), event, inventorySize, page);
        event.getInventory().setItem(slot, newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
        ItemStack toAdd = this.plugin.getManager(StoragePlusManager.class).cloneItemStack(vaultItem.item());
        toAdd.setAmount(vaultItem.amount());
        CompatibilityUtil.setCursor(event,toAdd);
    }

    private void addFromHotbar(InventoryClickEvent event, Player player, PlacedChest chest, PlacedChestContent vault, ItemStack hotbarItem, int inventorySize, int page) {
        StorageItem vaultItem;
        int slotToAdd = this.findCorrespondingSlot(event.getInventory(), hotbarItem, vault, chest);
        vaultItem = vault.content().stream().filter(item -> item.slot() == slotToAdd).findFirst().orElse(new StorageItem(new ItemStack(Material.AIR), 1, slotToAdd));
        var newStorageItem = this.addToStorageItem(player, chest, vault, vaultItem, hotbarItem, hotbarItem.getAmount(), event, inventorySize, page);
        int itemPage = (slotToAdd / inventorySize) + 1;
        if(itemPage == page) {
            event.getInventory().setItem(slotToAdd- ((page-1)*inventorySize), newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
        }
        player.getInventory().setItem(event.getHotbarButton(), new ItemStack(Material.AIR));
    }

    private void addItem(InventoryClickEvent event, Player player, int slot, PlacedChestContent vault, PlacedChest chest, StorageItem vaultItem, ItemStack cursor, int amountToAdd, int inventorySize, int page) {
        var newStorageItem = this.addToStorageItem(player, chest, vault, vaultItem, cursor, amountToAdd, event, inventorySize, page);

        int itemPage = (slot / inventorySize) + 1;
        if(itemPage == page) {
            event.getInventory().setItem(slot - ((page-1)*inventorySize), newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
        }
        int newAmount = cursor.getAmount() - amountToAdd;
        if(newAmount == 0) {
            CompatibilityUtil.setCursor(event,new ItemStack(Material.AIR));
            return;
        }
        cursor.setAmount(newAmount);
        CompatibilityUtil.setCursor(event,cursor);
    }

    private void removeItem(InventoryClickEvent event, Player player, int slot, PlacedChestContent vault, PlacedChest chest, StorageItem vaultItem, int amountToRemove, int inventorySize, int page) {
        var newStorageItem = this.removeFromStorageItem(vault, vaultItem, amountToRemove);
        int itemPage = (slot / inventorySize) + 1;
        if(itemPage == page) {
            event.getInventory().setItem(slot - ((page-1)*inventorySize), newStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
        }
        ItemStack newCursor = newStorageItem.isEmpty() ? vaultItem.item().clone() : newStorageItem.item().clone();
        newCursor.setAmount(amountToRemove);
        CompatibilityUtil.setCursor(event,newCursor);
    }

    private int findCorrespondingSlot(Inventory inventory, ItemStack correspond, PlacedChestContent vault, PlacedChest chest) {
        for (StorageItem vaultItem : vault.content()) {
            if(correspond.isSimilar(vaultItem.item()) && vaultItem.amount() < this.plugin.getManager(StoragePlusManager.class).getMaxStackSize(chest, vaultItem.item())) {
                return vaultItem.slot();
            }
        }
        return inventory.firstEmpty();
    }

    private StorageItem removeFromStorageItem(PlacedChestContent vault, StorageItem vaultItem, int amount) {
        int currentAmount = vaultItem.amount();
        StorageItem newStorageItem;
        if(currentAmount - amount == 0) {
            newStorageItem = new StorageItem(new ItemStack(Material.AIR), 1, vaultItem.slot());
        } else {
            newStorageItem = new StorageItem(vaultItem.item(), currentAmount - amount, vaultItem.slot());
        }
        vault.setContent(vault.content().stream().map(item -> item.slot() == newStorageItem.slot() ? newStorageItem : item).collect(Collectors.toList()));
        return newStorageItem;
    }

    private StorageItem addToStorageItem(Player player, PlacedChest chest, PlacedChestContent content, StorageItem vaultItem, ItemStack cursor, int amount, InventoryClickEvent event, int inventorySize, int page) {
        int maxStackSize = this.plugin.getManager(StoragePlusManager.class).getMaxStackSize(chest, cursor);
        int remainingAmount = amount;
        int currentAmount = vaultItem.isEmpty() ? 0 : vaultItem.amount();

        // Add to current slot up to max stack size
        int amountToAddInCurrent = Math.min(maxStackSize - currentAmount, remainingAmount);
        StorageItem newStorageItem = new StorageItem(vaultItem.isEmpty() ? cursor : vaultItem.item(), currentAmount + amountToAddInCurrent, vaultItem.slot());
        plugin.getManager(StoragePlusManager.class).setContent(chest, content.content().stream().map(v -> v.slot() == newStorageItem.slot() ? newStorageItem : v).collect(Collectors.toList()));
        remainingAmount -= amountToAddInCurrent;
        if (remainingAmount <= 0) {
            return newStorageItem;
        }

        // Add remaining amount in other empty slots
        for (StorageItem slot : content.content()) {
            if (slot.isEmpty()) {
                int amountToAdd = Math.min(maxStackSize, remainingAmount);
                StorageItem additionalStorageItem = new StorageItem(cursor, amountToAdd, slot.slot());
                plugin.getManager(StoragePlusManager.class).setContent(chest,content.content().stream().map(v -> v.slot() == additionalStorageItem.slot() ? additionalStorageItem : v).collect(Collectors.toList()));

                int itemPage = (slot.slot() / inventorySize) + 1;
                if(itemPage == page) {
                    event.getInventory().setItem(slot.slot() - ((page-1)*inventorySize), additionalStorageItem.toItem(player, chest.getChestTemplate().isInfinite()));
                }

                remainingAmount -= amountToAdd;
                if (remainingAmount <= 0) break;
            }
        }

        return newStorageItem;
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

}
