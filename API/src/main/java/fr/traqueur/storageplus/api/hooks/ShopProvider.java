package fr.traqueur.storageplus.api.hooks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ShopProvider {

    boolean sellItems(Player player, ItemStack item, int amount);

}
