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
        Map<Integer, ItemStack> items = contentButtons
                .stream()
                .flatMap(contentButton -> contentButton.getSlots()
                        .stream()
                        .map(slotInner -> new AbstractMap.SimpleEntry<>(slotInner, inventory.getInventory().getItem(slotInner))))
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (items.isEmpty()) {
            return;
        }
        Map<Integer,ItemStack> compressedItems = this.plugin.getManager(StoragePlusManager.class).compress(items, availableMaterials);
        compressedItems.forEach((slotInner, itemStack) -> inventory.getInventory().setItem(slotInner, itemStack));
    }
}
