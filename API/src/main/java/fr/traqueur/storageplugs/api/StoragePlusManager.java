package fr.traqueur.storageplugs.api;

import fr.traqueur.storageplugs.api.domains.SmartChest;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public interface StoragePlusManager extends Manager {

    Map<String, SmartChest> getSmartChests();

    Optional<SmartChest> getChestFromBlock(Location location);

    void placeChest(Location location, SmartChest chest);

    void breakChest(Location location);

    Optional<SmartChest> getChestFromItem(ItemStack item);

    SmartChest getSmartChest(String s);

    NamespacedKey getNamespaceKey();

    void give(Player player, SmartChest chest);

    void registerChests();
}
