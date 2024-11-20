package fr.traqueur.storageplus.api.domains;

import java.util.List;
import java.util.UUID;

public class PlacedChestContent {

    private final UUID uuid;
    private List<StorageItem> content;

    public PlacedChestContent(UUID uuid, List<StorageItem> content) {
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
}
