package fr.traqueur.storageplus;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.ZLogger;
import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.sarah.MigrationManager;
import fr.traqueur.storageplus.access.ZAccessManager;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.access.AccessManager;
import fr.traqueur.storageplus.api.domains.ChestTemplate;
import fr.traqueur.storageplus.api.gui.buttons.*;
import fr.traqueur.storageplus.api.gui.buttons.access.ZAccessManagerButton;
import fr.traqueur.storageplus.api.gui.buttons.access.ZAccessModeSwitchButton;
import fr.traqueur.storageplus.api.gui.buttons.access.ZAccessUserButton;
import fr.traqueur.storageplus.api.gui.buttons.actions.ZCompressorButton;
import fr.traqueur.storageplus.api.gui.buttons.actions.ZSellAllButton;
import fr.traqueur.storageplus.api.gui.buttons.actions.ZSmelterButton;
import fr.traqueur.storageplus.api.gui.buttons.pagination.ZNextButton;
import fr.traqueur.storageplus.api.gui.buttons.pagination.ZPreviousButton;
import fr.traqueur.storageplus.api.gui.loaders.MaterialAuthorizedButtonLoader;
import fr.traqueur.storageplus.api.hooks.HooksManager;
import fr.traqueur.storageplus.api.storage.Storage;
import fr.traqueur.storageplus.commands.StoragePlusCommand;
import fr.traqueur.storageplus.commands.converters.SmartChestConverter;
import fr.traqueur.storageplus.hooks.ZStoragePlusHooksManager;
import fr.traqueur.storageplus.storage.migrations.AccessChestMigration;
import fr.traqueur.storageplus.storage.migrations.ChestContentCreateMigration;
import fr.traqueur.storageplus.storage.SQLStorage;

import java.io.File;

public final class ZStoragePlus extends StoragePlusPlugin {

    private Storage storage;
    private InventoryManager inventoryManager;
    private ButtonManager buttonManager;

    @Override
    public void enable() {
        this.buttonManager = this.getProvider(ButtonManager.class);
        this.inventoryManager = this.getProvider(InventoryManager.class);

        if(buttonManager == null || this.inventoryManager == null) {
            this.getLogger().severe("ButtonManager or InventoryManager not found.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MainConfiguration configuration = Configuration.register(MainConfiguration.class, new ZMainConfiguration());
        configuration.load();

        File folder = new File(this.getDataFolder(), "chests/");
        if(!folder.exists()) {
            folder.mkdirs();
            this.saveResource("chests/autosell_chest.yml", false);
        }

        Configuration.REGISTRY.values().forEach(config -> {
            if(!config.isLoad()) {
                config.load();
            }
        });

        this.storage = new SQLStorage(this, configuration.getDatabaseConfiguration());

        MigrationManager.setMigrationTableName(this.getName().toLowerCase() + "_migrations");
        MigrationManager.registerMigration(new ChestContentCreateMigration(StoragePlusManager.TABLE_NAME));
        MigrationManager.registerMigration(new AccessChestMigration(AccessManager.TABLE_NAME));

        this.storage.onEnable();

        try {
            this.loadMenuComponents();
        } catch (InventoryException e) {
            ZLogger.severe("An error occurred while loading the menu components.", e);
        }

        this.registerManager(AccessManager.class, new ZAccessManager());
        var manager = this.registerManager(StoragePlusManager.class, new ZStoragePlusManager());
        var hookManager = this.registerManager(HooksManager.class, new ZStoragePlusHooksManager());
        this.getScheduler().runAsync((t) -> hookManager.registerHooks());

        this.commandManager.setDebug(configuration.isDebug());
        this.commandManager.registerConverter(ChestTemplate.class, new SmartChestConverter(manager));
        this.loadCommands();
    }

    @Override
    public void disable() {
        if(this.storage != null) {
            this.getManager(AccessManager.class).saveAll();
            this.getManager(StoragePlusManager.class).saveAll();
            this.storage.onDisable();
        }
    }

    public void loadCommands() {
        var command = new StoragePlusCommand(this);
        this.commandManager.unregisterCommand(command);
        this.commandManager.registerCommand(command);
    }

    @Override
    public void loadMenuComponents() throws InventoryException {
        buttonManager.unregisters(this);
        buttonManager.register(new NoneLoader(this, ZChestContentButton.class, "ZSTORAGEPLUS_CONTENT"));
        buttonManager.register(new NoneLoader(this, ZToggleAutoSellButton.class, "ZSTORAGEPLUS_TOGGLE_AUTOSELL"));
        buttonManager.register(new NoneLoader(this, ZToggleVacuumButton.class, "ZSTORAGEPLUS_TOGGLE_VACUUM"));
        buttonManager.register(new MaterialAuthorizedButtonLoader(this, ZCompressorButton.class, "ZSTORAGEPLUS_COMPRESSOR"));
        buttonManager.register(new MaterialAuthorizedButtonLoader(this, ZSmelterButton.class, "ZSTORAGEPLUS_SMELTER"));
        buttonManager.register(new NoneLoader(this, ZNextButton.class, "ZSTORAGEPLUS_NEXT"));
        buttonManager.register(new NoneLoader(this, ZPreviousButton.class, "ZSTORAGEPLUS_PREVIOUS"));
        buttonManager.register(new NoneLoader(this, ZAccessModeSwitchButton.class, "ZSTORAGEPLUS_ACCESS_MODE_SWITCH"));
        buttonManager.register(new NoneLoader(this, ZSellAllButton.class, "ZSTORAGEPLUS_SELL_ALL"));
        buttonManager.register(new NoneLoader(this, ZAccessManagerButton.class, "ZSTORAGEPLUS_ACCESS_MANAGER"));
        buttonManager.register(new NoneLoader(this, ZAccessUserButton.class, "ZSTORAGEPLUS_USERS_ACCESS"));

        inventoryManager.deleteInventories(this);
        inventoryManager.loadInventoryOrSaveResource(this, "inventories/chest_access_manager.yml");
    }

    @Override
    public boolean isDebug() {
        return Configuration.get(MainConfiguration.class).isDebug();
    }

    @Override
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @Override
    public Storage getStorage() {
        return this.storage;
    }
}
