package fr.traqueur.storageplugs.api.domains;

import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ChestTemplate {

    long getSellDelay();

    boolean isAutoSell();

    List<String> getShops();

    String getName();

    void open(StoragePlusPlugin plugin, Player player);

    ItemStack build(Player player);
}
