package fr.traqueur.storageplugs.api.config;

import fr.groupez.api.configurations.Loadable;

public record PlaceholdersConfig(String autoSellStateOn, String autoSellStateOff) implements Loadable {

}
