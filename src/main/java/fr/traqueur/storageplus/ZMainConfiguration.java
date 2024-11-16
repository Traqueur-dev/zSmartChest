package fr.traqueur.storageplus;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.NonLoadable;
import fr.traqueur.storageplugs.api.config.PlaceholdersConfig;

public class ZMainConfiguration implements MainConfiguration {

    @NonLoadable
    private boolean load;

    private boolean debug;

    private long defaultAutoSellInterval;

    private PlaceholdersConfig placeholders;

    public ZMainConfiguration() {
        this.load = false;
    }

    @Override
    public String getFile() {
        return "config.yml";
    }

    @Override
    public void loadConfig() {
        load = true;
    }

    @Override
    public boolean isLoad() {
        return load;
    }

    @Override
    public long getDefaultAutoSellDelay() {
        return defaultAutoSellInterval;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public PlaceholdersConfig getPlaceholders() {
        return placeholders;
    }
}
