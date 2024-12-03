package fr.traqueur.storageplus.api.gui.buttons;

import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import fr.traqueur.storageplus.api.domains.PlacedChestContent;
import fr.traqueur.storageplus.api.domains.StorageItem;
import fr.traqueur.storageplus.api.gui.ClickHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ZChestContentButton extends ZButton implements PaginateButton {

    private final StoragePlusPlugin plugin;
    private final ClickHolder holder;

    public ZChestContentButton(Plugin plugin) {
        this.plugin = ((StoragePlusPlugin) plugin);
        this.holder = new ClickHolder(this.plugin);
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        displayItems(player, inventory);
    }

    private void displayItems(Player player, InventoryDefault inventory) {
        StoragePlusManager manager = plugin.getManager(StoragePlusManager.class);
        PlacedChest chest = manager.getOpenedChest(player);
        List<StorageItem> content = manager.getContent(chest).content();
        for (int slot : this.slots) {
            int calculatedSlot = slot + (inventory.getMenuInventory().size() * (inventory.getPage()-1));
            AtomicReference<ItemStack> item = new AtomicReference<>();
            content.stream().filter(storageItem -> storageItem.slot() == calculatedSlot).findFirst().ifPresentOrElse(storageItem -> {
                item.set(storageItem.toItem(player, chest.getChestTemplate().isInfinite()));
            }, () -> {
                StorageItem newItem = new StorageItem(new ItemStack(Material.AIR), 1, calculatedSlot);
                item.set(newItem.toItem(player, chest.getChestTemplate().isInfinite()));
                content.add(newItem);
            });
            inventory.addItem(slot, item.get()).setClick(event -> event.setCancelled(true));
        }
        manager.setContent(chest, content);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, Player player, InventoryDefault inventoryDefault) {
        PlacedChest chest = this.plugin.getManager(StoragePlusManager.class).getOpenedChest(player);
        PlacedChestContent vault = this.plugin.getManager(StoragePlusManager.class).getContent(chest);
        ClickType clickType = event.getClick();
        ItemStack cursor = event.getCursor();
        int slot = event.getRawSlot();
        int inventorySize = inventoryDefault.getSpigotInventory().getSize();
        int page = inventoryDefault.getPage();

        if(slot >= inventorySize && !clickType.isShiftClick() && clickType != ClickType.NUMBER_KEY || slot < 0) {
            return;
        }

        if(!this.slots.contains(slot) && slot < inventorySize) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if(chest.getChestTemplate().isInfinite()) {
            switch (clickType) {
                case LEFT -> this.holder.handleInfiniteLeftClick(event, player, cursor, slot, chest, vault, page, inventorySize);
                case RIGHT -> this.holder.handleInfiniteRightClick(event, player, cursor, slot, vault, chest, page, inventorySize);
                case SHIFT_LEFT, SHIFT_RIGHT -> this.holder.handleInfiniteShift(event, player, slot, inventorySize, vault, chest, this.slots, page);
                case DROP, CONTROL_DROP -> this.holder.handleInfiniteDrop(event, player, slot, vault, chest,clickType == ClickType.CONTROL_DROP, page, inventorySize);
                case NUMBER_KEY -> this.holder.handleInfiniteNumberKey(event, player, slot, vault, chest, page, inventorySize);
            }
        } else {
            switch (clickType) {
                case LEFT -> this.holder.handleLeftClick(event, player, cursor, slot, chest, vault, page, inventorySize);
                case RIGHT -> this.holder.handleRightClick(event, player, cursor, slot, vault, chest, page, inventorySize);
                case SHIFT_LEFT, SHIFT_RIGHT -> this.holder.handleShift(event, player, slot, inventorySize, vault, chest, this.slots, page);
                case DROP, CONTROL_DROP -> this.holder.handleDrop(event, player, slot, vault, chest,clickType == ClickType.CONTROL_DROP, page, inventorySize);
                case NUMBER_KEY -> this.holder.handleNumberKey(event, player, slot, vault, chest, page, inventorySize);
            }
        }
        this.plugin.getManager(StoragePlusManager.class).setContent(chest, vault.content());
    }

    @Override
    public void onDrag(InventoryDragEvent event, Player player, InventoryDefault inventoryDefault) {
        event.setCancelled(true);
    }

    @Override
    public int getPaginationSize(Player player) {
        PlacedChest chest = this.plugin.getManager(StoragePlusManager.class).getOpenedChest(player);
        PlacedChestContent content = this.plugin.getManager(StoragePlusManager.class).getContent(chest);
        if(chest.getChestTemplate().getMaxPages() != -1) {
            return chest.getChestTemplate().getMaxPages() * this.slots.size();
        }

        return content.content().size() + this.slots.size();
    }
}
