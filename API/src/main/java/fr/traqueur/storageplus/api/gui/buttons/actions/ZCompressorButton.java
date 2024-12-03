package fr.traqueur.storageplus.api.gui.buttons.actions;

import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.currencies.Currencies;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ZCompressorButton extends MaterialAuthorizedButton {


    public ZCompressorButton(StoragePlusPlugin plugin, List<Material> availableMaterials, double amount, Currencies currency, String currencyName) {
        super(plugin, availableMaterials, amount, currency, currencyName);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        this.click(player, inventory, this.plugin.getManager(StoragePlusManager.class)::compress);
    }
}
