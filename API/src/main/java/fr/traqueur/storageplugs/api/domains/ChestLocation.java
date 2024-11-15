package fr.traqueur.storageplugs.api.domains;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public record ChestLocation(Location location, String chestName) {

    public static ChestLocation deserialize(String string) {
        String[] parts = string.split(";");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid serialized location: " + string);
        }
        String worldName = parts[0];
        World world = worldName.equals("null") ? null : Bukkit.getWorld(UUID.fromString(worldName));
        return new ChestLocation(new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])), parts[4]);
    }

    public String serialize() {
        return (this.location.getWorld() == null ? "null" : this.location.getWorld().getUID()) + ";" + this.location.getBlockX() + ";" + this.location.getBlockY() + ";" + this.location.getBlockZ() + ";" + this.chestName;
    }
}
