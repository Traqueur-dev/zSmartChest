package fr.traqueur.storageplus.api.storage.dto;

import fr.maxlego08.sarah.Column;

import java.util.UUID;

public record PlacedChestDTO(@Column(value="unique_id", primary = true) UUID uniqueId,
                             @Column(value = "content", type = "LONGTEXT") String content) {
}
