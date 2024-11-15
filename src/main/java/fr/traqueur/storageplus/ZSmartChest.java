package fr.traqueur.storageplus;

import fr.maxlego08.menu.MenuItemStack;
import fr.traqueur.storageplugs.api.SmartChest;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ZSmartChest implements SmartChest {

    private final String menu;
    private final MenuItemStack item;
    private final boolean autoSell;

    public ZSmartChest(String menu, MenuItemStack item, boolean autoSell) {
        this.menu = menu;
        this.item = item;
        this.autoSell = autoSell;
    }

    public void open(StoragePlusPlugin plugin, Player player) {
        plugin.getInventoryManager().openInventory(player, this.menu);
    }

    @Override
    public ItemStack build(Player player) {
        return this.item.build(player);
    }

}
