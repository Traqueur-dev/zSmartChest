package fr.traqueur.storageplugs.api.gui.buttons;

import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import fr.traqueur.storageplugs.api.functions.ItemTransformationFunction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class MaterialAuthorizedButton extends ZButton {

    protected final StoragePlusPlugin plugin;
    protected final List<Material> availableMaterials;

    public MaterialAuthorizedButton(StoragePlusPlugin plugin, List<Material> availableMaterials) {
        this.plugin = plugin;
        this.availableMaterials = availableMaterials;
    }

    protected void click(InventoryDefault inventory, ItemTransformationFunction function) {
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
        List<ItemStack> newItems = function.transfrom(items, availableMaterials);
        for (int i = 0; i < newItems.size(); i++) {
            inventory.getInventory().setItem(new ArrayList<>(contentButton.getSlots()).get(i), newItems.get(i));
        }
    }

}
