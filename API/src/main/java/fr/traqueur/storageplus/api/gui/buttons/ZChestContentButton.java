package fr.traqueur.storageplus.api.gui.buttons;

import fr.maxlego08.menu.api.utils.Placeholders;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ZChestContentButton extends ZButton {

    private final StoragePlusPlugin plugin;
    private final ClickHolder holder;

    public ZChestContentButton(Plugin plugin) {
        this.plugin = ((StoragePlusPlugin) plugin);
        this.holder = new ClickHolder(this.plugin);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryDefault inventory, Placeholders placeholders) {
        inventory.setDisablePlayerInventoryClick(false);
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        displayItems(player, inventory);
    }

    private void displayItems(Player player, InventoryDefault inventory) {
        StoragePlusManager manager = plugin.getManager(StoragePlusManager.class);
        PlacedChest chest = manager.getOpenedChest(player);
        List<StorageItem> items = manager.getContent(chest).content();
        for (int slot : this.slots) {
            AtomicReference<ItemStack> item = new AtomicReference<>();
            items.stream().filter(storageItem -> storageItem.slot() == slot).findFirst().ifPresentOrElse(storageItem -> {
                item.set(storageItem.toItem(player, chest.getChestTemplate().isInfinite()));
            }, () -> {
                StorageItem newItem = new StorageItem(new ItemStack(Material.AIR), 1, slot);
                item.set(newItem.toItem(player, chest.getChestTemplate().isInfinite()));
                items.add(newItem);
            });
            inventory.addItem(slot, item.get()).setClick(event -> event.setCancelled(true));
        }
        manager.setContent(chest, items);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, Player player, InventoryDefault inventoryDefault) {
        PlacedChest chest = this.plugin.getManager(StoragePlusManager.class).getOpenedChest(player);
        PlacedChestContent vault = this.plugin.getManager(StoragePlusManager.class).getContent(chest);
        ClickType clickType = event.getClick();
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        int slot = event.getRawSlot();
        int inventorySize = inventoryDefault.getSpigotInventory().getSize();

        if(slot >= inventorySize && !clickType.isShiftClick() && clickType != ClickType.DOUBLE_CLICK || slot < 0) {
            return;
        }

        event.setCancelled(true);

        switch (clickType) {
            case LEFT -> this.holder.handleLeftClick(event, player, cursor, slot, chest, vault);
            case RIGHT -> this.holder.handleRightClick(event, player, cursor, current, slot, inventorySize, vault, chest);
            case SHIFT_LEFT, SHIFT_RIGHT -> this.holder.handleShift(event, player, cursor, current, slot, inventorySize, vault, chest, this.slots);
            case DROP, CONTROL_DROP -> this.holder.handleDrop(event, player, cursor, current, slot, inventorySize, vault, chest,clickType == ClickType.CONTROL_DROP);
            case NUMBER_KEY -> this.holder.handleNumberKey(event, player, cursor, current, slot, inventorySize, vault,chest);
        }
    }
}
