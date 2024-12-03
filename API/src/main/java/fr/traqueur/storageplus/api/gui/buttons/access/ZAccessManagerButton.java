package fr.traqueur.storageplus.api.gui.buttons.access;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.messaging.Messages;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.access.AccessManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ZAccessManagerButton extends ZButton {

    private final StoragePlusPlugin plugin;

    public ZAccessManagerButton(Plugin plugin) {
        this.plugin = (StoragePlusPlugin) plugin;
    }

    @Override
    public void onRightClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
        super.onRightClick(player, event, inventory, slot);
        //handle managing access
    }

    @Override
    public void onLeftClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
        super.onLeftClick(player, event, inventory, slot);
        var chest = this.plugin.getManager(StoragePlusManager.class).getOpenedChest(player);
        this.plugin.getManager(AccessManager.class).addPending(player.getUniqueId(), chest.getUniqueId());
        Messages.START_ACCESS_REQUEST.send(player, Formatter.format("%cancel_word%", Configuration.get(MainConfiguration.class).getAccessManagingCancelWords().getFirst()));
        player.closeInventory();
    }
}
