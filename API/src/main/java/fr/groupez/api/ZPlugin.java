package fr.groupez.api;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import fr.groupez.api.commands.CommandsHandler;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.messaging.MessageLoader;
import fr.groupez.api.placeholder.Placeholders;
import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.commands.api.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ZPlugin extends JavaPlugin {

    protected CommandManager commandManager;
    protected PlatformScheduler scheduler;

    @Override
    public void onEnable() {

        long enableTime = System.currentTimeMillis();
        ZLogger.info("<yellow>=== ENABLE START ===");
        ZLogger.info("Plugin Version V&c" + getDescription().getVersion());

        this.scheduler = new FoliaLib(this).getScheduler();

        this.getDataFolder().mkdirs();

        MessageLoader loader = Configuration.register(MessageLoader.class, new MessageLoader());
        loader.loadConfig();

        ZConfiguration configuration = Configuration.register(ZConfiguration.class, new ZConfiguration());
        configuration.loadConfig();

        this.commandManager = new CommandManager(this);
        this.commandManager.setMessageHandler(new CommandsHandler());
        this.commandManager.setLogger(new Logger() {
            @Override
            public void error(String s) {
                ZLogger.severe(s);
            }

            @Override
            public void info(String s) {
                ZLogger.info(s);
            }
        });

        Placeholders.load(this);

        this.enable();

        ZLogger.info("<yellow>=== ENABLE DONE &7(&6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms&7) <yellow>===");

    }

    @Override
    public void onDisable() {

        long enableTime = System.currentTimeMillis();
        ZLogger.info("<yellow>=== DISABLE START ===");

        this.disable();

        ZLogger.info("<yellow>=== DISABLE DONE &7(&6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms&7) <yellow>===");

    }

    public abstract void enable();

    public abstract void disable();

    public PlatformScheduler getScheduler() {
        return scheduler;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
