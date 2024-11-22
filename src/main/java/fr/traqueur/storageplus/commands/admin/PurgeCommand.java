package fr.traqueur.storageplus.commands.admin;

import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.messaging.Messages;
import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.storageplus.api.StoragePlusManager;
import fr.traqueur.storageplus.api.StoragePlusPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PurgeCommand extends Command<StoragePlusPlugin> {

    private final StoragePlusManager manager;

    public PurgeCommand(StoragePlusPlugin plugin) {
        super(plugin, "purge");

        this.manager = plugin.getManager(StoragePlusManager.class);

        this.setUsage("<color:#92bed8>/storageplus purge [radius]");
        this.setDescription(Messages.DESCRIPTION_PURGE.toString());

        this.addOptionalArgs("radius", Integer.class);
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Optional<Integer> radiusOpt = arguments.getOptional("radius");
        int radius = radiusOpt.orElse(0);
        manager.purge(((Player) commandSender).getLocation().getChunk(), radius);
        int amount = (int) Math.pow((radius * 2) + 1, 2);
        Messages.PURGE.send(commandSender, Formatter.format("%amount%", amount));
    }
}
