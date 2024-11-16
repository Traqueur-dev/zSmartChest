package fr.groupez.api;

import fr.groupez.api.configurations.Configuration;

public interface MainConfiguration extends Configuration {

    long getDefaultAutoSellDelay();

    boolean isDebug();

}
