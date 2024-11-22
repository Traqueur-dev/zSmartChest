package fr.traqueur.storageplus.hooks;

import fr.groupez.api.ZLogger;
import fr.traqueur.storageplus.api.hooks.Hook;
import fr.traqueur.storageplus.api.hooks.HooksManager;
import fr.traqueur.storageplus.api.hooks.ShopProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ZStoragePlusHooksManager implements HooksManager {

    private final Map<Hook, ShopProvider> providers;

    public ZStoragePlusHooksManager() {
        this.providers = new HashMap<>();
    }

    @Override
    public void registerHooks() {
        for (Hooks value : Hooks.values()) {
            if(value.isEnable()) {
                try {
                    ShopProvider provider = value.getProvider().getConstructor().newInstance();
                    this.providers.put(value, provider);
                    if(this.getPlugin().isDebug()) {
                        ZLogger.info("Hook " + value.name() + " registered.");
                    }
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Optional<ShopProvider> getProvider(Hook hook) {
        return Optional.ofNullable(this.providers.get(hook));
    }

}
