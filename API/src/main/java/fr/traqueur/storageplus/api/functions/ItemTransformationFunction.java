package fr.traqueur.storageplus.api.functions;

import fr.traqueur.storageplus.api.domains.PlacedChest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@FunctionalInterface
public interface ItemTransformationFunction {

    void transfrom(PlacedChest chest, List<Material> availableMaterials, List<Integer> slots);

}
