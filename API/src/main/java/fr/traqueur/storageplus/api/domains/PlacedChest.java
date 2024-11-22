package fr.traqueur.storageplus.api.domains;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface PlacedChest {

    String serialize();

    void tick();

    UUID getUniqueId();

    long getTime();

    boolean isAutoSell();

    void setAutoSell(boolean autoSell);

    long getSellDelay();

    void setSellDelay(long sellDelay);

    boolean isVacuum();

    void setVacuum(boolean vacuum);

    ChestTemplate getChestTemplate();

    Location getLocation();
}
