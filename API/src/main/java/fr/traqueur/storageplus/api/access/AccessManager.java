package fr.traqueur.storageplus.api.access;

import fr.traqueur.storageplus.api.Manager;
import fr.traqueur.storageplus.api.domains.AccessChest;
import fr.traqueur.storageplus.api.domains.PlacedChest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccessManager extends Manager {

    String TABLE_NAME = "access_chests";

    void addAccess(AccessChest accessChest);

    void removeAccess(AccessChest accessChest);

    List<AccessChest> getAccesses(UUID chestId);

    boolean hasAccess(PlacedChest chestId, UUID playerId);

    void clearAccesses(UUID chestId);

    Optional<AccessChest> getAccess(UUID chestId, UUID playerId);

    boolean isPending(UUID playerId);

    void addPending(UUID playerId, PlacedChest chestId);

    PlacedChest getPending(UUID playerId);

    void removePending(UUID playerId);

    void saveAll();
}
