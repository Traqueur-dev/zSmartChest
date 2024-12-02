package fr.traqueur.storageplus.commands.admin;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.commands.ZCommand;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.messaging.Messages;
import fr.traqueur.commands.api.Arguments;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import fr.traqueur.storageplus.api.domains.ChestTemplate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand extends ZCommand<StoragePlusPlugin> {

    private final StoragePlusManager storagePlusManager;

    public GiveCommand(StoragePlusPlugin plugin) {
        super(plugin, "give");

        this.storagePlusManager = plugin.getManager(StoragePlusManager.class);

        this.setPermission(Configuration.get(MainConfiguration.class).getCommandPermission());
        this.setUsage("<color:#92bed8>/storageplus give &f<chest>");
        this.setDescription(Messages.DESCRIPTION_GIVE_COMMAND.toString());

        this.addArgs("chest", ChestTemplate.class);

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Player player = (Player) commandSender;
        ChestTemplate chest = arguments.get("chest");
        storagePlusManager.give(player, chest);
        Messages.GIVE_CHEST.send(player, Formatter.format("%chest%", chest.getName()));
    }
}
