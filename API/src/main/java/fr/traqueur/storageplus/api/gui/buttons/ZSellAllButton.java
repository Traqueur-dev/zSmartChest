package fr.traqueur.storageplus.api.gui.buttons;

import fr.groupez.api.messaging.Messages;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ZSellAllButton extends ZButton {

    private StoragePlusPlugin plugin;

     public ZSellAllButton(Plugin plugin) {
         this.plugin = (StoragePlusPlugin) plugin;
     }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        var manager = this.plugin.getManager(StoragePlusManager.class);
        manager.sell(manager.getOpenedChest(player));
        Messages.SELL_ALL.send(player);
    }
}
