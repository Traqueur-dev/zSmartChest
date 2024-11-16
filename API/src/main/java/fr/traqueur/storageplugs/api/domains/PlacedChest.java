package fr.traqueur.storageplugs.api.domains;

import org.bukkit.Location;

public interface PlacedChest {

    String serialize();

    void tick();

    long getTime();

    boolean isAutoSell();

    void setAutoSell(boolean autoSell);

    long getSellDelay();

    void setSellDelay(long sellDelay);

    ChestTemplate getChestTemplate();

    Location getLocation();
}
