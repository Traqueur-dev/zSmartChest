package fr.traqueur.storageplus.domains;

import fr.traqueur.storageplus.api.domains.PlacedChestContent;
import fr.traqueur.storageplus.api.domains.StorageItem;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ZPlacedChestContent implements PlacedChestContent {

    private final UUID uuid;
    private List<StorageItem> content;

    public ZPlacedChestContent(UUID uuid, List<StorageItem> content) {
        this.uuid = uuid;
        this.content = content;
    }

    public void setContent(List<StorageItem> collect) {
        this.content = collect;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public List<StorageItem> content() {
        return this.content;
    }

    @Override
    public void generatePage(Collection<Integer> slots, int size, int page) {
        if(content.stream().noneMatch(item -> ((item.slot() / size) + 1) == page)) {
            for(Integer slot : slots) {
                StorageItem empty = StorageItem.empty(slot + (size * (page - 1)));
                content.add(empty);
            }
        }
    }
}
