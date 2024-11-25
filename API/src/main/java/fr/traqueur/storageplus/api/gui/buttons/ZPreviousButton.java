package fr.traqueur.storageplus.api.gui.buttons;

import fr.maxlego08.menu.api.button.buttons.NextButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ZPreviousButton extends ZButton implements NextButton {

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        if (inventory.getPage() != 1) {
            var manager = JavaPlugin.getPlugin(StoragePlusPlugin.class).getManager(StoragePlusManager.class);
            PlacedChest chest = manager.getOpenedChest(player);
            manager.openChest(player, chest, event.isLeftClick() ? inventory.getPage() - 1 : 1, false);
        }
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        return inventory.getPage() != 1;
    }

}
