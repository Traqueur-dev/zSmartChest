package fr.traqueur.storageplugs.api.domains;

import org.bukkit.Location;

public interface PlacedChest {
    String serialize();

    void tick();

    long getTime();

    ChestTemplate getChestTemplate();

    Location getLocation();
}
