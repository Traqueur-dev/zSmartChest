package fr.groupez.api;

import fr.groupez.api.configurations.Configuration;
import fr.traqueur.storageplugs.api.config.PlaceholdersConfig;

public interface MainConfiguration extends Configuration {

    long getDefaultAutoSellDelay();

    boolean isDebug();

    PlaceholdersConfig getPlaceholders();
}
