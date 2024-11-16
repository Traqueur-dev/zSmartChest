package fr.traqueur.storageplus;

import fr.groupez.api.ZLogger;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.traqueur.storageplugs.api.domains.ChestLocation;
import fr.traqueur.storageplugs.api.domains.SmartChest;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.serializers.ChestLocationDataType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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

    private final Map<String, SmartChest> smartChests;

    public ZStoragePlusManager() {
        this.smartChests = new HashMap<>();

        this.registerChests();

        this.getPlugin().getServer().getPluginManager().registerEvents(new ZStoragePlusListener(this), this.getPlugin());
    }

    @Override
    public Map<String, SmartChest> getSmartChests() {
        return this.smartChests;
    }

    @Override
    public Optional<SmartChest> getChestFromBlock(Location location) {
        Chunk chunk = location.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        List<ChestLocation> chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
        return chests.stream().filter(e -> this.locationEquals(e.location(), location)).findFirst().map(e -> this.getSmartChest(e.chestName()));
    }

    @Override
    public void placeChest(Location location, SmartChest chest) {
        Chunk chunk = location.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        ChestLocation chestLocation = new ChestLocation(location, chest.getName());
        List<ChestLocation> chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
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
        List<ChestLocation> chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
        chests = new ArrayList<>(chests);
        chests.removeIf(e -> this.locationEquals(e.location(), location));
        container.set(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), chests);
        if(this.getPlugin().isDebug()) {
            ZLogger.info("Broke chest at " + location);
        }
    }

    @Override
    public Optional<SmartChest> getChestFromItem(ItemStack item) {
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
    public SmartChest getSmartChest(String s) {
        return this.smartChests.getOrDefault(s, null);
    }

    @Override
    public NamespacedKey getNamespaceKey() {
        return new NamespacedKey(this.getPlugin(), "storageplus");
    }

    @Override
    public void give(Player player, SmartChest chest) {
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
            menuItemStack = loader.load(config, "item.", file);
            boolean autoSell = config.getBoolean("auto-sell", false);
            this.smartChests.put(name, new ZSmartChest(getPlugin(), name, menuItemStack, autoSell));
            if(this.getPlugin().isDebug()) {
                ZLogger.info("Registered chest " + name);
            }
        } catch (InventoryException e) {
            ZLogger.severe("Error while loading chest " + name, e);
        }
    }

}
