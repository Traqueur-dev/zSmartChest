package fr.traqueur.storageplus.api.domains;

import fr.traqueur.storageplus.api.config.ShareMode;
import org.bukkit.Location;

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

    UUID getOwner();

    ShareMode getShareMode();

    void setShareMode(ShareMode shareMode);
}
