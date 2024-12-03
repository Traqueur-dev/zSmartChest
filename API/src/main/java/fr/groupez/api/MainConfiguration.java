package fr.groupez.api;

import fr.groupez.api.configurations.Configuration;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.traqueur.storageplus.api.config.AccessManagingMode;
import fr.traqueur.storageplus.api.config.PlaceholdersConfig;

import java.util.List;

public interface MainConfiguration extends Configuration {

    long getDefaultAutoSellDelay();

    boolean isDebug();

    PlaceholdersConfig getPlaceholders();

    MenuItemStack getIcon(String id);

    List<String> getCommandAliases();

    String getCommandPermission();

    AccessManagingMode getAccessManagingMode();

    List<String> getAccessManagingCancelWords();

    DatabaseConfiguration getDatabaseConfiguration();
}
