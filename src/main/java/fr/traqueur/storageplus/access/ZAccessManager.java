package fr.traqueur.storageplus.access;

import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.access.AccessManager;
import fr.traqueur.storageplus.api.config.AccessMode;
import fr.traqueur.storageplus.api.domains.AccessChest;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import fr.traqueur.storageplus.api.storage.Service;
import fr.traqueur.storageplus.api.storage.dto.AccessChestDTO;
import fr.traqueur.storageplus.storage.repositories.AccessChestRepository;
import org.bukkit.Bukkit;

import java.util.*;

public class ZAccessManager implements AccessManager {

    private final Map<UUID, PlacedChest> pendingPlayers;
    private final Map<UUID, List<AccessChest>> accesses;
    private final Service<AccessChest, AccessChestDTO> service;

    public ZAccessManager() {
        this.accesses = new HashMap<>();
        this.pendingPlayers = new HashMap<>();
        this.service = new Service<>(this.getPlugin(), AccessChestDTO.class, new AccessChestRepository(), TABLE_NAME);

        for (AccessChest accessChest : this.service.findAll()) {
            this.accesses.computeIfAbsent(accessChest.getChestId(), k -> new ArrayList<>()).add(accessChest);
        }
        Bukkit.getPluginManager().registerEvents(new ZAccessListener(this), this.getPlugin());
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
    public boolean hasAccess(PlacedChest chestId, UUID playerId) {
        if(chestId.getShareMode() == AccessMode.PRIVATE) {
            return chestId.getOwner().equals(playerId);
        }

        return chestId.getOwner().equals(playerId) || this.getAccess(chestId.getUniqueId(), playerId).isPresent();
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
    public boolean isPending(UUID playerId) {
        return this.pendingPlayers.containsKey(playerId);
    }

    @Override
    public void addPending(UUID playerId, PlacedChest chestId) {
        this.pendingPlayers.put(playerId, chestId);
    }

    @Override
    public PlacedChest getPending(UUID playerId) {
        return this.pendingPlayers.get(playerId);
    }

    @Override
    public void removePending(UUID playerId) {
        this.pendingPlayers.remove(playerId);
    }

    @Override
    public void saveAll() {
        this.accesses.values().forEach(accessChests -> accessChests.forEach(this.service::save));
    }

}
