package fr.traqueur.storageplus;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import fr.traqueur.storageplus.commands.StoragePlusCommand;

public final class ZStoragePlus extends StoragePlusPlugin {

    private InventoryManager inventoryManager;

    @Override
    public void enable() {

        MainConfiguration configuration = Configuration.register(MainConfiguration.class, new ZMainConfiguration());
        configuration.loadConfig();

        ButtonManager buttonManager = this.getProvider(ButtonManager.class);
        this.inventoryManager = this.getProvider(InventoryManager.class);

        if(buttonManager == null || this.inventoryManager == null) {
            this.getLogger().severe("ButtonManager or InventoryManager not found.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        buttonManager.unregisters(this);

        this.loadInventories();

        Configuration.REGISTRY.values().forEach(config -> {
            if(!config.isLoad()) {
                config.load();
            }
        });

        this.registerManager(StoragePlusManager.class, new ZStoragePlusManager());

        this.loadCommands();
    }

    @Override
    public void disable() {

    }

    public void loadCommands() {
        var command = new StoragePlusCommand(this);
        this.commandManager.unregisterCommand(command);
        this.commandManager.registerCommand(command);
    }

    public void loadInventories() {
        this.inventoryManager.deleteInventories(this);
        try {
            this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/smartchest.yml");
        } catch (InventoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
}
