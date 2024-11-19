package fr.traqueur.storageplus.api.domains;

import fr.traqueur.storageplus.api.StoragePlusPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ChestTemplate {

    long getSellDelay();

    boolean isAutoSell();

    List<String> getShops();

    boolean isVacuum();

    List<Material> getVacuumBlacklist();

    String getName();

    void open(StoragePlusPlugin plugin, Player player);

    ItemStack build(Player player);
}
