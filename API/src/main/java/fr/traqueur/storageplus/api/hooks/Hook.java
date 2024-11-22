package fr.traqueur.storageplus.api.hooks;

public interface Hook {

    boolean isEnable();

    String getName();

    Class<? extends ShopProvider> getProvider();

}
