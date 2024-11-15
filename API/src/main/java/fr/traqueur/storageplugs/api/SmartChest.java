package fr.traqueur.storageplugs.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SmartChest {

    void open(StoragePlusPlugin plugin, Player player);

    ItemStack build(Player player);
}
