package fr.traqueur.storageplus.api.hooks;

public interface ShopHook {

    boolean isEnable();

    String getName();

    Class<? extends ShopProvider> getProvider();

}
