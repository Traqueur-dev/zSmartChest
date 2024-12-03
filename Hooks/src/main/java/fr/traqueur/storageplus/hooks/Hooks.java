package fr.traqueur.storageplus.hooks;

import fr.traqueur.storageplus.api.hooks.ShopHook;
import fr.traqueur.storageplus.api.hooks.ShopProvider;
import fr.traqueur.storageplus.hooks.providers.EconomyShopGUIProvider;
import fr.traqueur.storageplus.hooks.providers.ShopGUIPlusProvider;
import fr.traqueur.storageplus.hooks.providers.ZShopProvider;
import org.bukkit.Bukkit;

public enum Hooks implements ShopHook {

    SHOPGUIPLUS("ShopGUIPlus", ShopGUIPlusProvider.class),
    ZSHOP("ZShop",ZShopProvider.class),
    ECONOMYSHOPGUI("EconomyShopGUI", EconomyShopGUIProvider.class);


    private final String name;
    private final Class<? extends ShopProvider> clazz;

    Hooks(String name, Class<? extends ShopProvider> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public boolean isEnable() {
        return Bukkit.getPluginManager().getPlugin(this.name) != null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<? extends ShopProvider> getProvider() {
        return this.clazz;
    }
}
