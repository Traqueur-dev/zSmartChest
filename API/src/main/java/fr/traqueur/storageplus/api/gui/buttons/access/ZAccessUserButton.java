package fr.traqueur.storageplus.api.gui.buttons.access;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.inventory.Pagination;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.access.AccessManager;
import fr.traqueur.storageplus.api.domains.AccessChest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public class ZAccessUserButton extends ZButton implements PaginateButton {

    private final StoragePlusPlugin plugin;

    public ZAccessUserButton(Plugin plugin) {
        this.plugin = (StoragePlusPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onInventoryClose(Player player, InventoryDefault inventory) {
        super.onInventoryClose(player, inventory);
        plugin.getManager(AccessManager.class).removePending(player.getUniqueId());
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        var chest = plugin.getManager(AccessManager.class).getPending(player.getUniqueId());
        Pagination<AccessChest> pagination = new Pagination<>();
        var buttons = pagination.paginate(plugin.getManager(AccessManager.class).getAccesses(chest.getUniqueId()), this.slots.size(), inventory.getPage());
        for (int i = 0; i != Math.min(buttons.size(), this.slots.size()); i++) {
            int slot = slots.get(i);
            AccessChest value = buttons.get(i);

            inventory.addItem(slot, this.getItem(player, value)).setClick(event -> {
                if(event.getClick() == ClickType.LEFT) {
                    plugin.getManager(AccessManager.class).removeAccess(value);
                    for (Player player1 : this.plugin.getManager(StoragePlusManager.class).playerWhoOpenChest(chest)) {
                        if(player1.getUniqueId().equals(value.getPlayerId())) {
                            player1.closeInventory();
                        }
                    }
                    event.getInventory().setItem(slot, new ItemStack(Material.AIR));
                }
            });
        }
    }

    private ItemStack getItem(Player user, AccessChest value) {
        var configuration = Configuration.get(MainConfiguration.class);
        var menuItem = configuration.getIcon("user-access-chest-item");
        Placeholders placeholders = new Placeholders();
        placeholders.register("player_name", Bukkit.getOfflinePlayer(value.getPlayerId()).getName());
        ItemStack item = menuItem.build(user.getPlayer(), true, placeholders);
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(value.getPlayerId()));
            item.setItemMeta(skullMeta);
        }
        return item;
    }

    @Override
    public int getPaginationSize(Player player) {
        var chest = plugin.getManager(AccessManager.class).getPending(player.getUniqueId());
        return plugin.getManager(AccessManager.class).getAccesses(chest.getUniqueId()).size();
    }
}
