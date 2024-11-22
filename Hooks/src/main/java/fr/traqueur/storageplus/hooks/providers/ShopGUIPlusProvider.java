package fr.traqueur.storageplus.hooks.providers;

import fr.traqueur.storageplus.api.hooks.ShopProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusProvider implements ShopProvider {
    @Override
    public boolean sellItems(Player player, ItemStack item, int amount) {
        return false;
    }
}
