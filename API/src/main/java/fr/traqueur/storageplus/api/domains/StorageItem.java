package fr.traqueur.storageplus.api.domains;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.zcore.Base64;
import fr.groupez.api.zcore.MaterialLocalization;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StorageItem {

    private ItemStack item;
    private final int slot;
    private int amount;

    public StorageItem(ItemStack item, int amount, int slot) {
        item.setAmount(1);
        this.item = item;
        this.amount = amount;
        this.slot = slot;
    }

    public ItemStack item() {
        return this.item;
    }

    public int amount() {
        return this.amount;
    }

    public int slot() {
        return this.slot;
    }

    public static StorageItem empty(int slot) {
        return new StorageItem(new ItemStack(Material.AIR), 1, slot);
    }

    public String serialize() {
        return Base64.encodeItem(this.item) + ":" + this.amount + ":" + this.slot;
    }

    public boolean isEmpty() {
        return this.item == null || this.item.getType().isAir() || this.amount == 0;
    }

    public ItemStack toItem(Player player, boolean infinite) {
        if(item == null) {
            return new ItemStack(Material.AIR);
        }

        if(infinite) {
            if(item.getType().isAir()) {
                return item;
            }

            ItemStack menuItem = this.item.clone();
            ItemMeta meta = menuItem.getItemMeta();

            MenuItemStack item = Configuration.get(MainConfiguration.class).getIcon("storage-item");

            Placeholders placeholders = new Placeholders();
            String materialName = MaterialLocalization.getTranslateName(this.item.getType());
            if(meta != null && meta.hasDisplayName()) {
                materialName = meta.getDisplayName();
            }

            placeholders.register("material_name", materialName);
            placeholders.register("amount", this.formatNumber(this.amount));
            placeholders.register("material", this.item.getType().name());

            ItemStack templateItem = item.build(player, true, placeholders);
            ItemMeta templateMeta = templateItem.getItemMeta();

            meta.setDisplayName(templateMeta.getDisplayName());

            ArrayList<String> lore = new ArrayList<>();
            if(meta.hasLore()) {
                lore.addAll(meta.getLore());
            }
            lore.addAll(templateMeta.getLore());
            meta.setLore(lore);

            menuItem.setItemMeta(meta);
            menuItem.setAmount(1);

            return menuItem;
        }

        this.item.setAmount(this.amount);
        return this.item;
    }

    private String formatNumber(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if (value >= 1_000_000_000) {
            return decimalFormat.format(value / 1_000_000_000) + "B";
        } else if (value >= 1_000_000) {
            return decimalFormat.format(value / 1_000_000) + "M";
        } else if (value >= 1_000) {
            return decimalFormat.format(value / 1_000) + "k";
        } else {
            return String.valueOf((int) value); // For numbers less than 1000
        }
    }

    public static StorageItem deserialize(String serialized) {
        String[] parts = serialized.split(":");
        return new StorageItem(Base64.decodeItem(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    public void setItem(ItemStack item) {
        item.setAmount(1);
        this.item = item;
    }

    public void addAmount(int i) {
        if(this.isEmpty()) {
            this.amount = i;
            return;
        }
        this.amount += i;
    }

    public void removeAmount(int i) {
        this.amount -= i;
        if(this.amount <= 0) {
            this.item = new ItemStack(Material.AIR);
            this.amount = 0;
        }
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
