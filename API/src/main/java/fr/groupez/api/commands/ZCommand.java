package fr.groupez.api.commands;

import fr.traqueur.commands.api.Command;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ZCommand<T extends JavaPlugin> extends Command<T> {

    public ZCommand(T plugin, String name) {
        super(plugin, name);
    }

    public void setPermission(Permission permission) {
        super.setPermission(permission.getPermission());
    }
}
