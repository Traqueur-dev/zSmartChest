package fr.traqueur.storageplus.api.domains;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PlacedChestContent {

    void setContent(List<StorageItem> collect);

    UUID uuid();

    List<StorageItem> content();

    void generatePage(Collection<Integer> slots, int size, int page);
}
