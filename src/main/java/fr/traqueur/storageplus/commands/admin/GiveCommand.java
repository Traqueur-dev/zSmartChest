package fr.traqueur.storageplus.commands.admin;

import fr.groupez.api.commands.ZCommand;
import fr.traqueur.commands.api.Arguments;
import fr.traqueur.storageplugs.api.SmartChest;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand extends ZCommand<StoragePlusPlugin> {

    private final StoragePlusManager storagePlusManager;

    public GiveCommand(StoragePlusPlugin plugin) {
        super(plugin, "give");

        this.storagePlusManager = plugin.getManager(StoragePlusManager.class);

        this.addArgs("chest", SmartChest.class);

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Player player = (Player) commandSender;
        SmartChest chest = arguments.get("chest");
        storagePlusManager.give(player, chest);
    }
}
