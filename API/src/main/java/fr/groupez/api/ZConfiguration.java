package fr.groupez.api;

import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.configurations.NonLoadable;

public class ZConfiguration implements Configuration {

    @NonLoadable
    private boolean load;

    private boolean debug;

    public ZConfiguration() {
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

    public boolean isDebug() {
        return debug;
    }
}
