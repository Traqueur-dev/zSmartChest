package fr.traqueur.storageplugs.api.gui;

import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ChestMenu extends ZInventory {

    public ChestMenu(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super(plugin, name, fileName, size, buttons);
    }

    @Override
    public void postOpenInventory(Player player, InventoryDefault inventoryDefault) {
        ((StoragePlusPlugin) getPlugin())
                .getManager(StoragePlusManager.class)
                .postOpenChest(player, inventoryDefault.getSpigotInventory());
    }

    @Override
    public void closeInventory(Player player, InventoryDefault inventoryDefault) {
        ((StoragePlusPlugin) getPlugin())
                .getManager(StoragePlusManager.class)
                .closeChest(player);
    }
}
