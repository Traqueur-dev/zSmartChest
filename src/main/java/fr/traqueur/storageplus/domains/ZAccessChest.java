package fr.traqueur.storageplus.domains;

import fr.traqueur.storageplus.api.domains.AccessChest;

import java.util.UUID;

public class ZAccessChest implements AccessChest {

    private final UUID uniqueId;
    private final UUID chestId;
    private final UUID playerId;

    public ZAccessChest(UUID uniqueId, UUID chestId, UUID playerId) {
        this.uniqueId = uniqueId;
        this.chestId = chestId;
        this.playerId = playerId;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public UUID getChestId() {
        return this.chestId;
    }

    @Override
    public UUID getPlayerId() {
        return this.playerId;
    }

}
