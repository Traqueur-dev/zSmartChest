package fr.traqueur.storageplus;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.ZLogger;
import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.domains.ChestTemplate;
import fr.traqueur.storageplugs.api.domains.PlacedChest;
import fr.traqueur.storageplugs.api.gui.ChestMenu;
import fr.traqueur.storageplugs.api.serializers.ChestLocationDataType;
import fr.traqueur.storageplus.domains.ZChestTemplate;
import fr.traqueur.storageplus.domains.ZPlacedChest;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZStoragePlusManager implements StoragePlusManager {

    private final Map<String, ChestTemplate> smartChests;
    private final Map<UUID, PlacedChest> openedChests;
    private final Map<Location, Inventory> mapInventoryOpened;


    public ZStoragePlusManager() {
        this.smartChests = new HashMap<>();
        this.openedChests = new HashMap<>();
        this.mapInventoryOpened = new HashMap<>();

        this.registerChests();

        this.getPlugin().getServer().getPluginManager().registerEvents(new ZStoragePlusListener(this), this.getPlugin());
        this.getPlugin().getScheduler().runTimer(new ZStoragePlusAutoSellTask(this), 0, 20);
    }

    @Override
    public Map<String, ChestTemplate> getSmartChests() {
        return this.smartChests;
    }

    @Override
    public Optional<PlacedChest> getChestFromBlock(Location location) {
        Chunk chunk = location.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        List<PlacedChest> chests = container.getOrDefault(this.getNamespaceKey(), PersistentDataType.LIST.listTypeFrom(ChestLocationDataType.INSTANCE), new ArrayList<>());
        return chests.stream().filter(e -> this.locationEquals(e.getLocation(), location)).findFirst();
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
        return new ZPlacedChest(UUID.fromString(parts[6]),new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])), this.getSmartChest(parts[4]), Long.parseLong(parts[5]), Boolean.parseBoolean(parts[7]), Long.parseLong(parts[8]));
    }

    @Override
    public void openChest(Player player, PlacedChest chest) {
        this.openedChests.put(player.getUniqueId(), chest);
        if(this.mapInventoryOpened.containsKey(chest.getLocation())) {
            player.openInventory(this.mapInventoryOpened.get(chest.getLocation()));
            return;
        }
        chest.getChestTemplate().open(this.getPlugin(), player);
    }

    @Override
    public void closeChest(Player player) {
        PlacedChest chest = this.openedChests.remove(player.getUniqueId());
        if(this.openedChests.values().stream().anyMatch(e -> this.locationEquals(e.getLocation(), chest.getLocation()))) {
            return;
        }
        this.mapInventoryOpened.remove(chest.getLocation());
        this.saveChest(chest);
    }

    @Override
    public void postOpenChest(Player player, Inventory spigotInventory) {
        this.mapInventoryOpened.putIfAbsent(this.openedChests.get(player.getUniqueId()).getLocation(), spigotInventory);
    }

    @Override
    public PlacedChest getOpenedChest(Player player) {
        return this.openedChests.get(player.getUniqueId());
    }

    @Override
    public void saveChest(PlacedChest chest) {
        if (this.getPlugin().isDebug()) {
            ZLogger.info("Saving chest " + chest.getChestTemplate().getName() + " at " + chest.getLocation());
        }
        Chunk chunk = chest.getLocation().getChunk();
        List<PlacedChest> chests = this.getChestsInChunk(chunk);
        chests.removeIf(e -> this.locationEquals(e.getLocation(), chest.getLocation()));
        chests.add(chest);
        this.saveChestsInChunk(chunk, chests);
    }

    @Override
    public List<ItemStack> compress(List<ItemStack> items, List<Material> availableMaterials) {
        Map<Material, Integer> map = new HashMap<>();
        List<ItemStack> compressed = new ArrayList<>();
        for (ItemStack item : items) {
            Material material = item.getType();
            if (availableMaterials.contains(material)) {
                map.put(material, map.getOrDefault(material, 0) + item.getAmount());
            } else {
                compressed.add(item);
            }
        }
        for (Map.Entry<Material, Integer> entry : map.entrySet()) {
            Material material = entry.getKey();
            int amount = entry.getValue();
            Material compressedType = this.getCompressedType(material);
            if(compressedType == material) {
                while (amount > 0) {
                    int toAdd = Math.min(amount, compressedType.getMaxStackSize());
                    ItemStack item = new ItemStack(material, toAdd);
                    compressed.add(item);
                    amount -= toAdd;
                }
            }
            int maxAmount = compressedType.getMaxStackSize();
            int compressedAmount = amount / 9;
            int rest = amount % 9;
            while (compressedAmount > 0) {
                int toAdd = Math.min(compressedAmount, maxAmount);
                ItemStack item = new ItemStack(compressedType, toAdd);
                compressed.add(item);
                compressedAmount -= toAdd;
            }
            if (rest > 0) {
                ItemStack item = new ItemStack(material, rest);
                compressed.add(item);
            }
        }
        Map<ItemStack, Integer> groupedItems = new HashMap<>();
        for (ItemStack item : compressed) {
            if (item == null) continue;

            Optional<ItemStack> similarItem = groupedItems.keySet().stream()
                    .filter(existing -> existing.isSimilar(item))
                    .findFirst();

            if (similarItem.isPresent()) {
                groupedItems.put(similarItem.get(), groupedItems.get(similarItem.get()) + item.getAmount());
            } else {
                groupedItems.put(item.clone(), item.getAmount());
            }
        }

        List<ItemStack> mergedItems = new ArrayList<>();
        for (Map.Entry<ItemStack, Integer> entry : groupedItems.entrySet()) {
            ItemStack baseItem = entry.getKey();
            int totalAmount = entry.getValue();
            int maxStackSize = baseItem.getMaxStackSize();
            while (totalAmount > 0) {
                int stackAmount = Math.min(totalAmount, maxStackSize);
                ItemStack stack = baseItem.clone();
                stack.setAmount(stackAmount);
                mergedItems.add(stack);
                totalAmount -= stackAmount;
            }
        }

        return mergedItems;
    }

    private Material getCompressedType(Material material) {
        return switch (material) {
            case IRON_INGOT -> Material.IRON_BLOCK;
            case GOLD_INGOT -> Material.GOLD_BLOCK;
            case DIAMOND -> Material.DIAMOND_BLOCK;
            case EMERALD -> Material.EMERALD_BLOCK;
            case LAPIS_LAZULI -> Material.LAPIS_BLOCK;
            case REDSTONE -> Material.REDSTONE_BLOCK;
            case COAL -> Material.COAL_BLOCK;
            case NETHERITE_INGOT -> Material.NETHERITE_BLOCK;
            case RAW_COPPER -> Material.RAW_COPPER_BLOCK;
            case RAW_IRON -> Material.RAW_IRON_BLOCK;
            case RAW_GOLD -> Material.RAW_GOLD_BLOCK;
            default -> material;
        };
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
            this.getPlugin().getInventoryManager().loadInventory(getPlugin(), fileName, ChestMenu.class);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            name = file.getName().replace(".yml", "");
            MenuItemStack menuItemStack;
            Loader<MenuItemStack> loader = new MenuItemStackLoader(this.getPlugin().getInventoryManager());
            menuItemStack = loader.load(config, "settings.item.", file);
            boolean autoSell = config.getBoolean("settings.auto-sell.enabled", false);
            long interval = config.getLong("settings.auto-sell.interval", Configuration.get(MainConfiguration.class).getDefaultAutoSellDelay());
            List<String> shops;
            if(config.contains("settings.auto-sell.shops")) {
                shops = config.getStringList("settings.auto-sell.shops");
            } else {
                shops = new ArrayList<>();
            }
            this.smartChests.put(name, new ZChestTemplate(getPlugin(), name, menuItemStack, autoSell, interval, shops));
            if(this.getPlugin().isDebug()) {
                ZLogger.info("Registered chest " + name);
            }
        } catch (InventoryException e) {
            ZLogger.severe("Error while loading chest " + name, e);
        }
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

}
