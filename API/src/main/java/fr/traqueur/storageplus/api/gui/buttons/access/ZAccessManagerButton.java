package fr.traqueur.storageplus.api.gui.buttons.access;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.messaging.Messages;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.access.AccessManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ZAccessManagerButton extends ZButton {

    private final StoragePlusPlugin plugin;

    public ZAccessManagerButton(Plugin plugin) {
        this.plugin = (StoragePlusPlugin) plugin;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        var chest = this.plugin.getManager(StoragePlusManager.class).getOpenedChest(player);
        this.plugin.getManager(AccessManager.class).addPending(player.getUniqueId(), chest);
        ClickType clickType = event.getClick();
        if (clickType == ClickType.LEFT) {
            Messages.START_ACCESS_REQUEST.send(player, Formatter.format("%cancel_word%", Configuration.get(MainConfiguration.class).getAccessManagingCancelWords().getFirst()));
            player.closeInventory();
        } else if (clickType == ClickType.RIGHT) {
            this.plugin.getInventoryManager().openInventory(player, "chest_access_manager");
        }
    }

}
