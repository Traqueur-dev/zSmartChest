package fr.traqueur.storageplus;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.database.Migration;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.ChestTemplate;
import fr.traqueur.storageplus.api.gui.buttons.*;
import fr.traqueur.storageplus.api.gui.loaders.MaterialAuthorizedButtonLoader;
import fr.traqueur.storageplus.api.hooks.HooksManager;
import fr.traqueur.storageplus.api.storage.Storage;
import fr.traqueur.storageplus.commands.StoragePlusCommand;
import fr.traqueur.storageplus.commands.converters.SmartChestConverter;
import fr.traqueur.storageplus.hooks.ZStoragePlusHooksManager;
import fr.traqueur.storageplus.storage.ChestContentCreateMigration;
import fr.traqueur.storageplus.storage.SQLStorage;

import java.io.File;

public final class ZStoragePlus extends StoragePlusPlugin {

    private Storage storage;
    private InventoryManager inventoryManager;

    @Override
    public void enable() {
        ButtonManager buttonManager = this.getProvider(ButtonManager.class);
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

        buttonManager.unregisters(this);
        buttonManager.register(new NoneLoader(this, ZChestContentButton.class, "ZSTORAGEPLUS_CONTENT"));
        buttonManager.register(new NoneLoader(this, ZToggleAutoSellButton.class, "ZSTORAGEPLUS_TOGGLE_AUTOSELL"));
        buttonManager.register(new NoneLoader(this, ZToggleVacuumButton.class, "ZSTORAGEPLUS_TOGGLE_VACUUM"));
        buttonManager.register(new MaterialAuthorizedButtonLoader(this, ZCompressorButton.class, "ZSTORAGEPLUS_COMPRESSOR"));
        buttonManager.register(new MaterialAuthorizedButtonLoader(this, ZSmelterButton.class, "ZSTORAGEPLUS_SMELTER"));

        MigrationManager.registerMigration(new ChestContentCreateMigration(StoragePlusManager.TABLE_NAME));

        this.storage.onEnable();

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
