package fr.traqueur.storageplus;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.NonLoadable;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.config.PlaceholdersConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZMainConfiguration implements MainConfiguration {

    @NonLoadable
    private boolean load;

    @NonLoadable
    private Map<String, MenuItemStack> storageIcons;

    @NonLoadable
    private DatabaseConfiguration configuration;

    private boolean debug;

    private long defaultAutoSellInterval;

    private PlaceholdersConfig placeholders;

    private List<String> commandAliases;
    private String commandPermission;

    public ZMainConfiguration() {
        this.storageIcons = new HashMap<>();
        this.load = false;
    }

    @Override
    public String getFile() {
        return "config.yml";
    }

    @Override
    public void loadConfig() {
        StoragePlusPlugin plugin = JavaPlugin.getPlugin(StoragePlusPlugin.class);
        File file = new File(plugin.getDataFolder(), getFile());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Loader<MenuItemStack> loader = new MenuItemStackLoader(plugin.getInventoryManager());
        config.getConfigurationSection("storage-icons").getKeys(false).forEach(key -> {
            try {
                this.storageIcons.put(key, loader.load(config, "storage-icons." + key + ".", file));
            } catch (InventoryException e) {
                throw new RuntimeException(e);
            }
        });

        this.configuration = new DatabaseConfiguration(
                config.getString("storage-config.table-prefix"),
                config.getString("storage-config.username"),
                config.getString("storage-config.password"),
                config.getInt("storage-config.port"),
                config.getString("storage-config.host"),
                config.getString("storage-config.database"),
                this.debug,
                DatabaseType.valueOf(config.getString("storage-config.type").toUpperCase())
        );


        load = true;
    }

    @Override
    public boolean isLoad() {
        return load;
    }

    @Override
    public long getDefaultAutoSellDelay() {
        return defaultAutoSellInterval;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public PlaceholdersConfig getPlaceholders() {
        return placeholders;
    }

    @Override
    public MenuItemStack getIcon(String id) {
        return this.storageIcons.getOrDefault(id, null);
    }

    @Override
    public List<String> getCommandAliases() {
        return commandAliases;
    }

    @Override
    public String getCommandPermission() {
        return commandPermission;
    }

    @Override
    public DatabaseConfiguration getDatabaseConfiguration() {
        return this.configuration;
    }
}
