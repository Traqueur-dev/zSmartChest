package fr.traqueur.storageplus.api;

import fr.traqueur.storageplus.api.domains.ChestTemplate;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import fr.traqueur.storageplus.api.domains.PlacedChestContent;
import fr.traqueur.storageplus.api.domains.StorageItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StoragePlusManager extends Manager {

    String TABLE_NAME = "storageplus_chests";
    
    Map<String, ChestTemplate> getSmartChests();

    Optional<PlacedChest> getChestFromBlock(Location location);

    void placeChest(Player player, Location location, ChestTemplate chest, ItemStack itemStack);

    void breakChest(BlockBreakEvent event, Location location);

    Optional<ChestTemplate> getChestFromItem(ItemStack item);

    ChestTemplate getSmartChest(String s);

    NamespacedKey getNamespaceKey();

    NamespacedKey getNamespaceKeyUUID();

    void give(Player player, ChestTemplate chest);

    void registerChests();

    void handleAutoSell();

    PlacedChest deserializeChest(String s);
    
    void openChest(Player player, PlacedChest chest, int page, boolean first);
    
    void closeChest(Player player);

    PlacedChest getOpenedChest(Player player);

    void saveChest(PlacedChest chest);

    void compress(PlacedChest chest, List<Material> availableMaterials, List<Integer> slots);

    void smelt(PlacedChest chest, List<Material> availableMaterials, List<Integer> slots);

    List<PlacedChest> getChestsInChunk(Chunk chunk);

    ItemStack addItemsToChest(Chunk chunk, ItemStack itemStack);

    void saveAll();

    PlacedChestContent getContent(PlacedChest chest);

    void setContent(PlacedChest chest, List<StorageItem> items);

    int getMaxStackSize(PlacedChest chest, ItemStack item);

    void dropItems(Location location, int amount, ItemStack clone);

    void purge(Chunk chunk, int radius);

    ItemStack cloneItemStack(ItemStack itemStack);
}
