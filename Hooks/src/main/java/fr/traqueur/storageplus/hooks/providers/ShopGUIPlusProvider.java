package fr.traqueur.storageplus.hooks.providers;

import fr.traqueur.storageplus.api.hooks.ShopProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusProvider implements ShopProvider {
    @Override
    public boolean sellItems(Player player, ItemStack item, int amount) {
        double price = ShopGuiPlusApi.getItemStackPriceSell(player, item);
        if (price == -1) {
            return false;
        }

        double total = price * amount;
        ShopGuiPlusApi.getItemStackShop(item).getEconomyProvider().deposit(player, total);
        return true;
    }
}
