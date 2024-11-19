package fr.traqueur.storageplus.domains;

import fr.traqueur.storageplus.api.domains.ChestTemplate;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ZPlacedChest implements PlacedChest {

    private final UUID owner;
    private final Location location;
    private final ChestTemplate chestTemplate;
    private long time;
    private boolean autoSell;
    private long sellDelay;
    private boolean vacuum;

    public ZPlacedChest(UUID owner, Location location, ChestTemplate chestTemplate) {
        this(owner, location, chestTemplate, 0, chestTemplate.isAutoSell(), chestTemplate.getSellDelay(), chestTemplate.isVacuum());
    }

    public ZPlacedChest(UUID owner, Location location, ChestTemplate chestTemplate, long time, boolean autoSell, long sellDelay, boolean vacuum) {
        this.owner = owner;
        this.location = location;
        this.chestTemplate = chestTemplate;
        this.time = time;
        this.autoSell = autoSell;
        this.sellDelay = sellDelay;
        this.vacuum = vacuum;
    }

    @Override
    public String serialize() {
        return (this.location.getWorld() == null ? "null" : this.location.getWorld().getUID()) + ";"
                + this.location.getBlockX() + ";"
                + this.location.getBlockY() + ";"
                + this.location.getBlockZ() + ";"
                + this.chestTemplate.getName() + ";"
                + this.time + ";"
                + this.owner.toString() + ";"
                + this.autoSell + ";"
                + this.sellDelay +";"
                + this.vacuum;
    }

    @Override
    public void tick() {
        this.time++;
        if(this.time == Long.MAX_VALUE) {
            this.time = 0;
        }
    }
    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public boolean isAutoSell() {
        return this.autoSell;
    }

    @Override
    public void setAutoSell(boolean autoSell) {
        this.autoSell = autoSell;
    }

    @Override
    public long getSellDelay() {
        return this.sellDelay;
    }

    @Override
    public void setSellDelay(long sellDelay) {
        this.sellDelay = sellDelay;
    }

    @Override
    public boolean isVacuum() {
        return this.vacuum;
    }

    @Override
    public void setVacuum(boolean vacuum) {
        this.vacuum = vacuum;
    }

    @Override
    public ChestTemplate getChestTemplate() {
        return chestTemplate;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public List<ItemStack> addItems(List<ItemStack> items) {
        return List.of();
    }
}
