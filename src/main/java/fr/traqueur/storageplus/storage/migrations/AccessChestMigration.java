package fr.traqueur.storageplus.storage.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.traqueur.storageplus.api.storage.dto.AccessChestDTO;

public class AccessChestMigration extends Migration {

    private final String table;

    public AccessChestMigration(String table) {
        this.table = table;
    }

    @Override
    public void up() {
        this.create("%prefix%"+this.table, AccessChestDTO.class);
    }
}
