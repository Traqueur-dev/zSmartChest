package fr.traqueur.storageplugs.api.domains;

import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ChestTemplate {

    long getSellDelay();

    boolean isAutoSell();

    String getName();

    void open(StoragePlusPlugin plugin, Player player);

    ItemStack build(Player player);
}
