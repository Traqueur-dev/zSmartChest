package fr.traqueur.storageplus.storage.repositories;

import fr.traqueur.storageplus.api.domains.AccessChest;
import fr.traqueur.storageplus.api.storage.Repository;
import fr.traqueur.storageplus.api.storage.dto.AccessChestDTO;
import fr.traqueur.storageplus.domains.ZAccessChest;

public class AccessChestRepository implements Repository<AccessChest, AccessChestDTO> {
    @Override
    public AccessChest toEntity(AccessChestDTO accessChestDTO) {
        return new ZAccessChest(accessChestDTO.uniqueId(), accessChestDTO.chestId(), accessChestDTO.playerId());
    }

    @Override
    public AccessChestDTO toDTO(AccessChest entity) {
        return new AccessChestDTO(entity.getUniqueId(), entity.getChestId(), entity.getPlayerId());
    }
}
