package fr.traqueur.storageplus.api.domains;

import java.util.UUID;

public interface AccessChest {

    UUID getUniqueId();

    UUID getChestId();

    UUID getPlayerId();

}
