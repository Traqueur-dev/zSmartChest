package fr.traqueur.storageplus.api.gui.buttons;

import fr.groupez.api.messaging.Messages;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.currencies.Currencies;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import fr.traqueur.storageplus.api.functions.ItemTransformationFunction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class MaterialAuthorizedButton extends ZButton {

    protected final StoragePlusPlugin plugin;
    protected final List<Material> availableMaterials;
    protected final boolean free;
    protected final double amount;
    protected final Currencies currency;
    protected final String currencyName;

    public MaterialAuthorizedButton(StoragePlusPlugin plugin, List<Material> availableMaterials, double amount, Currencies currency, String currencyName) {
        this.plugin = plugin;
        this.availableMaterials = availableMaterials;
        this.free = amount == 0;
        this.amount = amount;
        this.currency = currency;
        this.currencyName = currencyName;
    }

    protected void click(Player player, InventoryDefault inventory, ItemTransformationFunction function) {
        if(!this.free) {
            BigDecimal wallet = currency.getBalance(player, this.currencyName);
            if(wallet.compareTo(new BigDecimal(this.amount)) < 0) {
                Messages.NOT_ENOUGH_MONEY.send(player);
                return;
            }
            currency.withdraw(player, new BigDecimal(this.amount), this.currencyName);
        }
        var button = inventory.getButtons().stream().filter(buttonInner -> buttonInner instanceof ZChestContentButton).map(buttonInner -> (ZChestContentButton) buttonInner).findFirst();
        if(button.isEmpty()) {
            return;
        }
        PlacedChest chest = plugin.getManager(StoragePlusManager.class).getOpenedChest(player);
        function.transfrom(chest, availableMaterials, new ArrayList<>(button.get().getSlots()));
        button.get().onRender(player, inventory);
    }

}
