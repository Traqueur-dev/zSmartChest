package fr.groupez.api;

import org.bukkit.plugin.java.JavaPlugin;

public interface Manager<T extends ZPlugin> {

    default T getPlugin() {
        return (T) JavaPlugin.getPlugin(ZPlugin.class);
    }
}
