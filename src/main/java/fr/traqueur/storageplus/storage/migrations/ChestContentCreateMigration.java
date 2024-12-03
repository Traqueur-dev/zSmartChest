package fr.traqueur.storageplus.storage.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.traqueur.storageplus.api.storage.dto.PlacedChestDTO;

public class ChestContentCreateMigration extends Migration {

    private final String table;

    public ChestContentCreateMigration(String table) {
        this.table = table;
    }

    @Override
    public void up() {
        this.create("%prefix%"+this.table, PlacedChestDTO.class);
    }
}
