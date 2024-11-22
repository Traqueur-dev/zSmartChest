package fr.traqueur.storageplus.hooks.providers;

import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.zshop.api.ShopManager;
import fr.maxlego08.zshop.api.buttons.ItemButton;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.hooks.ShopProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ZShopProvider implements ShopProvider {

    @Override
    public boolean sellItems(Player player, ItemStack item, int amount) {
        var register = JavaPlugin.getPlugin(StoragePlusPlugin.class).getServer()
                .getServicesManager().getRegistration(ShopManager.class);
        if (register == null) {
            return false;
        }
        var shopManager = register.getProvider();
        for (ItemButton itemButton : shopManager.getItemButtons()) {
            if(!itemButton.canSell()) {
                continue;
            }
            if(itemButton.getItemStack().build(player, false).isSimilar(item)) {
                double price = itemButton.getSellPrice(player, amount);
                itemButton.getEconomy().depositMoney(player, price);
                return true;
            }
        }
        return false;
    }
}
