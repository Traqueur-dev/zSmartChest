package fr.traqueur.storageplugs.api.gui.buttons;

import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class ZCompressorButton extends ZButton {

    private final StoragePlusPlugin plugin;
    private final List<Material> availableMaterials;

    public ZCompressorButton(Plugin plugin, List<Material> availableMaterials) {
        this.plugin = (StoragePlusPlugin) plugin;
        this.availableMaterials = availableMaterials;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        List<ZChestContentButton> contentButtons = inventory.getButtons().stream().filter(button -> button instanceof ZChestContentButton).map(button -> (ZChestContentButton) button).toList();
        if(contentButtons.size() != 1) {
            throw new IllegalStateException("There should be only one ZChestContentButton in the inventory");
        }
        ZChestContentButton contentButton = contentButtons.getFirst();
        List<ItemStack> items = contentButton.getSlots()
                .stream()
                .map(slotInner -> {
                    ItemStack item = inventory.getInventory().getItem(slotInner);
                    if (item == null) {
                        return null;
                    }
                    item = item.clone();
                    inventory.getInventory().setItem(slotInner, null);
                    return item;
                })
                .filter(Objects::nonNull)
                .toList();
        if (items.isEmpty()) {
            return;
        }
        List<ItemStack> compressedItems = this.plugin.getManager(StoragePlusManager.class).compress(items, availableMaterials);
        for (int i = 0; i < compressedItems.size(); i++) {
            inventory.getInventory().setItem(new ArrayList<>(contentButton.getSlots()).get(i), compressedItems.get(i));
        }
    }
}
