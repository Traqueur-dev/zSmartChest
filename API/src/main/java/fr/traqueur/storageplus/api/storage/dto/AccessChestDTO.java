package fr.traqueur.storageplus.api.storage.dto;

import fr.maxlego08.sarah.Column;

import java.util.UUID;

public record AccessChestDTO(
        @Column(value ="uniqueId", primary = true) UUID uniqueId,
        UUID chestId,
        UUID playerId) {
}
