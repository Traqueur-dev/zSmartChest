package fr.traqueur.storageplugs.api;

import org.bukkit.entity.Player;

import java.util.Map;

public interface StoragePlusManager extends Manager {

    Map<String, SmartChest> getSmartChests();

    SmartChest getSmartChest(String s);

    void give(Player player, SmartChest chest);

}
