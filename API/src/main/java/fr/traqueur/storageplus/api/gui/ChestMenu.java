package fr.traqueur.storageplus.api.gui;

import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ChestMenu extends ZInventory {

    public ChestMenu(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super(plugin, name, fileName, size, buttons);
    }

    @Override
    public void postOpenInventory(Player player, InventoryDefault inventoryDefault) {
        inventoryDefault.setDisablePlayerInventoryClick(false);
    }

    @Override
    public void closeInventory(Player player, InventoryDefault inventoryDefault) {
        ((StoragePlusPlugin) getPlugin())
                .getManager(StoragePlusManager.class)
                .closeChest(player);
    }

    @Override
    public int getMaxPage(Player player, Object... objects) {
        int maxPages = ((StoragePlusPlugin) getPlugin())
                .getManager(StoragePlusManager.class)
                .getOpenedChest(player).getChestTemplate().getMaxPages();

        if(maxPages == -1) {
            return super.getMaxPage(player, objects);
        } else {
            return maxPages;
        }
    }
}
