package fr.traqueur.storageplus.hooks.providers;

import fr.traqueur.storageplus.api.hooks.ShopProvider;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.api.objects.SellPrice;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class EconomyShopGUIProvider implements ShopProvider {
    @Override
    public boolean sellItems(Player player, ItemStack item, int amount) {
        Optional<SellPrice> optional = EconomyShopGUIHook.getSellPrice(player, item);
        if (optional.isEmpty())
            return false;
        SellPrice price = optional.get();
        EconomyShopGUIHook.getEcon(price.getShopItem().getEcoType()).depositBalance(player, price.getPrice(price.getShopItem().getEcoType()) * amount);
        return true;
    }
}
