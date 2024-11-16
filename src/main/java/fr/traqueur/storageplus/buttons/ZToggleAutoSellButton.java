package fr.traqueur.storageplus.buttons;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import fr.traqueur.storageplugs.api.config.PlaceholdersConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ZToggleAutoSellButton extends ZButton {

    private final StoragePlusPlugin plugin;

    public ZToggleAutoSellButton(Plugin plugin) {
        this.plugin = ((StoragePlusPlugin) plugin);
    }

    @Override
    public ItemStack getCustomItemStack(Player player) {

        Placeholders placeholders = new Placeholders();
        PlaceholdersConfig config = Configuration.get(MainConfiguration.class).getPlaceholders();
        String autoSellState = this.plugin
                .getManager(StoragePlusManager.class)
                .getOpenedChest(player)
                .isAutoSell() ? config.autoSellStateOn() : config.autoSellStateOff();

        placeholders.register("autosell_state", autoSellState);

        return this.getItemStack().build(player, true, placeholders);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        var manager = this.plugin.getManager(StoragePlusManager.class);
        var chest = manager.getOpenedChest(player);
        chest.setAutoSell(!chest.isAutoSell());
    }
}
