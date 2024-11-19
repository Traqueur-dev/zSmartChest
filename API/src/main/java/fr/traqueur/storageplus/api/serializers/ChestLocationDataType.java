package fr.traqueur.storageplus.api.serializers;

import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.PlacedChest;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ChestLocationDataType implements PersistentDataType<String, PlacedChest> {

    public static final ChestLocationDataType INSTANCE = new ChestLocationDataType();

    private StoragePlusManager manager = JavaPlugin.getPlugin(StoragePlusPlugin.class).getManager(StoragePlusManager.class);

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<PlacedChest> getComplexType() {
        return PlacedChest.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull PlacedChest chestLocation, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return chestLocation.serialize();
    }

    @NotNull
    @Override
    public PlacedChest fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return manager.deserializeChest(s);
    }
}
