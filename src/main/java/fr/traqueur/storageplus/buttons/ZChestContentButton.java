package fr.traqueur.storageplus.buttons;

import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ZChestContentButton extends ZButton implements PaginateButton {

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        displayItems(player, inventory);
    }

    private void displayItems(Player player, InventoryDefault inventory) {
        for (int i = 0; i != this.slots.size(); i++) {
            int slot = slots.get(i);
            inventory.addItem(slot, new ItemStack(Material.AIR)).setClick(event -> {

            });
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        return 0;
    }
}
