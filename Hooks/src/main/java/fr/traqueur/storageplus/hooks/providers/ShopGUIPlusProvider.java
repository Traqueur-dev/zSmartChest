package fr.traqueur.storageplus.hooks.providers;

import fr.traqueur.storageplus.api.hooks.ShopProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusProvider implements ShopProvider {
    @Override
    public boolean sellItems(OfflinePlayer player, ItemStack item, int amount) {
        if(!player.isOnline()) {
            return false;
        }
        double price = ShopGuiPlusApi.getItemStackPriceSell(player.getPlayer(), item);
        if (price == -1) {
            return false;
        }

        double total = price * amount;
        ShopGuiPlusApi.getItemStackShop(item).getEconomyProvider().deposit(player.getPlayer(), total);
        return true;
    }
}
