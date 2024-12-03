package fr.traqueur.storageplus.api.hooks;

import fr.traqueur.storageplus.api.Manager;

import java.util.List;
import java.util.Optional;

public interface HooksManager extends Manager {
    void registerHooks();

    Optional<ShopProvider> getProvider(ShopHook hook);

    List<ShopHook> getHooks();
}
