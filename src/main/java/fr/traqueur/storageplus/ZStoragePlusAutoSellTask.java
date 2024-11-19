package fr.traqueur.storageplus;

import fr.traqueur.storageplus.api.StoragePlusManager;

public class ZStoragePlusAutoSellTask implements Runnable {

    private final StoragePlusManager manager;

    public ZStoragePlusAutoSellTask(StoragePlusManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        this.manager.handleAutoSell();
    }
}
