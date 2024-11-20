package fr.traqueur.storageplus.api;

import fr.groupez.api.ZPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.traqueur.storageplus.api.storage.Storage;

public abstract class StoragePlusPlugin extends ZPlugin {

    public abstract InventoryManager getInventoryManager();

    public abstract Storage getStorage();

    public abstract void loadCommands();

    public abstract boolean isDebug();

}
