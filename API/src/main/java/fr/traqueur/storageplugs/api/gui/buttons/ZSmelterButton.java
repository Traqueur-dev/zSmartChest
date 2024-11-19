package fr.traqueur.storageplugs.api.gui.buttons;

import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ZSmelterButton extends MaterialAuthorizedButton {

    public ZSmelterButton(StoragePlusPlugin plugin, List<Material> availableMaterials) {
        super(plugin, availableMaterials);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        this.click(inventory, this.plugin.getManager(StoragePlusManager.class)::smelt);
    }
}
