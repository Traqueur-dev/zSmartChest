package fr.traqueur.storageplus.access;

import fr.traqueur.storageplus.api.access.AccessManager;
import fr.traqueur.storageplus.api.domains.AccessChest;
import fr.traqueur.storageplus.api.storage.Service;
import fr.traqueur.storageplus.api.storage.dto.AccessChestDTO;
import fr.traqueur.storageplus.storage.repositories.AccessChestRepository;
import org.bukkit.Bukkit;

import java.util.*;

public class ZAccessManager implements AccessManager {

    private final Map<UUID, List<AccessChest>> accesses;
    private final Service<AccessChest, AccessChestDTO> service;

    public ZAccessManager() {
        this.accesses = new HashMap<>();
        this.service = new Service<>(this.getPlugin(), AccessChestDTO.class, new AccessChestRepository(), TABLE_NAME);

        for (AccessChest accessChest : this.service.findAll()) {
            this.accesses.computeIfAbsent(accessChest.getChestId(), k -> new ArrayList<>()).add(accessChest);
        }
    }

    @Override
    public void addAccess(AccessChest accessChest) {
        this.accesses.computeIfAbsent(accessChest.getChestId(), k -> new ArrayList<>()).add(accessChest);
        this.service.save(accessChest);
    }

    @Override
    public void removeAccess(AccessChest accessChest) {
        List<AccessChest> accessChests = this.accesses.get(accessChest.getChestId());
        if (accessChests != null) {
            accessChests.remove(accessChest);
        }
        this.service.delete(accessChest);
    }

    @Override
    public List<AccessChest> getAccesses(UUID chestId) {
        return this.accesses.getOrDefault(chestId, new ArrayList<>());
    }

    @Override
    public boolean hasAccess(UUID chestId, UUID playerId) {
        return this.getAccess(chestId, playerId).isPresent();
    }

    @Override
    public void clearAccesses(UUID chestId) {
        List<AccessChest> accessChests = this.accesses.remove(chestId);
        if (accessChests != null) {
            accessChests.forEach(this.service::delete);
        }
    }

    @Override
    public Optional<AccessChest> getAccess(UUID chestId, UUID playerId) {
        return this.accesses.getOrDefault(chestId, new ArrayList<>()).stream().filter(accessChest -> accessChest.getPlayerId().equals(playerId)).findFirst();
    }

    @Override
    public void saveAll() {
        this.accesses.values().forEach(accessChests -> accessChests.forEach(this.service::save));
    }

}
