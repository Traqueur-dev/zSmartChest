package fr.traqueur.storageplus.commands;

import fr.groupez.api.commands.ZCommand;
import fr.traqueur.commands.api.Arguments;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import fr.traqueur.storageplus.commands.admin.GiveCommand;
import fr.traqueur.storageplus.commands.admin.ReloadCommand;
import org.bukkit.command.CommandSender;

public class StoragePlusCommand extends ZCommand<StoragePlusPlugin> {

    public StoragePlusCommand(StoragePlusPlugin plugin) {
        super(plugin, "storageplus");

        this.addSubCommand(new ReloadCommand(plugin), new GiveCommand(plugin));
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {

    }
}
