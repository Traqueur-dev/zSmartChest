package fr.traqueur.storageplus.domains;

import fr.maxlego08.menu.MenuItemStack;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import fr.traqueur.storageplugs.api.domains.ChestTemplate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ZChestTemplate implements ChestTemplate {

    private final StoragePlusPlugin plugin;
    private final String name;
    private final MenuItemStack item;
    private final boolean autoSell;
    private final long sellDelay;

    public ZChestTemplate(StoragePlusPlugin plugin, String name, MenuItemStack item, boolean autoSell, long sellDelay) {
        this.name = name;
        this.item = item;
        this.autoSell = autoSell;
        this.sellDelay = sellDelay;
        this.plugin = plugin;
    }

    @Override
    public long getSellDelay() {
        return this.sellDelay;
    }

    @Override
    public boolean isAutoSell() {
        return this.autoSell;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void open(StoragePlusPlugin plugin, Player player) {
        plugin.getInventoryManager().openInventory(player, this.name);
    }

    @Override
    public ItemStack build(Player player) {
        ItemStack itemStack =  this.item.build(player);
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(plugin.getManager(StoragePlusManager.class).getNamespaceKey(),
                PersistentDataType.STRING, this.name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
