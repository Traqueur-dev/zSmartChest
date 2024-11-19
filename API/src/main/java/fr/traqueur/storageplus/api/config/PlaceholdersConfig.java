package fr.traqueur.storageplus.api.config;

import fr.groupez.api.configurations.Loadable;

public record PlaceholdersConfig(String stateOn, String stateOff) implements Loadable {

}
