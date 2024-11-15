package fr.traqueur.storageplugs.api.domains;

import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SmartChest {

    String getName();

    void open(StoragePlusPlugin plugin, Player player);

    ItemStack build(Player player);
}
