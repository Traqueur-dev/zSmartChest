package fr.traqueur.storageplugs.api.functions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@FunctionalInterface
public interface ItemTransformationFunction {

    List<ItemStack> transfrom(List<ItemStack> items, List<Material> availableMaterials);

}
