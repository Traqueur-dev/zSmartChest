package fr.traqueur.storageplus;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.ZLogger;
import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.traqueur.storageplugs.api.domains.PlacedChest;
import fr.traqueur.storageplus.domains.ZPlacedChest;
import fr.traqueur.storageplugs.api.domains.ChestTemplate;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.serializers.ChestLocationDataType;
import fr.traqueur.storageplus.domains.ZChestTemplate;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ZStoragePlusManager implements StoragePlusManager {

    private final Map<String, ChestTemplate> smartChests;

    public ZStoragePlusManager() {
        this.smartChests = new HashMap<>();

        this.registerChests();

        this.getPlugin().getServer().getPluginManager().registerEvents(new ZStoragePlusListener(this), this.getPlugin());
        this.getPlugin().getScheduler().runTimerAsync(new ZStoragePlusAutoSellTask(this), 0, 20);
    }

    @Override
    public Map<String, ChestTemplate> getSmartChests() {
        return this.smartChests;
    }

    @Override
    public Optional<ChestTemplate> getChestFromBlock(Location location) {
        Chunk chunk = location.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        List<PlacedChest> chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
        return chests.stream().filter(e -> this.locationEquals(e.getLocation(), location)).findFirst().map(PlacedChest::getChestTemplate);
    }

    @Override
    public void placeChest(Player player, Location location, ChestTemplate chest) {
        Chunk chunk = location.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        PlacedChest chestLocation = new ZPlacedChest(player.getUniqueId(), location, chest);
        List<PlacedChest> chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
        chests = new ArrayList<>(chests);
        chests.add(chestLocation);
        container.set(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), chests);
        if(this.getPlugin().isDebug()) {
            ZLogger.info("Placed chest " + chest.getName() + " at " + location);
        }
    }

    @Override
    public void breakChest(Location location) {
        Chunk chunk = location.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        List<PlacedChest> chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
        chests = new ArrayList<>(chests);
        chests.removeIf(e -> this.locationEquals(e.getLocation(), location));
        container.set(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), chests);
        if(this.getPlugin().isDebug()) {
            ZLogger.info("Broke chest at " + location);
        }
    }

    @Override
    public Optional<ChestTemplate> getChestFromItem(ItemStack item) {
        if(item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String key = container.get(this.getNamespaceKey(), PersistentDataType.STRING);
        if(key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.getSmartChest(key));
    }

    @Override
    public ChestTemplate getSmartChest(String s) {
        return this.smartChests.getOrDefault(s, null);
    }

    @Override
    public NamespacedKey getNamespaceKey() {
        return new NamespacedKey(this.getPlugin(), "storageplus");
    }

    @Override
    public void give(Player player, ChestTemplate chest) {
        player.getInventory().addItem(chest.build(player));
    }

    private boolean locationEquals(Location l1, Location l2) {
        if(l1.getWorld() == null && l2.getWorld() == null) {
            return l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
        }
        if((l1.getWorld() == null && l2.getWorld() != null) || (l1.getWorld() != null && l2.getWorld() == null)) {
            return false;
        }

        return l1.getWorld().getUID().equals(l2.getWorld().getUID()) && l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
    }

    @Override
    public void registerChests() {
        this.smartChests.clear();
        File folder = new File(this.getPlugin().getDataFolder(), "chests/");
        try (Stream<Path> s = Files.walk(Paths.get(folder.getPath()))) {
            s.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml"))
                    .forEach(this::registerChestFromFile);
        } catch (IOException exception) {
            ZLogger.severe("Error while loading chests", exception);
        }
    }

    @Override
    public void handleAutoSell() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                List<PlacedChest> chests = this.getChestsInChunk(loadedChunk);
                chests.stream().filter(PlacedChest::isAutoSell).forEach(chest -> {
                    chest.tick();
                    if (chest.getTime() % chest.getSellDelay() == 0) {
                        if(this.getPlugin().isDebug()) {
                            ZLogger.info("Auto selling chest " + chest.getChestTemplate().getName() + " at " + chest.getLocation());
                        }
                    }
                });
                this.saveChestsInChunk(loadedChunk, chests);
            }
        }
    }

    @Override
    public PlacedChest deserializeChest(String string) {
        String[] parts = string.split(";");
        String worldName = parts[0];
        World world = worldName.equals("null") ? null : Bukkit.getWorld(UUID.fromString(worldName));
        return new ZPlacedChest(UUID.fromString(parts[6]),new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])), this.getSmartChest(parts[4]), Long.parseLong(parts[5]));
    }

    private void saveChestsInChunk(Chunk loadedChunk, List<PlacedChest> chests) {
        PersistentDataContainer container = loadedChunk.getPersistentDataContainer();
        container.set(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), chests);
    }

    private List<PlacedChest> getChestsInChunk(Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        var chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
        return new ArrayList<>(chests);
    }

    private void registerChestFromFile(File file) {
        String name = "undefined";
        try {
            String fileName = file.getPath();
            fileName = fileName.replace(this.getPlugin().getDataFolder().getPath(), "");
            this.getPlugin().getInventoryManager().loadInventory(getPlugin(), fileName);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            name = file.getName().replace(".yml", "");
            MenuItemStack menuItemStack;
            Loader<MenuItemStack> loader = new MenuItemStackLoader(this.getPlugin().getInventoryManager());
            menuItemStack = loader.load(config, "settings.item.", file);
            boolean autoSell = config.getBoolean("settings.auto-sell", false);
            long interval = config.getLong("settings.auto-sell-interval", Configuration.get(MainConfiguration.class).getDefaultAutoSellDelay());
            this.smartChests.put(name, new ZChestTemplate(getPlugin(), name, menuItemStack, autoSell, interval));
            if(this.getPlugin().isDebug()) {
                ZLogger.info("Registered chest " + name);
            }
        } catch (InventoryException e) {
            ZLogger.severe("Error while loading chest " + name, e);
        }
    }

}
