package fr.traqueur.storageplugs.api;

import fr.groupez.api.ZPlugin;
import fr.maxlego08.menu.api.InventoryManager;

public abstract class StoragePlusPlugin extends ZPlugin {

    public abstract InventoryManager getInventoryManager();

    public abstract void loadInventories();

    public abstract void loadCommands();

    public abstract boolean isDebug();

}
