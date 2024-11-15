package fr.traqueur.storageplus;

import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.entity.Player;

public class ZSmartChest {

    private final String menu;
    private final boolean autoSell;

    public ZSmartChest(String menu, boolean autoSell) {
        this.menu = menu;
        this.autoSell = autoSell;
    }

    public void open(StoragePlusPlugin plugin, Player player) {
        plugin.getInventoryManager().openInventory(player, this.menu);
    }

}
