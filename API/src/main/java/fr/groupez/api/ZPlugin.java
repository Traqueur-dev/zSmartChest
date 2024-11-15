package fr.groupez.api;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import fr.groupez.api.commands.CommandsHandler;
import fr.groupez.api.placeholder.Placeholders;
import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.commands.api.logging.Logger;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;

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

    public void saveResource(String resourcePath, String toPath, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.getFile());
            } else {
                File outFile = new File(getDataFolder(), toPath);
                int lastIndex = toPath.lastIndexOf(47);
                File outDir = new File(getDataFolder(), toPath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = Files.newOutputStream(outFile.toPath());
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException exception) {
                    getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, exception);
                }

            }
        } else throw new IllegalArgumentException("ResourcePath cannot be null or empty");
    }

    public <I extends Manager<? extends ZPlugin>> I getManager(Class<I> clazz) {
        return getProvider(clazz);
    }

    public <I extends Manager<? extends ZPlugin>, T extends I> void registerManager(Class<I> clazz, T manager) {
        getServer().getServicesManager().register(clazz, manager, this, org.bukkit.plugin.ServicePriority.Normal);
    }

    protected <T> T getProvider(Class<T> clazz) {
        RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(clazz);
        if (provider == null) {
            return null;
        } else {
            return provider.getProvider();
        }
    }
}
