package fr.traqueur.storageplus.storage.repositories;

import fr.traqueur.storageplus.api.domains.PlacedChestContent;
import fr.traqueur.storageplus.api.domains.StorageItem;
import fr.traqueur.storageplus.api.storage.Repository;
import fr.traqueur.storageplus.api.storage.dto.PlacedChestDTO;
import fr.traqueur.storageplus.domains.ZPlacedChestContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlacedChestRepository implements Repository<PlacedChestContent, PlacedChestDTO> {
    @Override
    public PlacedChestContent toEntity(PlacedChestDTO placedChestDTO) {
        List<StorageItem> content;
        if(placedChestDTO.content().isEmpty()) {
            content = new ArrayList<>();
        } else {
            content = Arrays.stream(placedChestDTO.content().split(";"))
                    .map(StorageItem::deserialize)
                    .collect(Collectors.toList());
        }
        return new ZPlacedChestContent(placedChestDTO.uniqueId(), content);
    }

    @Override
    public PlacedChestDTO toDTO(PlacedChestContent entity) {
        return new PlacedChestDTO(entity.uuid(), entity.content().stream().map(StorageItem::serialize).reduce((a, b) -> a + ";" + b).orElse(""));
    }
}
