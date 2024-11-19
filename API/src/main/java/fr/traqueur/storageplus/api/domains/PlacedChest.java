package fr.traqueur.storageplus.api.domains;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PlacedChest {

    String serialize();

    void tick();

    long getTime();

    boolean isAutoSell();

    void setAutoSell(boolean autoSell);

    long getSellDelay();

    void setSellDelay(long sellDelay);

    boolean isVacuum();

    void setVacuum(boolean vacuum);

    ChestTemplate getChestTemplate();

    Location getLocation();

    List<ItemStack> addItems(List<ItemStack> items);
}
