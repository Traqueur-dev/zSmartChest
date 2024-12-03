package fr.traqueur.storageplus.api.domains;

import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.config.DropMode;
import fr.traqueur.storageplus.api.config.AccessMode;
import fr.traqueur.storageplus.api.hooks.ShopHook;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ChestTemplate {

    long getSellDelay();

    boolean isAutoSell();

    List<ShopHook> getShops();

    boolean isVacuum();

    List<Material> getVacuumBlacklist();

    String getName();

    DropMode getDropMode();

    boolean isInfinite();

    void open(StoragePlusPlugin plugin, Player player, int page);

    ItemStack build(Player player);

    int getMaxPages();

    int getMaxStackSize();

    double getMultiplier();

    AccessMode getShareMode();
}
