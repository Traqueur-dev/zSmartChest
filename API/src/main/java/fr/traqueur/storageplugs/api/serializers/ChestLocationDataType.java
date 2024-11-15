package fr.traqueur.storageplugs.api.serializers;

import fr.traqueur.storageplugs.api.domains.ChestLocation;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ChestLocationDataType implements PersistentDataType<String, ChestLocation> {

    public static final ChestLocationDataType INSTANCE = new ChestLocationDataType();

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<ChestLocation> getComplexType() {
        return ChestLocation.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull ChestLocation chestLocation, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return chestLocation.serialize();
    }

    @NotNull
    @Override
    public ChestLocation fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return ChestLocation.deserialize(s);
    }
}
