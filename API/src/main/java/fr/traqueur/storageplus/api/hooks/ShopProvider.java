package fr.traqueur.storageplus.api.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public interface ShopProvider {

    boolean sellItems(OfflinePlayer player, ItemStack item, int amount);

}
