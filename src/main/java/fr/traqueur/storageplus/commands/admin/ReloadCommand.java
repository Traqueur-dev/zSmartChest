package fr.traqueur.storageplus.commands.admin;

import fr.groupez.api.commands.ZCommand;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.messaging.Messages;
import fr.traqueur.commands.api.Arguments;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends ZCommand<StoragePlusPlugin> {


    public ReloadCommand(StoragePlusPlugin plugin) {
        super(plugin, "reload");
        
        this.setDescription(Messages.DESCRIPTION_RELOAD.toString());
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        this.getPlugin().loadInventories();
        Configuration.REGISTRY.values().forEach(Configuration::load);
        this.getPlugin().loadCommands();
        Messages.RELOAD.send(commandSender);
    }
}
