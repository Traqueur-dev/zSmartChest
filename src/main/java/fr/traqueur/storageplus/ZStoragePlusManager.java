package fr.traqueur.storageplus;

import fr.groupez.api.ZLogger;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.traqueur.storageplugs.api.SmartChest;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ZStoragePlusManager implements StoragePlusManager {

    private final Map<String, SmartChest> smartChests;

    public ZStoragePlusManager() {
        this.smartChests = new HashMap<>();

        this.registerChests();
    }


    public void registerChests() {
        File folder = new File(this.getPlugin().getDataFolder(), "chests/");
        if (!folder.exists()) {
            folder.mkdirs();
            this.getPlugin().saveResource("chests/autosell_chest.yml", false);
        }
        try (Stream<Path> s = Files.walk(Paths.get(folder.getPath()))) {
            s.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml"))
                    .forEach(this::registerChestFromFile);
        } catch (IOException exception) {
            ZLogger.severe("Error while loading chests", exception);
        }
    }

    private void registerChestFromFile(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String name = file.getName().replace(".yml", "");
        String menu = config.getString("menu");
        MenuItemStack menuItemStack;
        try {
            Loader<MenuItemStack> loader = new MenuItemStackLoader(this.getPlugin().getInventoryManager());
            menuItemStack = loader.load(config, "item.", file);
        } catch (InventoryException e) {
            ZLogger.severe("Error while loading chest " + name, e);
            return;
        }
        boolean autoSell = config.getBoolean("auto-sell", false);
        this.smartChests.put(name, new ZSmartChest(menu, menuItemStack, autoSell));
        if(this.getPlugin().isDebug()) {
            ZLogger.info("Registered chest " + name);
        }
    }

    @Override
    public Map<String, SmartChest> getSmartChests() {
        return this.smartChests;
    }

    @Override
    public SmartChest getSmartChest(String s) {
        return this.smartChests.getOrDefault(s, null);
    }

    @Override
    public void give(Player player, SmartChest chest) {
        player.getInventory().addItem(chest.build(player));
    }
}
