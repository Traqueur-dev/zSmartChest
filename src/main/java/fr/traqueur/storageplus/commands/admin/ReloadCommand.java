package fr.traqueur.storageplus.commands.admin;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.commands.ZCommand;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.messaging.Messages;
import fr.traqueur.commands.api.Arguments;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends ZCommand<StoragePlusPlugin> {


    public ReloadCommand(StoragePlusPlugin plugin) {
        super(plugin, "reload");


        this.setPermission(Configuration.get(MainConfiguration.class).getCommandPermission());
        this.setUsage("<color:#92bed8>/storageplus reload");
        this.setDescription(Messages.DESCRIPTION_RELOAD.toString());
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Configuration.REGISTRY.values().forEach(Configuration::load);
        this.getPlugin().loadCommands();
        getPlugin().getManager(StoragePlusManager.class).registerChests();
        Messages.RELOAD.send(commandSender);
    }
}
