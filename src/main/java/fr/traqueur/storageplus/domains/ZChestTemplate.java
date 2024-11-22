package fr.traqueur.storageplus.domains;

import fr.maxlego08.menu.MenuItemStack;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.config.DropMode;
import fr.traqueur.storageplus.api.domains.ChestTemplate;
import fr.traqueur.storageplus.api.hooks.Hook;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ZChestTemplate implements ChestTemplate {

    private final StoragePlusPlugin plugin;
    private final String name;
    private final MenuItemStack item;
    private final DropMode dropMode;
    private final boolean infinite;
    private final int maxStackSize;

    /* Fields to manage auto sell */
    private final boolean autoSell;
    private final long sellDelay;
    private final List<Hook> shops;

    /* Fields to manage vaccum system */
    private final boolean vacuum;
    private final List<Material> blacklistVacuum;

    public ZChestTemplate(StoragePlusPlugin plugin, String name, MenuItemStack item, boolean autoSell, long sellDelay, List<Hook> shops, boolean vacuum, List<Material> blacklistVacuum, DropMode dropMode, boolean infinite, int maxStackSize) {
        this.name = name;
        this.item = item;
        this.autoSell = autoSell;
        this.sellDelay = sellDelay;
        this.plugin = plugin;
        this.shops = shops;
        this.vacuum = vacuum;
        this.blacklistVacuum = blacklistVacuum;
        this.dropMode = dropMode;
        this.infinite = infinite;
        this.maxStackSize = maxStackSize;
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
    public List<Hook> getShops() {
        return this.shops;
    }

    @Override
    public boolean isVacuum() {
        return this.vacuum;
    }

    @Override
    public List<Material> getVacuumBlacklist() {
        return this.blacklistVacuum;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DropMode getDropMode() {
        return this.dropMode;
    }

    @Override
    public boolean isInfinite() {
        return this.infinite;
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

    @Override
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

}
