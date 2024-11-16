package fr.traqueur.storageplugs.api;

import fr.traqueur.storageplugs.api.domains.ChestTemplate;
import fr.traqueur.storageplugs.api.domains.PlacedChest;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public interface StoragePlusManager extends Manager {

    Map<String, ChestTemplate> getSmartChests();

    Optional<PlacedChest> getChestFromBlock(Location location);

    void placeChest(Player player, Location location, ChestTemplate chest);

    void breakChest(Location location);

    Optional<ChestTemplate> getChestFromItem(ItemStack item);

    ChestTemplate getSmartChest(String s);

    NamespacedKey getNamespaceKey();

    void give(Player player, ChestTemplate chest);

    void registerChests();

    void handleAutoSell();

    PlacedChest deserializeChest(String s);
    
    void openChest(Player player, PlacedChest chest);
    
    void closeChest(Player player);

    void postOpenChest(Player player, Inventory spigotInventory);
}
